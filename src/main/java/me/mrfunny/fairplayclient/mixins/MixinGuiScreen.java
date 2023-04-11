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

import gg.essential.api.utils.Multithreading;
import kotlin.Pair;
import me.mrfunny.fairplayclient.FairPlayClient;
import me.mrfunny.liquidaddons.module.WordsReplacer;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {

//    @Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
    @Shadow public Minecraft mc;
//    private final char[] punctuation = {',', '.', '?', '!', '/', '\\', '"', '\''};

    /**
     * @author MrFunny
     * @reason custom chat logic
     */
    @Overwrite
    public void sendChatMessage(String msg, boolean addToChat) {
        Multithreading.runAsync(() -> {
            if (msg.startsWith(String.valueOf(LiquidBounce.commandManager.getPrefix()))) {
                if(!msg.contains("lock") && addToChat) {
                    this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
                }

                if(FairPlayClient.isLocked() && FairPlayClient.modulesLocked && !msg.contains("lock")) {
                    return;
                }
                LiquidBounce.commandManager.executeCommands(msg);
                return;
            }
            String finalMsg = msg;
            if(WordsReplacer.INSTANCE.getState()) {
                String[] words = finalMsg.split(" ");
                boolean replacedSomething = false;
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    for (Map.Entry<Pair<String, String>, Boolean> replacementData : WordsReplacer.getReplacements().entrySet()) {
                        Pair<String, String> replacement = replacementData.getKey();
                        String replacementKey = replacement.getFirst();
                        if(replacementData.getValue() && !word.equals(replacementKey)) continue;
                        if(word.contains(replacementKey)) {
                            words[i] = word = word.replace(replacementKey, replacement.getSecond());
                            replacedSomething = true;
                        }
                    }
                }
                if(replacedSomething) {
                    finalMsg = String.join(" ", words);
                }
            }

            if (addToChat) {
                this.mc.ingameGUI.getChatGUI().addToSentMessages(finalMsg);
            }

            if (ClientCommandHandler.instance.executeCommand(this.mc.thePlayer, finalMsg) == 0) {
                this.mc.thePlayer.sendChatMessage(finalMsg);
            }
        });
    }
}
