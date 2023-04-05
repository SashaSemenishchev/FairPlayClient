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

package me.mrfunny.liquidaddons

import me.mrfunny.fairplayclient.FairPlayClient
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.misc.AtAllProvider
import net.ccbluex.liquidbounce.features.module.modules.misc.NoRotateSet
import net.ccbluex.liquidbounce.features.module.modules.misc.Spammer
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.movement.LiquidWalk
import net.ccbluex.liquidbounce.features.module.modules.movement.Sprint
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import org.lwjgl.input.Keyboard

@ModuleInfo("Dangerous Mod Blocker", "Blocks enablation of dangerous mods", ModuleCategory.MISC, Keyboard.KEY_BACKSLASH)
class DangerousModBlocker : Module() {
    companion object {
        @JvmStatic
        @get:JvmName("getInstance")
        val INSTANCE = DangerousModBlocker()

        @JvmStatic
        fun isBad(module: Module): Boolean {
            val category: ModuleCategory = module.category
            if (category == ModuleCategory.EXPLOIT || category == ModuleCategory.WORLD) {
                return true
            }
            if (category == ModuleCategory.MOVEMENT) {
                if (module is Sprint) {
                    (LiquidBounce.moduleManager[Sprint::class.java] as Sprint).modeValue.set("Legit")
                    return false
                }
                return true
            }
            for (dangerousMod in INSTANCE.dangerousMods) {
                if (!dangerousMod.isAssignableFrom(module::class.java)) continue
                return true
            }
            return false
        }
    }

    val dangerousMods: List<Class<out Module>> = listOf(
        Fly::class.java,
        KillAura::class.java,
        Ignite::class.java,
        FastBow::class.java,
        SuperKnockback::class.java,
        TeleportHit::class.java,
        AntiCactus::class.java,
        Blink::class.java,
        Regen::class.java,
        NoFall::class.java,
        KeepAlive::class.java,
        Zoot::class.java,
        Eagle::class.java,
        FreeCam::class.java,
        Backtrack::class.java,
        FastUse::class.java,
        PotionSaver::class.java,
        Spammer::class.java,
        NoRotateSet::class.java,
        AtAllProvider::class.java,
        LiquidWalk::class.java
    )

    override fun onEnable() {
        super.onEnable()
        var disabled = 0
        for (module in LiquidBounce.moduleManager.modules) {
            if(!module.state) continue
            if(isBad(module)) {
                module.state = false
                disabled++;
            }
        }

        if(disabled != 0) {
            FairPlayClient.sendWarning("§aDisabled $disabled dangerous modules")
        }
    }
}