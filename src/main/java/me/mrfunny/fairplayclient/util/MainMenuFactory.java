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

package me.mrfunny.fairplayclient.util;

import me.mrfunny.fairplayclient.FairPlayClient;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

public class MainMenuFactory {
    public static GuiScreen createMainMenu() {
        if(FairPlayClient.isLocked()) {
            return new GuiMainMenu();
        } else {
            return new net.ccbluex.liquidbounce.ui.client.GuiMainMenu();
        }
    }

    public static boolean isHackedMenu(GuiScreen screen) {
        return screen instanceof net.ccbluex.liquidbounce.ui.client.GuiMainMenu;
    }
}
