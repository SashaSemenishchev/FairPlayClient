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

import jdk.internal.dynalink.support.BottomGuardingDynamicLinker;
import me.mrfunny.fairplayclient.FairPlayClient;
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static net.minecraft.client.renderer.GlStateManager.resetColor;

@Mixin(GuiButton.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiButton extends Gui {

    @Shadow
    public boolean visible;

    @Shadow
    public int xPosition;

    @Shadow
    public int yPosition;

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    protected boolean hovered;

    @Shadow
    public boolean enabled;

    @Shadow
    protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

    @Shadow
    public String displayString;

    @Shadow
    @Final
    protected static ResourceLocation buttonTextures;
    private float cut;
    private float alpha;

    /**
     * @author CCBlueX
     */
    public void drawHackingModeButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            final FontRenderer fontRenderer = mc.getLanguageManager().isCurrentLocaleUnicode() ? mc.fontRendererObj : Fonts.font35;
            hovered = (mouseX >= xPosition && mouseY >= yPosition &&
                mouseX < xPosition + width && mouseY < yPosition + height);
            final float deltaTime = RenderUtils.deltaTime;

            if (enabled && hovered) {
                cut += 0.05F * deltaTime;

                if (cut >= 4) cut = 4;

                alpha += 0.3F * deltaTime;

                if (alpha >= 210) alpha = 210;
            } else {
                cut -= 0.05F * deltaTime;

                if (cut <= 0) cut = 0;

                alpha -= 0.3F * deltaTime;

                if (alpha <= 120) alpha = 120;
            }

            Gui.drawRect(xPosition + (int) cut, yPosition,
                xPosition + width - (int) cut, yPosition + height,
                enabled ? new Color(0F, 0F, 0F, alpha / 255F).getRGB() :
                    new Color(0.5F, 0.5F, 0.5F, 0.5F).getRGB());

            mc.getTextureManager().bindTexture(buttonTextures);
            mouseDragged(mc, mouseX, mouseY);

            AWTFontRenderer.Companion.setAssumeNonVolatile(true);

            fontRenderer.drawStringWithShadow(displayString,
                (float) ((xPosition + width / 2) -
                    fontRenderer.getStringWidth(displayString) / 2),
                yPosition + (height - 5) / 2F, 14737632);

            AWTFontRenderer.Companion.setAssumeNonVolatile(false);

            resetColor();
        }
    }

    @Inject(method = "drawButton", at = @At("HEAD"), cancellable = true)
    public void handleButtonDraw(Minecraft mc, int mouseX, int mouseY, CallbackInfo ci) {
        if(!FairPlayClient.isLocked()) {
            ci.cancel();
            drawHackingModeButton(mc, mouseX, mouseY);
        }
    }
}
