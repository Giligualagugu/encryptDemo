package com.kele.enc.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseDTO<T> {

    private String code;
    private String message;

    private T result;
}
