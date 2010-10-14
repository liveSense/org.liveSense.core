package org.liveSense.core;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import org.apache.jackrabbit.util.Text;


public class PasswordDigester {

    String password;
	String encoding;

    public PasswordDigester(String password, String digest, String encoding) throws IllegalArgumentException, NoSuchAlgorithmException, UnsupportedEncodingException {
        this.password = digestPassword(password, digest, encoding);
    }

    /**
     * Digest the given password using the configured digest algorithm
     *
     * @param pwd the value to digest
     * @return the digested value
     * @throws IllegalArgumentException
     */
    protected String digestPassword(String pwd, String passwordDigestAlgoritm, String encoding) throws IllegalArgumentException, NoSuchAlgorithmException, UnsupportedEncodingException {
        StringBuffer password = new StringBuffer();
        password.append("{").append(passwordDigestAlgoritm).append("}");
        password.append(Text.digest(passwordDigestAlgoritm, pwd.getBytes(encoding)));
        return password.toString();
    }

    public String toString() {
        return password;
    }


}
