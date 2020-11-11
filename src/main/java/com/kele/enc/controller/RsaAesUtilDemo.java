package com.kele.enc.controller;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SignAlgorithm;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class RsaAesUtilDemo {


    /**
     * 本demo 引入的 工具包:
     *
     * <dependency>
     * <groupId>cn.hutool</groupId>
     * <artifactId>hutool-crypto</artifactId>
     * <version>5.4.6</version>
     * </dependency>
     */
    public static void main(String[] args) throws Exception {


        String content = "test content: i am a requestInfo";

        // 默认生成2048长度的秘钥
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        // 模拟生成客户端的rsa秘钥对;
        KeyPair clientKeyPair = keyPairGenerator.generateKeyPair();

        String clientPrivateKey = Base64Encoder.encode(clientKeyPair.getPrivate().getEncoded());
        String clientPublicKey = Base64Encoder.encode(clientKeyPair.getPublic().getEncoded());
        // 模拟生成服务端的rsa秘钥对;
        KeyPair serverKeyPair = keyPairGenerator.generateKeyPair();
        String serverPrivateKey = Base64Encoder.encode(serverKeyPair.getPrivate().getEncoded());
        String serverPublicKey = Base64Encoder.encode(serverKeyPair.getPublic().getEncoded());

        System.out.println("客户端公钥和私钥:");
        System.out.println("client公钥:" + clientPublicKey);
        System.out.println("client私钥:" + clientPrivateKey);
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("服务端公钥和私钥:");
        System.out.println("server公钥:" + serverPublicKey);
        System.out.println("server私钥:" + serverPrivateKey);
        System.out.println("-------------------------------------------------------------------------");

        // client对request入参包装  ---start
        // 使用client端私钥生成明文的签名;
        System.out.println("client request入参包装:");
        byte[] signByte = SecureUtil.sign(SignAlgorithm.MD5withRSA, clientPrivateKey, null).sign(content.getBytes(StandardCharsets.UTF_8));
        String contentSign = Base64Encoder.encode(signByte);
        System.out.println("client 入参签名:" + contentSign);

        // 生成随机的aeskey 加密明文;
        String clientAesKey = Base64Encoder.encode(KeyUtil.generateKey("AES").getEncoded());
        System.out.println("client 生成aeskey:" + clientAesKey);
        String encryptedContent = SecureUtil.aes(Base64Decoder.decode(clientAesKey)).encryptBase64(content);
        System.out.println("client 使用aesKey加密content得到密文:" + encryptedContent);

        // 使用server端公钥加密 aeskey;
        String encryptedClientAesKey = SecureUtil.rsa(null, serverPublicKey).encryptBase64(Base64Decoder.decode(clientAesKey), KeyType.PublicKey);
        System.out.println("client server公钥加密后的aesKey:" + encryptedClientAesKey);

        System.out.println("-------------------------------------------------------------------------");
        // client对request入参包装  ---end

        // server接受参数 解密 并验证签名;
        System.out.println("server 接受参数 解密 并验证签名");

        // server解密入参; 使用私钥解密出 aesKey ==> 使用aesKey解密出入参明文 decryptedContent ==> 使用 client_public_key 验证签名;
        byte[] decryptAesKey = SecureUtil.rsa(serverPrivateKey, null).decrypt(encryptedClientAesKey, KeyType.PrivateKey);
        System.out.println("server 解密后的 aes秘钥:" + Base64Encoder.encode(decryptAesKey));
        String decryptContent = SecureUtil.aes(decryptAesKey).decryptStr(encryptedContent);
        System.out.println("server 解密后的明文:" + decryptContent);

        // 验证签名;
        boolean verify = SecureUtil.sign(SignAlgorithm.MD5withRSA, null, clientPublicKey).verify(decryptContent.getBytes(StandardCharsets.UTF_8), Base64Decoder.decode(contentSign));
        System.out.println("server 签名验证结果:" + verify);

        System.out.println("------------------------------------------------------------------------");
        // server端对 response处理  签名, 加密;
        System.out.println("server response处理  签名, 加密");
        // 1.server 生成 aeskey;
        String responseJson = "haha i am response";
        String serverAesKey = Base64Encoder.encode(KeyUtil.generateKey("AES").getEncoded());
        System.out.println("server 生成 aes 秘钥:" + serverAesKey);
        // 2. server使用自己的私钥计算签名;
        byte[] signByteRes = SecureUtil.sign(SignAlgorithm.MD5withRSA, serverPrivateKey, null).sign(responseJson.getBytes(StandardCharsets.UTF_8));
        String responseSign = Base64Encoder.encode(signByteRes);

        System.out.println("server 对响应数据签名:" + responseJson);

        // 3. server 使用serverAesKey 加密数据;
        String encryptResponse = SecureUtil.aes(Base64Decoder.decode(serverAesKey)).encryptBase64(responseJson);
        System.out.println("server aes加密后的响应密文:" + encryptResponse);

        // 4. server 对aes秘钥进行加密;
        String encryptedServerAesKey = SecureUtil.rsa(null, clientPublicKey).encryptBase64(Base64Decoder.decode(serverAesKey), KeyType.PublicKey);
        System.out.println("server AES秘钥 加密后:" + encryptedServerAesKey);

        System.out.println("------------------------------------------------------------------------");

        // client端 处理response;
        System.out.println("client 处理response 解密 计算签名");

        // 1. 解密出aes秘钥:
        byte[] decryptServerAesKeyByte = SecureUtil.rsa(clientPrivateKey, null).decrypt(encryptedServerAesKey, KeyType.PrivateKey);

        System.out.println("client 解密出server 的aes秘钥:" + Base64Encoder.encode(decryptServerAesKeyByte));

        // 2. 解密出响应的明文;
        String decryptResponse = SecureUtil.aes(decryptServerAesKeyByte).decryptStr(encryptResponse);
        System.out.println("client 解密出响应的明文:" + decryptResponse);
        // 3. 验证签名;
        boolean verfiy2 = SecureUtil.sign(SignAlgorithm.MD5withRSA, null, serverPublicKey).verify(decryptResponse.getBytes(StandardCharsets.UTF_8), Base64Decoder.decode(responseSign));
        System.out.println("client 验签结果:" + verfiy2);

    }
}
