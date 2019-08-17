package com.sannmizu.nearby_alumni.Utils;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {
    //构建Cipher实例时所传入的的字符串，默认为"RSA/NONE/PKCS1Padding"
    private static String sTransform = "RSA/NONE/PKCS1Padding";
    //进行Base64转码时的flag设置，默认为Base64.DEFAULT
    private static int sBase64Mode = Base64.NO_WRAP;
    /**秘钥默认长度*/
    public static final int DEFAULT_KEY_SIZE = 1024;
    /**加密的数据最大的字节数，即117个字节*/
    public static final int DEFAULT_BUFFERSIZE = (DEFAULT_KEY_SIZE / 8) - 11;
    /**当加密的数据超过DEFAULT_BUFFERSIZE，则使用分段加密*/
    private static String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClHg73wKT31u0EXHLHWEwLlZf1" +
            "nhI18NJtfZS70AlFjP5tEhTiCzV1WVL6dAuM6xpb0oen1HGvRvbRkhOIDKyaPz2g" +
            "md1buZnwYE2fAA+2S7ttejcwIuAVkR6OBmUCEeEJXAeDMWBc/VZzS6NtuPIgV0nR" +
            "1dJw70rMmwnIn2WerQIDAQAB";

    private static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        // 得到公钥对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        // 加密数据
        Cipher cipher = Cipher.getInstance(sTransform);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > DEFAULT_BUFFERSIZE) {
                cache = cipher.doFinal(data, offSet, DEFAULT_BUFFERSIZE);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * DEFAULT_BUFFERSIZE;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    public static String encrypt(String data) {
        byte[] encrypted = null;
        try {
            //  String publicKeyString = IOUtils.readAssertsFile(getApplicationContext(), "server_rsa_public_key.pem");
            byte[] publicKey = Base64.decode(public_key, sBase64Mode);
            encrypted = encryptByPublicKey(data.getBytes(), publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(encrypted, sBase64Mode);
    }

}
