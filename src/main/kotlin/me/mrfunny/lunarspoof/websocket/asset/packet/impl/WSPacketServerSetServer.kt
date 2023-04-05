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

package me.mrfunny.lunarspoof.websocket.asset.packet.impl

import me.mrfunny.lunarspoof.websocket.asset.LunarAssetWebSocket
import me.mrfunny.lunarspoof.websocket.asset.packet.AbstractWebSocketPacket
import net.minecraft.network.PacketBuffer

class WSPacketServerSetServer(private var server: String) : AbstractWebSocketPacket() {
    override fun write(buf: PacketBuffer) {
        buf.writeInt(6)
        buf.writeString("") // empty uuid string
        buf.writeString(server)
    }

    override fun read(buf: PacketBuffer) {
    }

    override fun handle(socket: LunarAssetWebSocket) {
        println("Friend joined server")
    }
}