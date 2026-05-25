package com.bookmyroute.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SupportReplyRequest {

    @NotBlank
    @Size(min = 5, max = 2000)
    private String reply;

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
}
