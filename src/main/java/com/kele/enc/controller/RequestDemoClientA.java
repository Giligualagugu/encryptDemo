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
import java.util.concurrent.TimeUnit;

public class RequestDemoClientA {


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
        TimeUnit.MILLISECONDS.sleep(1000);
//        sendRequest();
//        TimeUnit.MILLISECONDS.sleep(1000);
//        sendRequest();
//        TimeUnit.MILLISECONDS.sleep(1000);
//        sendRequest();
    }

    private static void sendRequest() {
        System.out.println("模拟客户端发送:");
        Map<String, Object> map = new HashMap<>();
        map.put("kele", "heheda~");
        map.put("qyId","123");
        map.put("taskId","1231");
        String content = JSON.toJSONString(map);

        // 请求方 公私钥; TODO 调用方自行生成 长度为1024;  clientPublicKey 发送给yzf;
        String clientPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJRPRQ2Rgwd6UVXagCuwI1ELJU3WTcy4tobmbM7eE8XU1/YFHaRBWvl+Su5grUSCV4GPT0slt6Y4Gag8xlVhkQAu58yg4tGyXI6LajsPkiZCcLBkk4GcjhXZmm66g6hwcaIT8U/ucibcfEt2FWdAOp++LBL9lqAhiBaQNxwUbSwZAgMBAAECgYB/kSuRPy9YWELruPT0Hx5RkGe26vWieOMvZ7YBvg195dUYnU/gwAJzhckH/4op2n9QRqdquPeddrXC+vuR50kjZs8AczspZQAJtnDJ3RbQNc5S+SM06hhZagZ9U7sG2V56y/6sL4Rn5blPbnjrCpEpXq0CTOSmQxlDR+B2KgTGLQJBAP75Ts4uZ6dENhy6jRXafWZ+lq2m/tSbJGfFoRMEtPOwkMfmsJ8/eofBUusgMHGdyHkMRlVUhbQCqfRjj8xs+2sCQQCU6BGhCtnupEGkSRI1PgS1pD/M+c7D8maBUCmqWKRrtqvzRjevJQ1Cw4x7iKx2Q7QnDeSeKbz8fm7zjqMwrTuLAkEAiGzo55UYzPzIX1LJzpmbY030XXHvA98G+ada6gworgMZVHxwoKSccSrVrQ0KHqCn9WzF7T5OU+Tg/uyOsnjVhQJAVGUTWMrlqbO+4HXps/Nqh+iHoI0U+0mvfysH8lw7FDF8U21Asiu10Fk0je9wj7bfrCLbqCjPy+t8xt3Y8AW+8QJAYuSlZb9zMsJAkc2wV85WWJOQEx7rNA+TEmlTjAmGCET6bZSOvYouJ80VNBz2G/oNAii63vhqPOzYHQ1plrqWgA==";
        String clientPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUT0UNkYMHelFV2oArsCNRCyVN1k3MuLaG5mzO3hPF1Nf2BR2kQVr5fkruYK1EgleBj09LJbemOBmoPMZVYZEALufMoOLRslyOi2o7D5ImQnCwZJOBnI4V2ZpuuoOocHGiE/FP7nIm3HxLdhVnQDqfviwS/ZagIYgWkDccFG0sGQIDAQAB";

        // yzf 的公钥;
        String serverPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjEGygZ8jFNPgkFXfP7IaDhf2Uwicl29IedvGDApk0hFIFAIdG7upRuQPoukroMyVYT/JZ9DD7BcQ40rMLthoqFvVQJwt5Xl98CgL4K3LmGuWuO+PvlOsAP1Pdaew/Zw1xZc5MAXJBzaudU968okTwFdv/pH5I0W7qB0DRCaHEsOTP+i7Md8+FMs6kYFBQZLihus2XxXuhhjrD9KZKQK4duRtRuxLM37F/NWukUo+kLMgZpxfw+azlm5KiluifWdaYg0gEtgROFArddAnxKNSXH2tTm7C+mKYwTr9notT/YliLBMojW7IQLTYkx9cUOBeegFc2ga3dQysUbZPpZQB1wIDAQAB";

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
        ServerRequestDTO serverRequestDTO = new ServerRequestDTO(encryptedContent);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("x-app-token", "94944fa85e0846b1b39212103e71567e"); // todo app-id  切换成配置好的;
        httpHeaders.set("x-aes-key", encryptedClientAesKey);
        httpHeaders.set("x-rsa-sign", contentSign);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        HttpEntity<ServerRequestDTO> httpEntity = new HttpEntity<>(serverRequestDTO, httpHeaders);

        // String url = "http://localhost:30082/taxloan/test/for-security";// 此接口可以用于联调测试 加密解密和验签流程;
        String url = "http://localhost:30082/taxloan/v1/ftd/invoice/query";// 此接口可以用于联调测试 加密解密和验签流程;

        ResponseEntity<JSONObject> hashMapResponseEntity = restTemplate.postForEntity(url, httpEntity, JSONObject.class);


        if (hashMapResponseEntity.getStatusCode() == HttpStatus.OK) {
            JSONObject body = hashMapResponseEntity.getBody();
            System.out.println("响应结果:" + body);

            HttpHeaders responseEntityHeaders = hashMapResponseEntity.getHeaders();
            String responseAesKey = responseEntityHeaders.getFirst("x-aes-key");
            String responseSign = responseEntityHeaders.getFirst("x-rsa-sign");
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

        public ServerRequestDTO(String encryptedContent) {
            this.data = encryptedContent;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
