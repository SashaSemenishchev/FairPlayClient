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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.mrfunny.lunarspoof.LunarSpoof;
import me.mrfunny.lunarspoof.websocket.asset.LunarAssetWebSocket;
import me.mrfunny.lunarspoof.websocket.asset.packet.impl.WSPacketServerSetServer;
import me.mrfunny.liquidaddons.util.ConstantPool;
import me.mrfunny.fairplayclient.FairPlayClient;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(C17PacketCustomPayload.class)
public class MixinC17PayloadPacket {

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/network/PacketBuffer;)V", at = @At("RETURN"))
    public void handleCustomPayload(String channelIn, PacketBuffer dataIn, CallbackInfo ci) {
        if(!channelIn.equals("MC|Brand")) return;
        final ByteBuf message = Unpooled.buffer();
        message.writeBytes("Lunar-Client".getBytes());

        NetworkManager netManager = ConstantPool.getCurrentNetworkManager();
        if(netManager != null) {
            LunarSpoof.getLogger().info("Fooled Lunar Client");
            netManager.sendPacket(new C17PacketCustomPayload("REGISTER", new PacketBuffer(message)));
        }

        String ip;
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        if(data != null) {
            ip = data.serverIP;
        } else {
            ip = "localhost";
        }
        LunarSpoof.getLogger().info("IP: " + ip);
        LunarAssetWebSocket socket = FairPlayClient.lunarSpoof.getAssetSocket();
        if(socket != null) {
            socket.sendPacket(new WSPacketServerSetServer(ip));
        }
        LiquidBounce.clientRichPresence.setShowRichPresenceValue(false);

    }

}
