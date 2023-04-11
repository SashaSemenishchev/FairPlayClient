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
import me.mrfunny.liquidaddons.command.LockLogsCommand;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class GhostLogFilter implements Filter {
    boolean lockedByFile;
    public GhostLogFilter() {

        this.lockedByFile = new File(Minecraft.getMinecraft().mcDataDir, "clientlock").exists();
    }
    @Override
    public Result getOnMismatch() {
        return Result.DENY;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return null;
    }

    @Override
    public Result filter(LogEvent event) {
        if(!FairPlayClient.isLocked() && !lockedByFile) return Result.NEUTRAL;
        if(event.getLoggerName().equals("Essential LB")) {
            return Result.DENY;
        }
        if(LockLogsCommand.isBadLogLine(event.getMessage().getFormattedMessage())) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }
}
