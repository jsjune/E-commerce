package com.ecommerce.common;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AesUtil {

    @Value("${aes.private.key}")
    private String privateKey_256;

    public String aesEncode(String data) throws Exception {
        System.out.println("privateKey_256 = " + privateKey_256);
        SecretKeySpec secretKey = new SecretKeySpec(privateKey_256.getBytes(), "AES");
        IvParameterSpec iV = new IvParameterSpec(
            privateKey_256.substring(0, 16).getBytes());

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iV);

        byte[] encryptionByte = cipher.doFinal(data.getBytes("UTF-8"));

        return Hex.encodeHexString(encryptionByte);
    }

    public String aesDecode(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(privateKey_256.getBytes(), "AES");
        IvParameterSpec iV = new IvParameterSpec(
            privateKey_256.substring(0, 16).getBytes());

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        cipher.init(Cipher.DECRYPT_MODE, secretKey, iV);

        byte[] decryptionByte = cipher.doFinal(Hex.decodeHex(data));

        return new String(decryptionByte);
    }

}
