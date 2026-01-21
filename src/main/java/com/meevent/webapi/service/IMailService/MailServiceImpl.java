package com.meevent.webapi.service.IMailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${email.sender}")
    private String emailUser;

    @Override
    @Async
    public void sendVerificationEmail(String toUser, String subject, String message, String token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailUser);
            helper.setTo(toUser);
            helper.setSubject(subject);

            // Diseñamos el cuerpo del correo en HTML
            String htmlContent
                    = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>"
                    + "<h2 style='color: #2e7d32; text-align: center;'>¡Bienvenido a Meevent!</h2>"
                    + "<p style='font-size: 16px; color: #333;'>Hola,</p>"
                    + "<p style='font-size: 16px; color: #333; line-height: 1.5;'>" + message + "</p>"
                    + "<div style='text-align: center; margin: 30px 0;'>"
                    + "<a href='" + token + "' style='background-color: #2e7d32; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>Confirmar mi cuenta</a>"
                    + "</div>"
                    + "<p style='font-size: 12px; color: #777;'>Si el botón no funciona, copia y pega el siguiente enlace en tu navegador:</p>"
                    + "<p style='font-size: 12px; color: #777; word-break: break-all;'>" + token + "</p>"
                    + "<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>"
                    + "<p style='font-size: 11px; color: #999; text-align: center;'>Este es un correo automático, por favor no respondas a este mensaje.</p>"
                    + "</div>";

            helper.setText(htmlContent, true); /* true allows the html support */
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al construir el correo: " + e.getMessage());
        }
    }

}
