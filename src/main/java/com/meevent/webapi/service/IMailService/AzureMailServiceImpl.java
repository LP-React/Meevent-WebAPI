package com.meevent.webapi.service.IMailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.SyncPoller;

@Service
public class AzureMailServiceImpl implements IMailService {

    @Value("${azure.connection.string}")
    private String connectionString;
    @Value("${azure.domain.name}")
    private String fromEmail;

    /* <-- Must be an allowed email */
    @Override
    public void sendVerificationEmail(String toUser, String subject, String message, String token) {

        try {
            EmailClient emailClient = new EmailClientBuilder().
                    connectionString(connectionString).buildClient();
            String brandColor = "#E2125D";

            String htmlContent = "<html><body style='margin:0; padding:0; background-color: #f4f4f4;'>"
                    + "<table width='100%' bgcolor='#f4f4f4' cellpadding='0' cellspacing='0' style='border-collapse: collapse;'>"
                    + "  <tr>"
                    + "    <td align='center' style='padding: 20px 0;'>"
                    + "      <table width='600' bgcolor='#ffffff' cellpadding='0' cellspacing='0' style='border-collapse: collapse; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>"
                    + "        <tr>"
                    + "          <td bgcolor='" + brandColor + "' align='center' style='padding: 30px;'>"
                    + "            <svg width='180' height='52' viewBox='0 0 350 102' xmlns='http://www.w3.org/2000/svg' style='display: block;'>"
                    + "              <g fill='#ffffff'>"
                    + "                <rect y='97' width='350' height='5'></rect>"
                    + "                <path d='M21.14888 19.41184 c4.40068 0 7.98056 3.580156 7.98056 7.98028 l0 10.41184 c0 1.212772 -0.98328 2.196056 -2.196056 2.196056 s-2.196056 -0.98328 -2.196056 -2.196056 l0 -10.41184 c0 -1.9784 -1.609728 -3.588124 -3.5884 -3.588124 c-1.9784 0 -3.588124 1.609452 -3.588124 3.588124 l0 10.41184 c0 1.212772 -0.98328 2.196056 -2.196056 2.196056 s-2.196056 -0.98328 -2.196056 -2.196056 l0 -10.41184 c0 -1.9784 -1.609452 -3.588124 -3.588124 -3.588124 s-3.5884 1.609452 -3.5884 3.588124 l0 10.41184 c0 1.212772 -0.98328 2.196056 -2.196056 2.196056 s-2.196056 -0.98328 -2.196056 -2.196056 l0 -16.196 c0 -1.212772 0.98328 -2.196056 2.196056 -2.196056 c0.785936 0 1.473592 0.41422 1.861484 1.034884 c1.159532 -0.65746 2.497772 -1.03492 3.923008 -1.03492 z M41.34808 19.41184 c5.67628 0 10.29408 4.61772 10.29408 10.29404 c0 1.212772 -0.98328 2.196056 -2.196056 2.196056 l-13.57636 0 c0.872928 2.17 2.999532 3.70586 5.47836 3.70586 c1.576484 0 3.058556 -0.613788 4.17332 -1.728556 c0.857852 -0.85758 2.247968 -0.857304 3.10582 0 c0.857852 0.857852 0.85758 2.248244 0 3.10582 c-1.944608 1.944336 -4.52964 3.01492 -7.27916 3.01492 c-5.67628 0 -10.29408 -4.61776 -10.29408 -10.29408 s4.61776 -10.29408 10.29408 -10.29408 z M35.86972 27.5098 l10.95672 0 c-0.872928 -2.17 -2.999532 -3.70586 -5.47836 -3.70586 s-4.60544 1.53586 -5.47836 3.70586 z M63.88208 19.41184 c5.67628 0 10.29408 4.61772 10.29408 10.29404 c0 1.212772 -0.98328 2.196056 -2.196056 2.196056 l-13.57636 0 c0.872928 2.17 2.999532 3.70586 5.47836 3.70586 c1.576484 0 3.058556 -0.613788 4.17332 -1.728556 c0.857852 -0.85758 2.247968 -0.857304 3.10582 0 c0.857852 0.857852 0.85758 2.248244 0 3.10582 c-1.944608 1.944336 -4.52964 3.01492 -7.27916 3.01492 c-5.67628 0 -10.29408 -4.61776 -10.29408 -10.29408 s4.61776 -10.29408 10.29408 -10.29408 z M58.40372 27.5098 l10.95672 0 c-0.872928 -2.17 -2.999532 -3.70586 -5.47836 -3.70586 s-4.60544 1.53586 -5.47836 3.70586 z M93.49676 19.6438 c1.084572 0.542148 1.524336 1.861444 0.981916 2.946016 l-8.098 16.196 c-0.371953 0.743908 -1.132344 1.213868 -1.9641 1.213868 s-1.592148 -0.46996 -1.9641 -1.213868 l-8.098 -16.196 c-0.54242 -1.084844 -0.102656 -2.403868 0.981916 -2.946288 c1.084844 -0.54242 2.403868 -0.10293 2.946288 0.982188 l6.13392 12.26752 l6.13392 -12.26728 c0.542148 -1.085116 1.861172 -1.524064 2.946288 -0.982188 z M104.95008 19.41184 c5.67628 0 10.29408 4.61772 10.29408 10.29404 c0 1.212772 -0.98328 2.196056 -2.196056 2.196056 l-13.57636 0 c0.872928 2.17 2.999532 3.70586 5.47836 3.70586 c1.576484 0 3.058556 -0.613788 4.17332 -1.728556 c0.857852 -0.85758 2.247968 -0.857304 3.10582 0 c0.857852 0.857852 0.85758 2.248244 0 3.10582 c-1.944608 1.944336 -4.52964 3.01492 -7.27916 3.01492 c-5.67628 0 -10.29408 -4.61776 -10.29408 -10.29408 s4.61776 -10.29408 10.29408 -10.29408 z M99.47172 27.5098 l10.95672 0 c-0.872928 -2.17 -2.999532 -3.70586 -5.47836 -3.70586 s-4.60544 1.53586 -5.47836 3.70586 z M125.17024 19.41184 c4.4004 0 7.9802 3.580156 7.9802 7.98028 l0 10.41184 c0 1.212772 -0.98328 2.196056 -2.196056 2.196056 s-2.196056 -0.98328 -2.196056 -2.196056 l0 -10.41184 c0 -1.9784 -1.609452 -3.588124 -3.588124 -3.588124 s-3.588124 1.609452 -3.588124 3.588124 l0 10.41184 c0 1.212772 -0.98328 2.196056 -2.196056 2.196056 s-2.196056 -0.98328 -2.196056 -2.196056 l0 -16.196 c0 -1.212772 0.98328 -2.196056 2.196056 -2.196056 c0.785936 0 1.47332 0.41422 1.861484 1.034884 c1.159256 -0.65746 2.4975 -1.03492 3.922736 -1.03492 z M145.5828 35.60784 c1.212772 0 2.196092 0.98332 2.196092 2.196092 s-0.98328 2.196056 -2.196056 2.196056 c-5.76688 0 -10.4588 -4.69192 -10.4588 -10.4588 l0 -15.34504 c0 -1.212772 0.98328 -2.196056 2.196056 -2.196056 s2.196056 0.98328 2.196056 2.196056 l0 5.22172 l6.06664 0 c1.212772 0 2.196056 0.98328 2.196056 2.196056 s-0.98328 2.196056 -2.196056 2.196056 l-6.06664 0 l0 5.7312 c0 3.345156 2.721484 6.06664 6.06664 6.06664 z'></path>"
                    + "              </g>"
                    + "            </svg>"
                    + "          </td>"
                    + "        </tr>"
                    + "        <tr>"
                    + "          <td align='center' style='padding: 50px 40px; font-family: Arial, sans-serif;'>"
                    + "            <h2 style='color: #222222; font-size: 24px; line-height: 1.3; margin-bottom: 25px;'>Verifique su correo presionando el botón de verificación.</h2>"
                    + "            <p style='color: #555555; font-size: 16px; margin-bottom: 35px;'>" + message + "</p>"
                    + "            <a href='http://tu-frontend.com/verify?token=" + token + "' style='background-color:" + brandColor + "; color: #ffffff; padding: 16px 60px; text-decoration: none; border-radius: 50px; font-weight: bold; display: inline-block; font-size: 18px;'>Verificar cuenta</a>"
                    + "            <p style='margin-top: 45px; color: #888888; font-size: 13px;'>"
                    + "              Código de verificación:<br/>"
                    + "              <span style='font-family: monospace; background-color: #f8f8f8; padding: 8px 15px; border: 1px dashed #cccccc; display: inline-block; margin-top: 10px; color: #333;'>" + token + "</span>"
                    + "            </p>"
                    + "          </td>"
                    + "        </tr>"
                    + "        <tr>"
                    + "          <td bgcolor='#f8f8f8' align='center' style='padding: 30px; border-top: 1px solid #eeeeee;'>"
                    + "            <p style='color: #777777; font-size: 12px; margin: 0;'>© 2026 Meevent | Lima, Lima, Perú</p>"
                    + "          </td>"
                    + "        </tr>"
                    + "      </table>"
                    + "    </td>"
                    + "  </tr>"
                    + "</table>"
                    + "</body></html>";
            EmailMessage emailMessage = new EmailMessage()
                    .setSenderAddress(fromEmail) /* <--  Must be an allowed email  */
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
