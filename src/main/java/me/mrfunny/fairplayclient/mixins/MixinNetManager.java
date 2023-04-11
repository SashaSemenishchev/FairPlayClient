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
import me.mrfunny.liquidaddons.util.ConstantPool;
import me.mrfunny.lunarspoof.LunarSpoof;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
@SideOnly(Side.CLIENT)
public abstract class MixinNetManager {

    @Shadow public abstract void sendPacket(Packet packetIn);

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("RETURN"))
    public void handlePacket(Packet packetIn, CallbackInfo ci) {
        if(packetIn instanceof C17PacketCustomPayload) {
            C17PacketCustomPayload current = (C17PacketCustomPayload) packetIn;
            if(current.getChannelName().equals("MC|Brand")) {
                final ByteBuf message = Unpooled.buffer();
                message.writeBytes("Lunar-Client".getBytes());
                LunarSpoof.getLogger().info("Spoofed 'playing with lunar' icon");
                sendPacket(new C17PacketCustomPayload("REGISTER", new PacketBuffer(message)));
            }
        }
    }
}
