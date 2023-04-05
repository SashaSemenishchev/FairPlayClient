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

package me.mrfunny.lunarspoof.websocket.asset

import me.mrfunny.lunarspoof.LunarSpoof
import gg.essential.api.utils.Multithreading
import me.mrfunny.lunarspoof.feature.indicator.LunarNameTagIcon
import me.mrfunny.lunarspoof.websocket.asset.packet.AbstractWebSocketPacket
import me.mrfunny.lunarspoof.websocket.asset.packet.impl.WSPacketClientPlayerInfo
import me.mrfunny.lunarspoof.websocket.asset.packet.impl.WSPacketServerPlayerInfoRequest
import io.netty.buffer.Unpooled
import kotlinx.coroutines.sync.Mutex
import net.minecraft.network.PacketBuffer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer
import java.util.UUID

@Suppress("unstable")
class LunarAssetWebSocket(
    httpHeaders: Map<String, String>
) : WebSocketClient(
    URI("wss://assetserver.lunarclientprod.com/connect"),
    Draft_6455(),
    httpHeaders,
    30000
) {
    override fun onOpen(handshakedata: ServerHandshake) {
        LOGGER.info("Connection Opened.")
    }

    var closedByMethod: Boolean = false
    private val closedMutex: Any = Object()

    override fun onMessage(message: String) {}
    override fun onMessage(bytes: ByteBuffer) {
        processPacketBuffer(PacketBuffer(Unpooled.wrappedBuffer(bytes.array())))
    }

    private fun processPacketBuffer(buf: PacketBuffer) {
        try {
            val packetId = buf.readVarIntFromBuffer()
            val clazz = AbstractWebSocketPacket.REGISTRY.inverse()[packetId]
            println("Packet: $packetId")
            try {
                if (clazz == null) LOGGER.error("Unknown packet ID: $packetId")
                val packet = clazz?.getDeclaredConstructor()?.newInstance() ?: return
                LOGGER.debug("Received: ${clazz.simpleName}")
                packet.read(buf)
                packet.handle(this)
            } catch (e: Exception) {
                LOGGER.error("Error from: $clazz")
                e.printStackTrace()
            }
        } catch (e: Exception) {
            LOGGER.error("Top level parsing exception: ")
            e.printStackTrace()
        }

    }

    fun requestPlayerInfo(uuid: UUID) {
        sendPacket(WSPacketServerPlayerInfoRequest(listOf(uuid)))
    }

    fun requestPlayerInfo(uuids: List<UUID>) {
        sendPacket(WSPacketServerPlayerInfoRequest(uuids))
    }

    fun sendPacket(packet: AbstractWebSocketPacket) {
        if (!this.isOpen) return
        val packetBuffer = PacketBuffer(Unpooled.buffer())
        packet.write(packetBuffer)
        val data = ByteArray(packetBuffer.readableBytes())
        packetBuffer.readBytes(data)
        packetBuffer.release()
        this.send(data)
        LOGGER.debug("Sent: ${packet::class.simpleName}")
    }

    fun processPlayer(packet: WSPacketClientPlayerInfo) {
        LOGGER.info("PLAYER: ${packet.playerId}")
        LunarSpoof.lunarUsers[packet.playerId] = LunarNameTagIcon(packet.color, packet.bl)
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        LOGGER.info(String.format("Connection Closed (%d, \"%s\")", code, reason))
        if(!closedByMethod) {
            LOGGER.info("trying to reconnect")
            Multithreading.runAsync {
                reconnectBlocking()
            }
        }
    }

    override fun onError(e: Exception) {
        e.printStackTrace()
    }

    override fun close() {
        synchronized(closedMutex) {
            closedByMethod = true
            super.close()
            closedByMethod = false
        }
    }

    companion object {
        val LOGGER: Logger = LogManager.getLogger("LS Assets")
    }
}
