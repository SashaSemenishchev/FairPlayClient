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

import me.mrfunny.fairplayclient.FairPlayClient;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {

    @Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
    public void handleChatMessage(String msg, boolean addToChat, CallbackInfo ci) {
        if (msg.startsWith(String.valueOf(LiquidBounce.commandManager.getPrefix())) && addToChat) {

            if(!msg.contains("lock")) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(msg);
            }

            if(FairPlayClient.isLocked() && FairPlayClient.modulesLocked && !msg.contains("lock")) {
                return;
            }
            ci.cancel();
            LiquidBounce.commandManager.executeCommands(msg);
        }
    }
}
