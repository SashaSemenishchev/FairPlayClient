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

package me.mrfunny.liquidaddons.command

import me.mrfunny.fairplayclient.FairPlayClient
import net.ccbluex.liquidbounce.features.command.Command
import java.io.File
import java.nio.file.Files

class LockCommand : Command("lock") {
    override fun execute(args: Array<String>) {
        if(FairPlayClient.locked) return
        val lockPath = args.getOrNull(0) ?: "clientlock"
        val lockFile = File(lockPath)
        try {
            Files.write(lockFile.toPath(), byteArrayOf(0))
        } catch (ignored: Exception){}

        mc.ingameGUI.chatGUI.clearChatMessages()
        FairPlayClient.locked = true;
    }

    override fun tabComplete(args: Array<String>): List<String> {
        return emptyList()
    }
}