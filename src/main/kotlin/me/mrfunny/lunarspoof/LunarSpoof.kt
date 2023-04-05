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

package me.mrfunny.lunarspoof

import com.mojang.util.UUIDTypeAdapter
import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import me.mrfunny.fairplayclient.FairPlayClient
import me.mrfunny.lunarspoof.feature.indicator.LunarNameTagIcon
import me.mrfunny.lunarspoof.websocket.asset.LunarAssetWebSocket
import me.mrfunny.lunarspoof.websocket.auth.LunarAuthWebSocket
import net.minecraft.util.Session
import org.apache.commons.codec.digest.DigestUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.URISyntaxException
import java.util.*

class LunarSpoof(private val session: Session, private val currentServer: String?) {

    var assetSocket: LunarAssetWebSocket? = null
        private set

    private fun startAuthSocket(consumer: (String?) -> Unit) {
        try {
            LOGGER.info("Starting Authentication WebSocket...")
            var username = "vkus" // it's meeee
            var playerId = "cde86f22-ff70-49eb-b2b2-e1dcdaa2e8d6"
            if (!EssentialAPI.getMinecraftUtil().isDevelopment()) {
                username = session.username
                playerId = UUIDTypeAdapter.fromString(session.playerID).toString()
            }
            println("$username:$playerId")
            LunarAuthWebSocket(
                mapOf(
                    "username" to username,
                    "playerId" to playerId,
                ),
                session,
                consumer
            ).connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun startAssetWebSocket() {
        Multithreading.runAsync {
            try {
                if (assetSocket != null && assetSocket!!.isOpen) {
                    println("Already open, closing")
                    assetSocket!!.closeBlocking()
                }
                startAuthSocket { auth ->
                    LOGGER.info("Starting Asset WebSocket...")
                    if(auth == null) {
                        println("Failed authentication!")
                        FairPlayClient.failedSpoof = true;
                        return@startAuthSocket
                    }
                    LOGGER.debug("Authentication: $auth")

                    val httpHeaders = mapOf(
                        "accountType" to "XBOX",
                        "arch" to (System.getProperty("os.arch") ?: System.getenv("PROCESSOR_ARCHITECTURE")),
                        "Authorization" to auth,
                        "branch" to "master",
                        "clothCloak" to "",
                        "gitCommit" to "71aa13d47ce799788b8a4be46c1f196a4183e1b7'",
                        "hatHeightOffset" to hatHeightOffset,
                        "hwid" to DigestUtils.sha256Hex(System.getProperty("user.name")),
                        "launcherVersion" to "2.15.1",
                        "lunarPlusColor" to "-1",
                        "os" to System.getProperty("os.name"),
                        "playerId" to UUIDTypeAdapter.fromString(session.playerID).toString(),
                        "protocolVersion" to "9",
                        "server" to (currentServer ?: ""),
                        "showHatsOverHelmet" to "",
                        "showHatsOverSkinLayer" to "",
                        "username" to session.username,
                        "version" to "v1_8",
                        "flipShoulderPet" to "",
                        "ichorModules" to "common,optifine,lunar",
                        "showOverBoots" to "",
                        "showOverChestplate" to "",
                        "showOverLeggings" to "",
                        "Host" to "assetserver.lunarclientprod.com"
                    )
                    LOGGER.debug("Headers: ")
                    LOGGER.debug(httpHeaders)
                    try {
                        LunarAssetWebSocket(httpHeaders).also { assetSocket = it }.connect()
                    } catch (e: URISyntaxException) {
                        FairPlayClient.failedSpoof = true;
                        e.printStackTrace()
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        val lunarUsers = mutableMapOf<UUID, LunarNameTagIcon>()
        @get:JvmName("getLogger")
        @JvmStatic
        val LOGGER: Logger = LogManager.getLogger("LunarSpoof")
        const val hatHeightOffset: String = "[{\"id\":3520,\"height\":0.0},{\"id\":2628,\"height\":0.0},{\"id\":3661,\"height\":0.0},{\"id\":3471,\"height\":0.0},{\"id\":3472,\"height\":0.0},{\"id\":2583,\"height\":0.0},{\"id\":2584,\"height\":0.0},{\"id\":2526,\"height\":0.0},{\"id\":2527,\"height\":0.0},{\"id\":2528,\"height\":0.0},{\"id\":2856,\"height\":0.0},{\"id\":2540,\"height\":0.0},{\"id\":2541,\"height\":0.0},{\"id\":2542,\"height\":0.0},{\"id\":3438,\"height\":0.0},{\"id\":2543,\"height\":0.0},{\"id\":3439,\"height\":0.0},{\"id\":2544,\"height\":0.0},{\"id\":2545,\"height\":0.0},{\"id\":2424,\"height\":0.0},{\"id\":2490,\"height\":0.0},{\"id\":2491,\"height\":0.0},{\"id\":2492,\"height\":0.0},{\"id\":2493,\"height\":0.0},{\"id\":2494,\"height\":0.0},{\"id\":2558,\"height\":0.0},{\"id\":2559,\"height\":0.0},{\"id\":3519,\"height\":0.0}]"
    }
}