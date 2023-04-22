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

package me.mrfunny.liquidaddons.util

import me.mrfunny.fairplayclient.util.CappedHashMap
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.Minecraft
import net.minecraft.network.NetworkManager
import java.util.UUID

object ConstantPool {
    @JvmField
    val allowedModIds: List<String> = listOf(
        "fml",
        "forge",
        "mcp",
        "optifine",
        "essential",
        "patcher"
    )
    @JvmField
    val modIdsToAdd: Map<String, String> = mapOf(
        "skyblockaddons" to "3.0.2-DEV",
        "keystrokes" to "5.1",
        "dungeonfinder" to "2.4"
    )

    const val legitDistance: Double = 3.0
    @JvmField val reachData: Map<UUID, Float> = CappedHashMap(100);
    @JvmField val fairplayModeEnabled = BoolValue("Fairplay Mode", true)
    @JvmField val changeVelocity = BoolValue("FM - Change velocity", true)
    @JvmField val maxReachDistance = FloatValue("FM - Max Reach", 3.7f, 3.1f, 5f)
    @JvmField val reachAddition = FloatValue("FM - Reach addition", 0.1f, 0f, 3f)
    @JvmField val minVelocity = IntegerValue("FM - Min Velocity", 90, 100, 0)
    @JvmStatic var currentNetworkManager: NetworkManager? = null
        get() {
            if(field == null) {
                return Minecraft.getMinecraft().netHandler.networkManager
            }
            return field
        }
}