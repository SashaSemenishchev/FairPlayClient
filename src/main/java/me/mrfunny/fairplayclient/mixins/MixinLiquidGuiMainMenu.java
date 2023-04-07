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

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.client.GuiMainMenu;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.minecraft.client.gui.GuiScreen;
import org.fusesource.jansi.Ansi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinLiquidGuiMainMenu {
    @Inject(method = "func_73863_a", at = @At("RETURN"), remap = false)
    public void handleMainDraw(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiScreen _this = (GuiScreen) (Object) this;
//        this.width - this.fontRendererObj.getStringWidth(s1) - 2,
        String githubClient = "https://github.com/CCBlueX/LiquidBounce/";
        String githubInjection = "https://github.com/SashaSemenishchev/FairPlayClient/";
        Fonts.minecraftFont.drawStringWithShadow("FairPlayClient by MrFunny", 2, _this.height - 10, 16777215);
        Fonts.minecraftFont.drawStringWithShadow(githubClient, _this.width - Fonts.minecraftFont.getStringWidth(githubClient) - 2, _this.height - Fonts.minecraftFont.FONT_HEIGHT - 11, 16777215);
        Fonts.minecraftFont.drawStringWithShadow(githubInjection, _this.width - Fonts.minecraftFont.getStringWidth(githubInjection) - 2, _this.height - 10, 16777215);
    }
}
