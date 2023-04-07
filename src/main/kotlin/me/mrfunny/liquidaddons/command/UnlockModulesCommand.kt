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
import me.mrfunny.fairplayclient.util.AesProvider
import me.mrfunny.fairplayclient.util.HardwareIdProvider
import net.ccbluex.liquidbounce.features.command.Command
import java.io.File
import java.nio.file.Files

class UnlockModulesCommand : Command("unlockmodules") {
    override fun execute(args: Array<String>) {
        val password = args.getOrNull(1) ?: return
        val lockPath = args.getOrNull(2) ?: "clientlock"
        val lockFile = File(mc.mcDataDir, lockPath)
        try {
            val passwordToMatch = AesProvider.decrypt(Files.readAllBytes(lockFile.toPath()), HardwareIdProvider.getHardwareId())
            if(password.contentEquals(passwordToMatch)) {
                FairPlayClient.modulesLocked = false
            }
        } catch (ignored: Exception){
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        return emptyList();
    }
}