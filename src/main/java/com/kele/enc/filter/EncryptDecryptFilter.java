package com.kele.enc.filter;

import com.alibaba.fastjson.JSONObject;
import com.kele.enc.wrapper.BizRequestWrapper;
import com.kele.enc.wrapper.BizResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@WebFilter(urlPatterns = "/demo-enc")
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class EncryptDecryptFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("filter: decrypt request body ");

        String method = request.getMethod();
        System.out.println("请求类型:"+method);
        BizRequestWrapper bizRequestWrapper = new BizRequestWrapper(request);
        BizResponseWrapper bizResponseWrapper = new BizResponseWrapper(response);

        byte[] body = bizRequestWrapper.getBody();
        String json = new String(body, StandardCharsets.UTF_8);
        System.out.println("入参json:" + json);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("kele", "11");
        bizRequestWrapper.setBody(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
        chain.doFilter(bizRequestWrapper, bizResponseWrapper);
        byte[] responseBody = bizResponseWrapper.getResponseData();
        System.out.println("返回参数" + new String(responseBody, StandardCharsets.UTF_8));
        JSONObject res = new JSONObject();
        res.put("dandan", "22");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(res.toJSONString().getBytes());

    }
}
