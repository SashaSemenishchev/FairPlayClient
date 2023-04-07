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

import me.mrfunny.fairplayclient.util.MainMenuFactory;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventManager;
import net.ccbluex.liquidbounce.event.ScreenEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.ccbluex.liquidbounce.utils.MinecraftInstance.mc;

@Mixin(Minecraft.class)
@SideOnly(Side.CLIENT)
public class MixinMinecraft {

    @Shadow @Final private static Logger logger;

    @Shadow private int tempDisplayWidth;

    @Shadow private int tempDisplayHeight;

    @Shadow public GuiScreen currentScreen;

    @Shadow public boolean skipRenderWorld;

    @Inject(
        method = "startGame",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/fml/client/SplashProgress;drawVanillaScreen(Lnet/minecraft/client/renderer/texture/TextureManager;)V",
            remap = false
        )
    )
    public void injectIntoSplashToFixWhiteScreen(CallbackInfo ci) {
        try {
            logger.info("Resetting display mode to possibly fix white screen on MacOS");
//            Display.setDisplayMode(Display.getDisplayMode());
            GL11.glViewport(0, 0, tempDisplayWidth, tempDisplayHeight);
            Display.update();
        } catch (Exception e) {
            logger.warn("Failed to reset display mode");
            logger.catching(Level.WARN, e);
        }
    }

    @Inject(method = "displayGuiScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", shift = At.Shift.AFTER))
    private void handleDisplayGuiScreen(CallbackInfo callbackInfo) {
        if (currentScreen instanceof net.minecraft.client.gui.GuiMainMenu || (currentScreen != null && currentScreen.getClass().getName().startsWith("net.labymod") && currentScreen.getClass().getSimpleName().equals("ModGuiMainMenu"))) {
            currentScreen = MainMenuFactory.createMainMenu();

            ScaledResolution scaledResolution = new ScaledResolution(mc);
            currentScreen.setWorldAndResolution(mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
            skipRenderWorld = false;

        }
        LiquidBounce.eventManager.callEvent(new ScreenEvent(currentScreen));
    }

    //        int width = this.tempDisplayWidth; // your window width
//        int height = this.tempDisplayHeight; // your window height
//
//// Reset the viewport to the current size
//            GL11.glViewport(0, 0, width, height);

// Set the display mode to the current mode
}
