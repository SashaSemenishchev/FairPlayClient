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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AesProvider {
    public static byte[] encrypt(String inputString, String password) throws Exception {
        byte[] inputBytes = inputString.getBytes(StandardCharsets.UTF_8);
        byte[] passwordBytes = parsePassword(password).getBytes(StandardCharsets.UTF_8);
        // hello
        // Create a secret key specification based on the password
        SecretKeySpec secretKeySpec = new SecretKeySpec(passwordBytes, "AES");

        // Initialize the cipher with the secret key
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        // Encrypt the input bytes
        return cipher.doFinal(inputBytes);
    }

    @NotNull
    private static String parsePassword(String password) {
        int passwdLen = password.length();
        String finalPassword;
        if(passwdLen > 16) {
            finalPassword = password.substring(0, 16);
        } else if(passwdLen < 16) {
            finalPassword = password + (StringUtils.repeat('0', 16 - passwdLen));
        } else finalPassword = password;
        return finalPassword;
    }

    // Decrypts the given encrypted string using the provided password
    public static String decrypt(byte[] input, String password) throws Exception {

        // Convert the encrypted string from Base64 to byte array

        // Convert password to byte array
        byte[] passwordBytes = parsePassword(password).getBytes(StandardCharsets.UTF_8);

        // Create a secret key specification based on the password
        SecretKeySpec secretKeySpec = new SecretKeySpec(passwordBytes, "AES");

        // Initialize the cipher with the secret key
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        // Decrypt the encrypted bytes
        byte[] decryptedBytes = cipher.doFinal(input);

        // Convert the decrypted bytes to a string

        // Return the decrypted string
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
