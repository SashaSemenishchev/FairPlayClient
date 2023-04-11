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

import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import me.mrfunny.fairplayclient.FairPlayClient
import me.mrfunny.lunarspoof.LunarSpoof
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.minecraft.launchwrapper.Launch
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import kotlin.io.path.Path

class LockLogsCommand : Command("locklogs") {
    override fun execute(args: Array<String>) {
        Multithreading.runAsync {
            val start = System.currentTimeMillis();
            val latestLog = File(mc.mcDataDir, "logs" + File.separator + "latest.log")
            val toPrint = arrayListOf<String>()

            for (line in Files.readAllLines(latestLog.toPath())) {
                if(isBadLogLine(line)) continue
                val newLine = replaceGoodLines(line)
                if(newLine != null) {
                    toPrint.add(newLine)
                    continue
                }
                toPrint.add(line)
            }
            val writer = PrintWriter(latestLog)
            for (line in toPrint) {
                writer.println(line)
            }
            writer.close()
            EssentialAPI.getNotifications().push("Logs were cleaned up", "Done in ${System.currentTimeMillis() - start}ms")
        }
    }

    companion object {
        @JvmStatic
        fun isBadLogLine(line: String) = line.contains("essentials lb", true) ||
                line.contains("liquidbounce", true) ||
                line.contains("lunarspoof", ignoreCase = true) ||
                line.contains("ls", ignoreCase = true) ||
                line.contains("auth", ignoreCase = true) ||
                line.contains("ccbluex", true) ||
                line.contains("spoof", true)

        @JvmStatic
        fun replaceGoodLines(line: String): String? {
            if(line.contains("fairplayclient")) {
                return line.replace("fairplayclient", "labymod")
            }

            return null
        }
    }


    override fun tabComplete(args: Array<String>): List<String> {
        return emptyList()
    }
}