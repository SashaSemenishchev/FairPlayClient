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

package me.mrfunny.fairplayclient.mixins;

import me.mrfunny.liquidaddons.util.ConstantPool;
import me.mrfunny.fairplayclient.FairPlayClient;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.player.Reach;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerController {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void handleEntityAttack(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
        if(targetEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) targetEntity;
            Reach module = (Reach) LiquidBounce.moduleManager.get(Reach.class);
            if(!module.getState() || !ConstantPool.fairplayModeEnabled.get()) return;
            Float reachForPlayer = ConstantPool.reachData.get(player.getUniqueID());
            if(reachForPlayer != null) {
                module.getCombatReachValue().set(reachForPlayer + 0.1f);
                FairPlayClient.inCombat = true;
                FairPlayClient.lastCombat = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
            }
        }
    }

}
