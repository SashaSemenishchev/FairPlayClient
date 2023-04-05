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

package me.mrfunny.lunarspoof.websocket.auth.packet.impl

import me.mrfunny.lunarspoof.websocket.auth.LunarAuthWebSocket
import me.mrfunny.lunarspoof.websocket.auth.packet.AuthenticatorPacket
import gg.essential.api.utils.JsonHolder
import net.minecraft.util.CryptManager
import java.security.PublicKey
import java.util.*

class SPacketEncryptionRequest : AuthenticatorPacket() {
    override val name: String = "SPacketEncryptionRequest"

    lateinit var key: PublicKey
        private set
    lateinit var bytes: ByteArray
        private set

    override fun process(ws: LunarAuthWebSocket) {
        ws.acceptEncryption(this)
    }

    override fun processJson(json: JsonHolder) {
        val decoder = Base64.getUrlDecoder()
        key = CryptManager.decodePublicKey(decoder.decode(json.optString("publicKey")))
        bytes = decoder.decode(json.optString("randomBytes"))
    }
}