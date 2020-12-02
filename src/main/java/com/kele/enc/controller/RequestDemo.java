package com.kele.enc.controller;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestDemo {


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
        sendRequest();
    }

    private static void sendRequest() {
        System.out.println("模拟客户端发送:");
        Map<String, Object> map = new HashMap<>();
        map.put("qyId","123");
        map.put("taskId","1231");
        String content = JSON.toJSONString(map);
        String token= "1817e2ab38c5462aba1e33af01be4879";
        // 请求方 公私钥; TODO 调用方自行生成 长度为1024;  clientPublicKey 发送给yzf;
        String clientPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC8QALIaOr406+9S0LB1RQPG3e68hg2qD8RDHtGx1AJnex2UVGmm73UMRrw5CfTxuuc3i9yUz8hGwSLfRpehzCGjhz8n69GLePghV/qsf5354BVT0ut0fw9bLRVd5LJ0Zqy2ciYeEdJw+oIDjUaBRSSnwJTC60ow/5aR2NIYdF8lX1tiwrc2baZVvq/Y8JnqkDrPqKMe0lLmbTpSSu21+Xd9KFciAev2/TlYeZWfGAf7qmeqFjB24oZg6wdw9G0GQ9X5fMyCfqUPEntkWLe1puXfVK9HRHfmHjzmajqIrlUAub84k5lP/A8/E7Bs7RxvRzz6HFyZhqCrWCsdxXZghyFAgMBAAECggEBAIz7P1pJwNey6WVRjOBPLKOXxZY7IskkPX8OIvDB6f4ZyHJfuk+VSZg1YmDP7cfbrK6hc1UiZR/s9P1mJq5ufnf2zFyhVomvan7GRCwnO1UfCexZ42p83XQ/CdsMHuGIWVviYClXwsPKtmVsBnbDIUIhVYNONlNWXRHNbtxRbarnnOL+P8ZWBf372lcKbrR3gSceC7V2reoab05kCrYii7v9eI1C7hroQ6YJIiiMy/YeDRWwiDtatVqh7lZVIz88llEMP4iLfxlcj0QcQOpeuEchCiEOeC53dA8nV3SU0TsqNQ4fPIbJtgait/IIJ+9auAgu9DYla0aKR5UZ7shrJWECgYEA3LmNrwcMT+ACGx+84RVF0TvpJBNd2QeU1wZ18zEKPoM1yVVtDj+y2twRz/uhfIooURMMU7KDkx1decVs3r/hPjlnyzihjZy2mEzeXiM2x+f9tvBD5OzLpl2/HocSg2JapuPpWLWPOzHu/TkOwbchpukWO01bbyOTuFP7JEpM2L0CgYEA2lXTsLxq8zabZ/DejOn64Sh3WzoRm2Lr1BTD5Az/TeOdLtuSO7eNrSdQzlynnUaOXj4P2qp8FLqxnJtIZ3gjfN0n6KOYp0abZNC8FrgcWN5IfGfHAJPPhDa61IPpU9C6J8IzJlb7llCPXNkpQQg3SdFDnOLmtC0TwKpNgzXvA2kCgYAh+c39azBR+0FXu4Lvgr7Eflxc2T5Q8gHPN4dgb+QLy1a0LxoiX1vApK95PFqXOQ0Lrcrv90KZmqOQ4vrnLtIVKfD4Lzdu/Ph+aRaxluoPy34actGAo5KVgUiyAkivQKyDlAGPTiNGEWGgSPAkkXP0pVxutHGmoetTLFo0M9uwYQKBgDwWKrAU6ooMmTA9xcGaEwUsEcz1aisNSkGPhWmZehmBbpyaqN/uX/srOr3QGgDajgu5oLtY99TsnDyxL3fqRDn00yre+v/uGo3gNCM8f/ClG+XA2a96/j0worIhq3D5nIGcnF8MyShQ+2NeEFBpZm3WPXlLqJolXa5EMBjQGvshAoGBAL/SRVOajz6QIw4IL1jcf16PEqbACzaa555qqtjAOI2cAWLmtVjCmRQHW0e/DjyzMkIqJffkpQihcbSraIonZt8NnESLAZSJv3WajE+9e1Jh+HPvY9Mj5wx+EZnrv0AVet9LGxhJm3AgwjPy9pKJLzisrE/Cn/MROZadPWUWwrAc";
        String clientPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7ZUg5hbqP6XqhOJtUrENyxdrS57u6qbjsMU7/TQr2QnRiXv9A3fs2Ks/cbQuu97O9O1XjqEhDnfPUi75ySuJxufqsGiCEpmrS39IeDObdBAU9/FRPTFjh4gvGAaJzVtI6DcfC+xBHgJDxZ39hPEfGo0FEfu2WTiWeRA/pA9NCZdpdf26Mx/uDTmlOv0R1RkAWj8IYEn3rYOXSfeDkXoqv2ytwMmKfSsuB89fZGfUpyZpfMtixQqeCna/mhKSwk3/YVsSEcFpRwNNtRqHyLzCklD9bqPaMqWkwy1rWqDchto4Y5s7pqh8edcRiLh5XnCJUeimtJfE/XOjfx4Ie66aIwIDAQAB";

        // yzf 的公钥;
        String serverPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzLzlqZposiamvLtTbvTrM4SYiAp/LPn6tA7k7ZehQ7m+KWO/HuyLhzE3YuBujlo6K/x8YHObPI34a5bvDDJr86U/LdXgBePL4NnmLstrAMCOIBNUDcVGE+a+vahRlcfzn8A8rqtWa78s7LX4UMajU6QcBtX5eLSL8Zwu2RU3IRmbs6uoG5YoNddBid+apEKYE8udRugCtaB/LMAFXSoCE7+r2t5QOYf1REM8vgdgy2LevW0QP84J+ozkcED7KO13aYdLnBnB4Aq1JOKZYOKLXooCgCNPNHAdhOqHS2mWiQqS/9eGmvZdJKgAI9FZz08YC9OYekg2HaV2r8r6kNoeRQIDAQAB";

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


        RestTemplate restTemplate = new RestTemplate();
        ServerRequestDTO serverRequestDTO = new ServerRequestDTO(encryptedContent,contentSign);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("x-app-token", token);
        httpHeaders.set("x-aes-key", encryptedClientAesKey);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        HttpEntity<ServerRequestDTO> httpEntity = new HttpEntity<>(serverRequestDTO, httpHeaders);

         String url = "http://221.226.82.254:30083/taxloan/test/for-security";// 此接口可以用于联调测试 加密解密和验签流程;

        ResponseEntity<JSONObject> hashMapResponseEntity = restTemplate.postForEntity(url, httpEntity, JSONObject.class);

        if (hashMapResponseEntity.getStatusCode() == HttpStatus.OK) {
            JSONObject body = hashMapResponseEntity.getBody();
            System.out.println("响应结果:" + body);

            HttpHeaders responseEntityHeaders = hashMapResponseEntity.getHeaders();
            String responseAesKey = responseEntityHeaders.getFirst("x-aes-key");
            String responseSign = body.getString("sign");
            System.out.println("响应aeskey:" + responseAesKey);
            System.out.println("响应签名:" + responseSign);

            String result = body.getString("result");
            if (StringUtils.isNotBlank(result)) {
                // 解密aeskey;
                byte[] decryptAesKey = SecureUtil.rsa(clientPrivateKey, null).decrypt(responseAesKey, KeyType.PrivateKey);
                String decryptContent = SecureUtil.aes(decryptAesKey).decryptStr(result);
                System.out.println("响应明文:" + decryptContent);
                // 验证签名
                boolean verfiy = SecureUtil.sign(SignAlgorithm.MD5withRSA, null, serverPublicKey).verify(decryptContent.getBytes(StandardCharsets.UTF_8), Base64Decoder.decode(responseSign));
                System.out.println("client 验签结果:" + verfiy);
            }
        }
    }

    private static class ServerRequestDTO {
        private String data;
        private String sign;

        public ServerRequestDTO(String encryptedContent,String sign) {
            this.data = encryptedContent;
            this.sign=sign;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
