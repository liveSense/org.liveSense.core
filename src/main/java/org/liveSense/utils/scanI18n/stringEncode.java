/*
 * stringEncode.java
 *
 * Created on 2006-09-24, 9:46 AM
 */
/*
Copyright (C) 2006  Yong Li

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.liveSense.utils.scanI18n;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author Yong Li
 */
public class stringEncode {
    static final String chars = "0123456789ABCDEFabcdef";
    /**
     * Creates a new instance of stringEncode
     */
    private stringEncode() {
    }
    public static String native2ascii(String s) {
        StringBuffer sb = new StringBuffer(s.length() + 80);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c <= 0x7f) {
                sb.append(c);
            } else {
				String hex = Integer.toHexString((int) c).toUpperCase();
				for (int j=hex.length(); j<4; j++) hex = "0"+hex;
                sb.append("\\u" + hex);
            }
        }
        return sb.toString();
    }
    public static String ascii2native(String s){
        StringBuffer sb = new StringBuffer(s.length());
        int pos=0;
        while(pos < s.length())
        {
            if(s.charAt(pos) == '\\')
            {
                if (pos + 5 < s.length() && s.charAt(pos+1) == 'u' && isVoildHex(s,pos+2))
                {
                    sb.append(convert2native(s,pos+2));
                    pos += 6;
                }
                else
                {
                    sb.append(s.charAt(pos++));
                }
            }
            else
            {
                sb.append(s.charAt(pos++));
            }
        }
        return sb.toString();
    }
    private static boolean isVoildHex(String s,int pos)
    {
        return   chars.indexOf(s.charAt(pos)) != -1 
                && chars.indexOf(s.charAt(pos+1)) != -1
                && chars.indexOf(s.charAt(pos+2)) != -1
                && chars.indexOf(s.charAt(pos+3)) != -1;
    }
    private static String convert2native(String s,int start)
    {
        String tmp = s.substring(start,start+4).toUpperCase();
        byte chs[] = new byte[2];
        int value = 16 * chars.indexOf(tmp.charAt(0)) + chars.indexOf(tmp.charAt(1));
        chs[0] = (byte)value;
        value = 16 * chars.indexOf(tmp.charAt(2)) + chars.indexOf(tmp.charAt(3));
        chs[1] = (byte)value;
        String ret;
        try {
            ret= new String(chs,"UTF-16");
        } catch (UnsupportedEncodingException ex) {
            ret = "\\u" + tmp;
        }
        return ret;
    }
}
