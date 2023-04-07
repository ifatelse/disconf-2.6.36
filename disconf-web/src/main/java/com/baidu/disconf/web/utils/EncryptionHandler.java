package com.baidu.disconf.web.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/4/7 14:49
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class EncryptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionHandler.class);

    private static final String AES_NAME = "aes";

    private static final String AES_MODE = "AES/CBC/PKCS5Padding";

    private static final String IV_PARAMETER = "fa5207b3fa2686b2";

    private static final String DEFAULT_SECRET_KEY = "disconf6b31603ae47325b4e19f931a7";



    public static String encrypt(String fileName, String content) {

        if (StringUtils.isBlank(fileName)) {
            return content;
        }
        try {
            String secretKey = generateSecretKey(fileName);
            secretKey = new String(Base64.decodeBase64(secretKey.getBytes(StandardCharsets.UTF_8)));
            Key key = new SecretKeySpec(Hex.decodeHex(secretKey.toCharArray()), AES_NAME);
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV_PARAMETER.getBytes(StandardCharsets.UTF_8)));
            byte[] result = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            logger.error("[EncryptionHandler] encrypt error", e);
        }
        return content;
    }

    public static String decrypt(String fileName, String content) {
        if (StringUtils.isBlank(fileName) || StringUtils.isBlank(content)) {
            return content;
        }
        try {
            String secretKey = generateSecretKey(fileName);
            secretKey = new String(Base64.decodeBase64(secretKey.getBytes(StandardCharsets.UTF_8)));
            Key key = new SecretKeySpec(Hex.decodeHex(secretKey.toCharArray()), AES_NAME);
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV_PARAMETER.getBytes(StandardCharsets.UTF_8)));
            byte[] result = cipher.doFinal(Hex.decodeHex(content.toCharArray()));
            return new String(result);
        } catch (Exception e) {
            logger.error("[EncryptionHandler] decrypt error", e);
        }
        return content;
    }


    private static String generateSecretKey(String fileName) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_NAME);
            keyGenerator.init(new SecureRandom(fileName.getBytes(StandardCharsets.UTF_8)));
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] byteKey = secretKey.getEncoded();
            String key = Hex.encodeHexString(byteKey);
            return new String(Base64.encodeBase64(key.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            logger.error("[AesEncryptionPluginService] generate key error", e);
        }
        return DEFAULT_SECRET_KEY;
    }

}
