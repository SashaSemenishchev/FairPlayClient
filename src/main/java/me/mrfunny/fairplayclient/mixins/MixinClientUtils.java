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

import me.mrfunny.fairplayclient.util.GhostLogFilter;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientUtils.class)
public class MixinClientUtils {

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/LogManager;getLogger(Ljava/lang/String;)Lorg/apache/logging/log4j/Logger;"), remap = false)
    private static Logger getProperLogger(String name) {
        LoggerContext context = (LoggerContext) LogManager.getContext();
        context.addFilter(new GhostLogFilter());
        return LogManager.getLogger("Essential LB");
    }
}
