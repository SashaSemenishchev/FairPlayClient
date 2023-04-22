/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Copyright © SashaSemenishchev 2023
 * Contact: sashasemenishchev@protonmail.com
 */

package me.mrfunny.lunarspoof.websocket.auth

import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import me.mrfunny.lunarspoof.websocket.auth.packet.AuthenticatorPacket
import me.mrfunny.lunarspoof.websocket.auth.packet.impl.CPacketEncryptionResponse
import me.mrfunny.lunarspoof.websocket.auth.packet.impl.SPacketAuthenticatedRequest
import me.mrfunny.lunarspoof.websocket.auth.packet.impl.SPacketEncryptionRequest
import gg.essential.api.utils.JsonHolder
import gg.essential.api.utils.Multithreading
import net.minecraft.util.CryptManager
import net.minecraft.util.Session
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.math.BigInteger
import java.net.Proxy
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

class LunarAuthWebSocket(
    httpHeaders: Map<String, String>,
    private val session: Session,
    private val consumer: (String?) -> Unit
) : WebSocketClient(
    URI("wss://authenticator.lunarclientprod.com"),
    Draft_6455(),
    httpHeaders,
    30000
) {
    private var authenticated = false
    override fun onOpen(handshakedata: ServerHandshake) {
        LOGGER.info("Connected.")
        attemts++
    }

    override fun onMessage(message: String) {}
    override fun onMessage(bytes: ByteBuffer) {
        processMessage(JsonHolder(String(bytes.array())))
    }

    fun processMessage(json: JsonHolder) {
        val packet: AuthenticatorPacket
        val packetType = json.optString("packetType")
        packet = when (packetType) {
            "SPacketEncryptionRequest" -> SPacketEncryptionRequest()
            "SPacketAuthenticatedRequest" -> SPacketAuthenticatedRequest()
            else -> return
        }
        packet.processJson(json)
        packet.process(this)
        LOGGER.info("Processed Packet: " + packet.name)
    }

    fun acceptEncryption(packet: SPacketEncryptionRequest) {
        LOGGER.info("Accepting Encryption")
        val secretKey = CryptManager.createNewSharedKey()
        val publicKey = packet.key
        val keyHash = CryptManager.getServerIdHash("", publicKey, secretKey) ?: return
        val keyHashHex = BigInteger(keyHash).toString(16)
        try {
            val session = YggdrasilAuthenticationService(
                Proxy.NO_PROXY,
                UUID.randomUUID().toString()
            ).createMinecraftSessionService()
            session.joinServer(this.session.profile, this.session.token, keyHashHex)
        } catch (ex: AuthenticationException) {
            ex.printStackTrace()
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
            close()
        }
        sendPacket(CPacketEncryptionResponse(secretKey, publicKey, packet.bytes))
        Multithreading.schedule({
            sendPacket(CPacketEncryptionResponse(secretKey, publicKey, packet.bytes))
        }, 1, TimeUnit.SECONDS)
    }

    fun acceptAuthentication(packet: SPacketAuthenticatedRequest) {
        authenticated = true
        close()
        consumer(packet.jwtKey)
    }

    fun sendPacket(packet: AuthenticatorPacket) {
        if (!isOpen) return
        LOGGER.info("Sending Packet: " + packet.name)
        val json = JsonHolder()
        json.put("packetType", packet.name)
        packet.processJson(json)
        println("Sending: $json")
        send(json.toString().toByteArray(StandardCharsets.UTF_8))
    }

    private var attemts = 0;

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        if (code == 1000) {
            LOGGER.info("Authentication Succeeded.")
        }
        LOGGER.info(String.format("Connection Closed (%d, \"%s\")", code, reason))
        if(reason == "Timed out while waiting for encryption response" && attemts < 4) {
            Multithreading.runAsync {
                reconnectBlocking()
            }
        }
        if (authenticated) return
        consumer(null)
    }

    override fun onError(e: Exception) {
        e.printStackTrace()
    }

    companion object {
        val LOGGER: Logger = LogManager.getLogger("LS Auth")
    }
}