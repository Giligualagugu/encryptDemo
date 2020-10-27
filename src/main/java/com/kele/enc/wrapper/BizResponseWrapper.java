package com.kele.enc.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class BizResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);

    public BizResponseWrapper(HttpServletResponse resp) throws IOException {
        super(resp);
    }


    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener listener) {

            }

            @Override
            public void write(int b) throws IOException {
                buffer.write(b);
            }
        };
    }

    public byte[] getResponseData() throws IOException {
        return buffer.toByteArray();
    }
}


