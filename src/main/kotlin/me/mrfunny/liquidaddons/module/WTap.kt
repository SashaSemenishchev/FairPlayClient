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

package me.mrfunny.liquidaddons.module

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo("W-Tap", "W taps to increase knockback", ModuleCategory.COMBAT)
object WTap : Module() {

    private var tick: Int = 0
    private var tapping: Boolean = false

    @EventTarget
    fun onUpdate(event: TickEvent) {
        if(!tapping) return
        if(tick == 4) {
            mc.thePlayer.isSprinting = true
            tapping = false
        }
        tick++
    }
    @EventTarget
    fun onAttack(event: AttackEvent) {
        val player = mc.thePlayer
        if(player.isSprinting && !tapping) {
            player.isSprinting = true
            tapping = true
            tick = 0
        }
    }
}