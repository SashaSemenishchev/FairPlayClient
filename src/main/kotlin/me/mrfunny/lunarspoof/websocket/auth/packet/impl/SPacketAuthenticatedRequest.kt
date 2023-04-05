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

class SPacketAuthenticatedRequest : AuthenticatorPacket() {
    override val name: String = "SPacketAuthenticatedRequest"
    lateinit var jwtKey: String
        private set

    override fun process(ws: LunarAuthWebSocket) {
        ws.acceptAuthentication(this)
    }

    override fun processJson(json: JsonHolder) {
        this.jwtKey = json.optString("jwtKey")
    }
}