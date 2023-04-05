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

package me.mrfunny.lunarspoof.websocket.asset.packet

import me.mrfunny.lunarspoof.utils.createHashBiMap
import me.mrfunny.lunarspoof.websocket.asset.LunarAssetWebSocket
import me.mrfunny.lunarspoof.websocket.asset.packet.impl.WSPacketClientPlayerInfo
import net.minecraft.network.PacketBuffer

abstract class AbstractWebSocketPacket {
    protected val LOGGER = LunarAssetWebSocket.LOGGER

    abstract fun write(buf: PacketBuffer)
    abstract fun read(buf: PacketBuffer)
    abstract fun handle(socket: LunarAssetWebSocket)

    fun writeKey(buf: PacketBuffer, array: ByteArray) {
        buf.writeShort(array.size)
        buf.writeBytes(array)
    }

    fun readKey(buf: PacketBuffer): ByteArray {
        val key = buf.readShort()
        if (key < 0) {
            LOGGER.error("Invalid key!")
            return ByteArray(0)
        }
        val data = ByteArray(key.toInt())
        buf.readBytes(data)
        return data
    }

    companion object {
        val REGISTRY = createHashBiMap(
            WSPacketClientPlayerInfo::class.java to 8
        )
    }
}