package com.sannmizu.nearby_alumni.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.sannmizu.nearby_alumni.R;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    //构建Cipher实例时所传入的的字符串，默认为"RSA/NONE/PKCS1Padding"
    private static String sTransform = "AES/CBC/PKCS5Padding";
    //进行Base64转码时的flag设置，默认为Base64.DEFAULT
    private static int sBase64Mode = Base64.NO_WRAP;
    /**秘钥默认长度*/
    public static final int DEFAULT_KEY_SIZE = 128;

    private static byte[] encryptOrDecrypt(byte[] data, byte[] Key, byte[] Iv, int mode) throws Exception {
        //获取密钥对象
        SecretKeySpec secretKeySpec = new SecretKeySpec(Key, "AES");
        //获取iv
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Iv);
        //加密
        Cipher cipher = Cipher.getInstance(sTransform);
        cipher.init(mode, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(data);
    }

    public static String encrypt(String data, String key, String iv) {
        byte[] encrypted = null;
        try {
            encrypted = encryptOrDecrypt(data.getBytes(), key.getBytes(), iv.getBytes(), Cipher.ENCRYPT_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(encrypted, sBase64Mode);
    }

    public static String decrypt(String data, String key, String iv) {
        byte[] encrypted = Base64.decode(data, sBase64Mode);
        byte[] decrypted = null;
        try {
            decrypted = encryptOrDecrypt(encrypted, key.getBytes(), iv.getBytes(), Cipher.DECRYPT_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decrypted);
    }

    public static String encryptFromLocal(String data, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = sharedPreferences.getString(context.getString(R.string.connect_aes_key), "null");
        String iv = sharedPreferences.getString(context.getString(R.string.connect_aes_iv), "null");
        if(key == "null" || iv == "null") {
            return "";
        }
        return encrypt(data, key, iv);
    }
}
