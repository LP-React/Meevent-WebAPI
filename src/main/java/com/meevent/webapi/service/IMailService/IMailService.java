package com.meevent.webapi.service.IMailService;


public interface IMailService {


    void sendVerificationEmail(String toUser, String subject, String message, String token);

    void sendPasswordResetEmail(String toUser, String token);

    void sendPasswordChangeConfirmationEmail(String toUser);

}
