
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

import me.mrfunny.liquidaddons.module.DangerousModBlocker;
import me.mrfunny.liquidaddons.util.ConstantPool;
import me.mrfunny.fairplayclient.FairPlayClient;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.modules.player.Reach;
import net.ccbluex.liquidbounce.features.module.modules.render.ClickGUI;
import net.ccbluex.liquidbounce.value.Value;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = Module.class, remap = false)
public abstract class MixinModule {

    @Inject(method = "getValues", at = @At("RETURN"), cancellable = true, remap = false)
    public void handleGetValues(CallbackInfoReturnable<List<Value<?>>> cir) {
        if(this.getClass().isAssignableFrom(Reach.class)) {
            List<Value<?>> values = cir.getReturnValue();
            values.add(ConstantPool.fairplayModeEnabled);
            values.add(ConstantPool.changeVelocity);
            values.add(ConstantPool.maxReachDistance);
            values.add(ConstantPool.reachAddition);
            values.add(ConstantPool.minVelocity);
            cir.setReturnValue(values);
        }
    }

    @Inject(method = "setState", at = @At("HEAD"), cancellable = true, remap = false)
    public void checkIfDangerous(boolean value, CallbackInfo ci) {
        Module _this = (Module) (Object) this;
        if(_this instanceof ClickGUI && value && FairPlayClient.isLocked() && FairPlayClient.modulesLocked) {
            ci.cancel();
            return;
        }
        if(!value) return;
        DangerousModBlocker checker = DangerousModBlocker.INSTANCE;
        if(!checker.getState()) return;
        if(DangerousModBlocker.isBad(_this)) {
            FairPlayClient.sendWarning("§eFairPlay blocked dangerous mod activation: " + getName());
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    public abstract String getName();
}
