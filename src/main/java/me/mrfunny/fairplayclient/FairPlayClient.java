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

package me.mrfunny.fairplayclient;

import gg.essential.api.EssentialAPI;
import me.mrfunny.lunarspoof.LunarSpoof;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.io.File;
import java.nio.file.Files;

@Mod(
        modid = FairPlayClient.MODID,
        name = FairPlayClient.MOD_NAME,
        version = FairPlayClient.VERSION,
        acceptedMinecraftVersions = "[1.8.9]",
        clientSideOnly = true
)
public class FairPlayClient {
    public final static String MODID = "fairplayclient";
    public final static String MOD_NAME = "Fair Play Client";
    public final static String  VERSION = "0.1";

    public static long lastCombat = 0;
    public static boolean inCombat = false;
    public static boolean locked = false;
    public static LunarSpoof lunarSpoof;
    public static boolean failedSpoof;

    @Mod.EventHandler
    public void onMcLoad(FMLLoadCompleteEvent event) {
        if(failedSpoof) {
            sendWarning("§cFailed to spoof Lunar client");
        }
    }
    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        ServerData data = mc.getCurrentServerData();
        String ip;

        if(data == null) {
            ip = "";
        } else {
            ip = data.serverIP;
        }
        lunarSpoof = new LunarSpoof(mc.getSession(), ip);
        lunarSpoof.startAssetWebSocket();
        if(Files.exists(new File(mc.mcDataDir, "clientlock").toPath())) {
            locked = true;
        }
//        Display.setTitle("Minecraft 1.8.9");
    }

    public static void sendWarning(String it) {
        EssentialAPI.getNotifications().push("§eFairPlayClient", it, 2f);
    }

    public static void sendNotification(String title, String subtitle) {
        EssentialAPI.getNotifications().push(title, subtitle, 2f);
    }
}
