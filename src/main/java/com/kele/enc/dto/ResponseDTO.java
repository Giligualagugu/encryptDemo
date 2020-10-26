package com.kele.enc.dto;

import lombok.Data;

@Data
public class ResponseDTO<T> {

    private String code;
    private String message;

    private T result;
}
