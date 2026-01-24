
package com.meevent.webapi.service.IMailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.SyncPoller;

@Service
@Profile("dev")
@Primary
public class AzureMailServiceImpl implements IMailService{



    @Value("${azure.connection.string}")
    private String connectionString;
    @Value("${azure.domain.name}")
    private String fromEmail;
    @Override
    public void sendVerificationEmail(String toUser, String subject, String message, String token) {

        try {
            EmailClient emailClient = new EmailClientBuilder().
            connectionString(connectionString).buildClient();
            String htmlContent = "<html><body>" +
                                 "<h1>" + subject + "</h1>" +
                                 "<p>" + message + "</p>" +
                                 "<p>Tu código de verificación es: <b>" + token + "</b></p>" +
                                 "</body></html>";

            EmailMessage emailMessage = new EmailMessage()
                    .setSenderAddress(fromEmail)
                    .setToRecipients(new EmailAddress(toUser))
                    .setSubject(subject)
                    .setBodyHtml(htmlContent);

            SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
            poller.waitForCompletion();
            
            System.out.println("Correo de verificación enviado con Azure a: " + toUser);
        } catch (Exception e) {
            System.err.println("Error en Azure Mail: " + e.getMessage());
        }

    }
    
    

}


