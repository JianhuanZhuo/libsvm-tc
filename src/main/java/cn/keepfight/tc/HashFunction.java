package cn.keepfight.tc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * Created by 卓建欢 on 2017/12/19.
 */
public class HashFunction {

    private MessageDigest md;

    public HashFunction() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int hash(Object s) {
//        md.update(s.);
//        byte[] digest = md.digest();
        return s.hashCode();
    }
}
