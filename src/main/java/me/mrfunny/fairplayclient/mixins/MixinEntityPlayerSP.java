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
import me.mrfunny.liquidaddons.util.Raytracer;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.ModuleManager;
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity;
import net.ccbluex.liquidbounce.features.module.modules.player.Reach;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Handles main FairPlay logic
 */
@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {
    @Shadow protected Minecraft mc;

    @Inject(method = "attackEntityFrom", at = @At("RETURN"))
    public void handleDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        try {
            if(source.getDamageType().contains("fall")) return;
            ModuleManager mm = LiquidBounce.moduleManager;
            Reach module = (Reach) mm.get(Reach.class);
            if(!module.getState() || !ConstantPool.fairplayModeEnabled.get()) return;
            double closestRotation = Double.MAX_VALUE;

            double motionYaw = Raytracer.getMotionYaw(mc.thePlayer);
            EntityPlayer attackingPlayer = null;
            EntityPlayerSP localPlayer = mc.thePlayer;
            double currentDistance;
            double distance = 0;
            float maxDistance = ConstantPool.maxReachDistance.get();
            float checkDistance = maxDistance + 1;
            for (EntityPlayer playerEntity : this.mc.theWorld.playerEntities) {
                if(playerEntity.equals(localPlayer)) continue;
                currentDistance = getDistance(playerEntity, localPlayer, checkDistance);
                if(currentDistance > checkDistance) continue;
                double currentRotationDistance = Math.abs(motionYaw - playerEntity.rotationYawHead);
                if(attackingPlayer == null) {
                    attackingPlayer = playerEntity;
                    distance = currentDistance;
                    closestRotation = currentRotationDistance;
                    continue;
                }

                if(currentRotationDistance < closestRotation) {
                    closestRotation = currentRotationDistance;
                    attackingPlayer = playerEntity;
                    distance = currentDistance;
                }
            }
            if(attackingPlayer != null) {
                FairPlayClient.inCombat = true;
                FairPlayClient.lastCombat = mc.theWorld.getTotalWorldTime();
                System.out.println(distance);
                if(distance >= ConstantPool.legitDistance) {
                    if(distance > 10) {
                        System.out.println(attackingPlayer.posX + "," + attackingPlayer.posZ + " - " + localPlayer.posX + "," + localPlayer.posZ);
                    }
                    if(distance <= maxDistance) {

                        UUID uuid = attackingPlayer.getUniqueID();
                        Float rememberedReach = ConstantPool.reachData.get(uuid);
                        double trueDistance = distance;
                        if(rememberedReach == null) {
                            ConstantPool.reachData.put(uuid, (float) distance);
                        } else {
                            if(rememberedReach < distance) {
                                ConstantPool.reachData.put(uuid, (float) distance);
                            } else if(rememberedReach >= distance) {
                                trueDistance = rememberedReach;
                            }
                        }
                        module.getCombatReachValue().set(trueDistance + 0.1);
                        if(ConstantPool.changeVelocity.get()) {
                            try {
                                Velocity velocity = (Velocity) mm.get(Velocity.class);
                                Field field = velocity.getClass().getDeclaredField("legitChanceValue");
                                field.setAccessible(true);
                                IntegerValue value = (IntegerValue) field.get(velocity);
                                value.set((int)(100 - (distance / maxDistance)));
                            } catch (Exception ignored){}
                        }
                    } else {
                        module.getCombatReachValue().set(maxDistance - 0.1);
                    }


                    FairPlayClient.sendWarning("§c" + attackingPlayer.getName() + " hit you with bigger distance: " + distance);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void onTick(CallbackInfo ci) {
        long current = Minecraft.getMinecraft().theWorld.getTotalWorldTime();

        if((current - FairPlayClient.lastCombat) > 60 && FairPlayClient.inCombat && ConstantPool.fairplayModeEnabled.get()) {
            Reach module = (Reach) LiquidBounce.moduleManager.get(Reach.class);
            module.getCombatReachValue().set(ConstantPool.legitDistance);
            FairPlayClient.inCombat = false;
        }
    }

    private double getDistance(EntityPlayer currentPlayer, EntityPlayerSP player, float max) {
        double deltaX = player.posX - currentPlayer.posX;
        double deltaY = player.posY - currentPlayer.posY;
        double deltaZ = player.posZ - currentPlayer.posZ;
        double strictlyCalculated = StrictMath.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
        MovingObjectPosition raytrace = currentPlayer.rayTrace(max, 1f);
        UUID validateAgainst = raytrace.entityHit == null ? null : raytrace.entityHit.getUniqueID();
        if(!player.getUniqueID().equals(validateAgainst)) {
            return strictlyCalculated;
        }

        double toEyes = raytrace.hitVec.distanceTo(currentPlayer.getPositionEyes(1f));
        if(Math.abs(toEyes - strictlyCalculated) > (player.width / 2)) {
            FairPlayClient.sendWarning(currentPlayer.getName() + " maybe using HitBoxes");
        }

        return toEyes;
    }
}
