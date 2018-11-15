package com.tan.mvc.pojo;

import lombok.Data;

@Data
public class ResponseMessage {
    public ResponseMessage() {
    }

    public ResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    private String responseMessage;

}
