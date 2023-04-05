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

import net.ccbluex.liquidbounce.ui.client.GuiClientConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Inject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(GuiClientConfiguration.Companion.class)
@SideOnly(Side.CLIENT)
public class MixinGuiClientConfiguration {
    /**
     * @author SashaSemenishchev
     * @reason Annoying MacOS bug
     */
    @Overwrite(remap = false)
    public final void updateClientWindow() {
        Display.setTitle("Minecraft 1.8.9");
        try {
            Method method = Minecraft.class.getDeclaredMethod("func_175594_ao");
            method.setAccessible(true);
            method.invoke(Minecraft.getMinecraft());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
