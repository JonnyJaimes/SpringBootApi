package com.bezkoder.springjwt.EMAIL;

public interface EmailSender {

    void send(String to, String email);
}