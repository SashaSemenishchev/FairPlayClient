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

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import kotlin.math.PI
import kotlin.math.atan2

object Raytracer {
    @JvmStatic
    fun EntityPlayerSP.getMotionYaw(): Double {
        val motX = this.motionX
        val motZ = this.motionZ
        var yaw = if (motZ != .0) {
            -atan2(motX, motZ) * 180 / PI
        } else {
            .0
        }
        if (yaw < .0) {
            yaw += 360;
        }

        return MathHelper.wrapAngleTo180_double(yaw + 180)
    }
}