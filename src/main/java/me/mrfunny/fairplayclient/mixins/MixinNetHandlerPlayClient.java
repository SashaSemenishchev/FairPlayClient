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

import me.mrfunny.lunarspoof.LunarSpoof;
import me.mrfunny.lunarspoof.websocket.asset.LunarAssetWebSocket;
import me.mrfunny.lunarspoof.websocket.asset.packet.impl.WSPacketServerSetServer;
import me.mrfunny.liquidaddons.util.ConstantPool;
import me.mrfunny.fairplayclient.FairPlayClient;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Handles Lunar client things
 */
@Mixin(value = NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

    @Shadow @Final private NetworkManager netManager;

    @Inject(method = "handleJoinGame", at = @At(value = "HEAD"))
    public void injectHandleJoinGame(CallbackInfo callbackInfo) {
        ConstantPool.setCurrentNetworkManager(netManager);
        LunarAssetWebSocket socket = FairPlayClient.lunarSpoof.getAssetSocket();
        if(socket != null && socket.isClosed()) {
            socket.reconnect();
        }

        String ip;
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        if(data != null) {
            ip = data.serverIP;
        } else {
            ip = "localhost";
        }
        LunarSpoof.getLogger().info("IP: " + ip);
        if(socket != null) {
            socket.sendPacket(new WSPacketServerSetServer(ip));
        }
        LiquidBounce.clientRichPresence.setShowRichPresenceValue(false);
    }

    @Inject(method = "handleSpawnPlayer", at = @At("RETURN"))
    public void handlePlayerSpawn(S0CPacketSpawnPlayer packetIn, CallbackInfo ci) {
        LunarAssetWebSocket socket = FairPlayClient.lunarSpoof.getAssetSocket();
        if (socket != null) {
            socket.requestPlayerInfo(packetIn.getPlayer());
        }
    }

    @Inject(method = "cleanup", at = @At(value = "RETURN"))
    public void handleCleanup(CallbackInfo ci) {
        LunarSpoof.getLogger().info("Handling cleanup");
        LunarAssetWebSocket socket = FairPlayClient.lunarSpoof.getAssetSocket();
        if(socket != null) {
            socket.sendPacket(new WSPacketServerSetServer(""));
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    public void handleDisconnect(IChatComponent reason, CallbackInfo ci) {
        LunarSpoof.getLogger().info("Disconnecting");
        LunarSpoof.Companion.getLunarUsers().clear();
    }
}
