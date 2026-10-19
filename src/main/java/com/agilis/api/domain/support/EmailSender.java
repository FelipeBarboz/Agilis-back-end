package com.agilis.api.domain.support;

public interface EmailSender {

    void send(String to, String subject, String body);
}