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

import org.apache.commons.codec.digest.DigestUtils;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

public class HardwareIdProvider {
    public static String getHardwareId() {
        SystemInfo info = new SystemInfo();
        OperatingSystem os = info.getOperatingSystem();

        return DigestUtils.sha256Hex(
            System.getProperty("user.name")
                + ";OS:{" +
                os.getManufacturer() + " " + os.getFamily() + os.getVersion().toString()
                 + "}"

        );
    }
}
