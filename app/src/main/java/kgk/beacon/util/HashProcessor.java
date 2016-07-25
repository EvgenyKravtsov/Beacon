package kgk.beacon.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashProcessor {

    public static String md5Hash(String input) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(input.getBytes(),0,input.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }
}
