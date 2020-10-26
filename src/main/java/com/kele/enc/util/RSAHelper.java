package com.kele.enc.util;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @AUTHOR xukele
 * @SINCE 2020/1/4 11:57
 * <p>
 * 支持PKCS8格式RSA钥匙对
 */
public class RSAHelper {

    private static final String RSA_TYPE = "RSA";

    /**
     * 使用公钥验证签名
     *
     * @param source
     * @param signature
     * @param publicKey
     * @return 验证成功返回true
     */
    public static boolean verifySignature(byte[] source, String signature, String publicKey) {
        try {
            Signature signator = Signature.getInstance("SHA256withRSA");
            signator.initVerify(getPublicKey(publicKey));
            signator.update(source);
            return signator.verify(Base64Utils.decodeFromString(signature));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot verifySignature", e);
        }

    }

    /**
     * 使用私钥计算出 source的签名;
     *
     * @param source
     * @param privateKey
     * @return
     */
    public static String createSignature(byte[] source, String privateKey) {
        try {
            Signature signator = Signature.getInstance("SHA256withRSA");//
            signator.initSign(getPrivateKey(privateKey));
            signator.update(source);
            return Base64Utils.encodeToString(signator.sign());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot createSignature", e);
        }

    }

    /**
     * 使用公钥解密
     */
//	public static String decryptByPublicKey(String content, String publicKey) {
//		try {
//			byte[] decrypt = decrypt(Base64Utils.decodeFromString(content), getPublicKey(publicKey));
//			return new String(decrypt, StandardCharsets.UTF_8);//解密得到字符串的byte数组,直接转换;
//		} catch (Exception e) {
//			throw new IllegalStateException("Cannot decrypt", e);
//		}
//	}

    /**
     * 使用私钥解密
     */
    public static String decryptByPrivateKey(String content, String privateKey) {
        try {
            byte[] decrypt = decrypt(Base64Utils.decodeFromString(content), getPrivateKey(privateKey));
            return new String(decrypt, StandardCharsets.UTF_8);//解密得到字符串的byte数组,直接转换;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot decrypt", e);
        }
    }

    /**
     * 使用私钥加密
     */
//	public static String encryptByPrivateKey(String content, String privateKey) {
//		try {
//			byte[] encrypt = encrypt(content.getBytes(StandardCharsets.UTF_8), getPrivateKey(privateKey));
//			return Base64Utils.encodeToString(encrypt);////加密后转换成字符串形式;
//		} catch (Exception e) {
//			throw new IllegalStateException("Cannot encrypt", e);
//		}
//	}

    /**
     * 使用公钥加密
     *
     * @param content   明文
     * @param publicKey 公钥
     * @return 返回密文
     */
    public static String encryptByPublicKey(String content, String publicKey) {
        try {
            byte[] encrypt = encrypt(content.getBytes(StandardCharsets.UTF_8), getPublicKey(publicKey));
            return Base64Utils.encodeToString(encrypt);//加密后转换成字符串形式;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot encrypt", e);
        }
    }

    /**
     * 加密方法
     *
     * @param text
     * @param key
     * @return
     */
    private static byte[] encrypt(byte[] text, Key key) {
        ByteArrayOutputStream output = new ByteArrayOutputStream(text.length);
        try {
            final Cipher cipher = Cipher.getInstance(RSA_TYPE);
            int limit = Math.min(text.length, 128);
            int pos = 0;
            while (pos < text.length) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                cipher.update(text, pos, limit);
                pos += limit;
                limit = Math.min(text.length - pos, 128);
                byte[] buffer = cipher.doFinal();
                output.write(buffer, 0, buffer.length);
            }
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot encrypt", e);
        }
    }

    /**
     * 解密方法
     *
     * @param text
     * @param key
     * @return
     */
    private static byte[] decrypt(byte[] text, Key key) {
        ByteArrayOutputStream output = new ByteArrayOutputStream(text.length);
        try {
            final Cipher cipher = Cipher.getInstance(RSA_TYPE);
            int limit = Math.min(text.length, 128);
            int pos = 0;
            while (pos < text.length) {
                cipher.init(Cipher.DECRYPT_MODE, key);
                cipher.update(text, pos, limit);
                pos += limit;
                limit = Math.min(text.length - pos, 128);
                byte[] buffer = cipher.doFinal();
                output.write(buffer, 0, buffer.length);
            }
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot decrypt", e);
        }
    }

    private static PrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_TYPE);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(privateKey)));
    }

    private static PublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_TYPE);
        return keyFactory.generatePublic(new X509EncodedKeySpec(Base64Utils.decodeFromString(publicKey)));
    }

    /**
     * demo
     *
     * @param args
     * @throws NoSuchAlgorithmException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {

        String content = "xukele_666";//明文;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_TYPE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //获取公钥和私钥钥的字符串形式;
        String privateKey = Base64Utils.encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64Utils.encodeToString(keyPair.getPublic().getEncoded());
        System.out.println("私钥:" + privateKey);
        System.out.println("公钥:" + publicKey);
        System.out.println("明文：" + content);
        //公钥加密,私钥解密;
        System.out.println("使用公钥加密,私钥解密");
        String encrypt = encryptByPublicKey(content, publicKey);
        System.out.println("密文：" + encrypt);//打印密文;
        System.out.println("还原明文：" + decryptByPrivateKey(encrypt, privateKey));
    }

}
