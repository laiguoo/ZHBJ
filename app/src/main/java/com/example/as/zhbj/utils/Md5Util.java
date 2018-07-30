package com.example.as.zhbj.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by asus on 2017/12/30.
 */

public class Md5Util {
    /**
     * 给指定字符串按照MD5算法加密
     *
     * @param psd 需要加密的密码
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encode(String psd) {
        psd = psd + "mobilesafe";
        //try catch 包围代码块快捷键 ctrl+alt+T
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bs = digest.digest(psd.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bs) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                stringBuffer.append(hexString);
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
