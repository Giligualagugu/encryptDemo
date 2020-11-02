package com.kele.enc.wrapper;

import com.kele.enc.dto.ResponseDTO;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;

/**
 * 这个拦截作用于切面;  beforeBodyWrite 方法返回的参数可以被 filter获取到;
 * <p>
 * returnType= controller类的某个 接口入口方法, getContainingClass =获取到 controller的class;
 */
@RestControllerAdvice
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

    private static final Class<? extends Annotation> RESPONSE_BODY = ResponseBody.class;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), RESPONSE_BODY) || returnType.hasMethodAnnotation(RESPONSE_BODY);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof ResponseDTO) {
            return body;
        }

        System.out.println("包装返回参数");
        return new ResponseDTO<>().setCode("0").setMessage("ok").setResult(body);
    }
}
