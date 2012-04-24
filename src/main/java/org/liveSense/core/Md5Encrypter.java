/*
 *  Copyright 2010 Robert Csakany <robson@semmi.se>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.liveSense.core;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

/**
 *
 * @author Robert Csakany (robson@semmi.se)
 * @created Apr 8, 2010
 */
public class Md5Encrypter {
	

    public static String encrypt(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    			MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] utf8 = str.getBytes("UTF-8");
            BigInteger bigInt = new BigInteger(1, digest.digest(utf8));
   			return bigInt.toString(16);
    }
}
    
