package life2food.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            if (fromEmail == null || fromEmail.isEmpty()) {
                System.err.println("Error: No se ha configurado el email remitente (spring.mail.username)");
                return;
            }

            if (toEmail == null || toEmail.isEmpty()) {
                System.err.println("Error: El email del destinatario está vacío");
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("¡Bienvenido a Life2Food!");

            String htmlContent = getWelcomeEmailTemplate(firstName != null ? firstName : "");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Email de bienvenida enviado exitosamente a: " + toEmail);
        } catch (MessagingException e) {
            // Log the error but don't fail user creation
            System.err.println("Error al enviar email de bienvenida: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error al enviar email de bienvenida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getWelcomeEmailTemplate(String firstName) {
        return "<!DOCTYPE html>" +
                "<html lang=\"es\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Bienvenido a Life2Food</title>" +
                "    <style>" +
                "        * {" +
                "            margin: 0;" +
                "            padding: 0;" +
                "            box-sizing: border-box;" +
                "        }" +
                "        body {" +
                "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;"
                +
                "            background-color: #f5f7fa;" +
                "            line-height: 1.6;" +
                "            color: #333333;" +
                "        }" +
                "        .email-container {" +
                "            max-width: 600px;" +
                "            margin: 0 auto;" +
                "            background-color: #ffffff;" +
                "        }" +
                "        .header {" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" +
                "            padding: 40px 30px;" +
                "            text-align: center;" +
                "            color: #ffffff;" +
                "        }" +
                "        .header h1 {" +
                "            font-size: 32px;" +
                "            font-weight: 700;" +
                "            margin: 0;" +
                "            letter-spacing: -0.5px;" +
                "        }" +
                "        .header .logo-text {" +
                "            font-size: 18px;" +
                "            margin-top: 10px;" +
                "            opacity: 0.95;" +
                "        }" +
                "        .content {" +
                "            padding: 40px 30px;" +
                "        }" +
                "        .greeting {" +
                "            font-size: 24px;" +
                "            font-weight: 600;" +
                "            color: #2d3748;" +
                "            margin-bottom: 20px;" +
                "        }" +
                "        .content p {" +
                "            font-size: 16px;" +
                "            color: #4a5568;" +
                "            margin-bottom: 20px;" +
                "            line-height: 1.8;" +
                "        }" +
                "        .highlight-box {" +
                "            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);" +
                "            border-radius: 12px;" +
                "            padding: 30px;" +
                "            margin: 30px 0;" +
                "            text-align: center;" +
                "            color: #ffffff;" +
                "        }" +
                "        .highlight-box h2 {" +
                "            font-size: 22px;" +
                "            margin-bottom: 10px;" +
                "            font-weight: 600;" +
                "        }" +
                "        .highlight-box p {" +
                "            color: #ffffff;" +
                "            font-size: 16px;" +
                "            margin: 0;" +
                "        }" +
                "        .cta-button {" +
                "            display: inline-block;" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" +
                "            color: #ffffff !important;" +
                "            text-decoration: none;" +
                "            padding: 16px 40px;" +
                "            border-radius: 8px;" +
                "            font-weight: 600;" +
                "            font-size: 16px;" +
                "            margin: 30px 0;" +
                "            text-align: center;" +
                "            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);" +
                "        }" +
                "        .features {" +
                "            margin: 30px 0;" +
                "        }" +
                "        .feature-item {" +
                "            display: flex;" +
                "            align-items: center;" +
                "            margin-bottom: 20px;" +
                "            padding: 15px;" +
                "            background-color: #f7fafc;" +
                "            border-radius: 8px;" +
                "            border-left: 4px solid #667eea;" +
                "        }" +
                "        .feature-icon {" +
                "            font-size: 24px;" +
                "            margin-right: 15px;" +
                "        }" +
                "        .feature-text {" +
                "            flex: 1;" +
                "            font-size: 15px;" +
                "            color: #4a5568;" +
                "        }" +
                "        .footer {" +
                "            background-color: #2d3748;" +
                "            padding: 30px;" +
                "            text-align: center;" +
                "            color: #a0aec0;" +
                "        }" +
                "        .footer p {" +
                "            font-size: 14px;" +
                "            margin-bottom: 10px;" +
                "        }" +
                "        .footer a {" +
                "            color: #667eea;" +
                "            text-decoration: none;" +
                "        }" +
                "        .footer-links {" +
                "            margin-top: 20px;" +
                "        }" +
                "        .footer-links a {" +
                "            margin: 0 10px;" +
                "            color: #a0aec0;" +
                "            text-decoration: none;" +
                "            font-size: 13px;" +
                "        }" +
                "        .divider {" +
                "            height: 1px;" +
                "            background: linear-gradient(to right, transparent, #e2e8f0, transparent);" +
                "            margin: 30px 0;" +
                "        }" +
                "        @media only screen and (max-width: 600px) {" +
                "            .content {" +
                "                padding: 30px 20px;" +
                "            }" +
                "            .header {" +
                "                padding: 30px 20px;" +
                "            }" +
                "            .header h1 {" +
                "                font-size: 26px;" +
                "            }" +
                "            .cta-button {" +
                "                display: block;" +
                "                margin: 20px 0;" +
                "            }" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"email-container\">" +
                "        <div class=\"header\">" +
                "            <h1>🌱 Life2Food</h1>" +
                "            <div class=\"logo-text\">Rescatando alimentos, transformando vidas</div>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <div class=\"greeting\">¡Hola " + firstName + "! 👋</div>" +
                "            <p>¡Estamos emocionados de darte la bienvenida a <strong>Life2Food</strong>!</p>" +
                "            <p>Te has unido a una comunidad comprometida con reducir el desperdicio de alimentos y apoyar a nuestra comunidad local.</p>"
                +
                "            <div class=\"highlight-box\">" +
                "                <h2>🎉 ¡Gracias por ser parte del cambio!</h2>" +
                "                <p>Cada acción cuenta para crear un mundo más sostenible</p>" +
                "            </div>" +
                "            <p>Con Life2Food puedes:</p>" +
                "            <div class=\"features\">" +
                "                <div class=\"feature-item\">" +
                "                    <div class=\"feature-icon\">🛒</div>" +
                "                    <div class=\"feature-text\"><strong>Explorar ofertas</strong> de restaurantes, supermercados y granjas locales</div>"
                +
                "                </div>" +
                "                <div class=\"feature-item\">" +
                "                    <div class=\"feature-icon\">💚</div>" +
                "                    <div class=\"feature-text\"><strong>Rescatar alimentos</strong> excedentes a precios increíbles</div>"
                +
                "                </div>" +
                "                <div class=\"feature-item\">" +
                "                    <div class=\"feature-icon\">🌍</div>" +
                "                    <div class=\"feature-text\"><strong>Contribuir</strong> a reducir el desperdicio alimentario</div>"
                +
                "                </div>" +
                "                <div class=\"feature-item\">" +
                "                    <div class=\"feature-icon\">🤝</div>" +
                "                    <div class=\"feature-text\"><strong>Apoyar</strong> a negocios locales y a tu comunidad</div>"
                +
                "                </div>" +
                "            </div>" +
                "            <div style=\"text-align: center;\">" +
                "                <a href=\"https://life2food.com\" class=\"cta-button\">Explorar Ofertas Ahora</a>" +
                "            </div>" +
                "            <div class=\"divider\"></div>" +
                "            <p style=\"font-size: 14px; color: #718096;\">Si tienes alguna pregunta o necesitas ayuda, no dudes en contactarnos. Estamos aquí para ayudarte.</p>"
                +
                "            <p style=\"margin-top: 30px;\">¡Bienvenido a la familia Life2Food!<br><strong>El equipo de Life2Food</strong></p>"
                +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>&copy; 2025 Life2Food. Todos los derechos reservados.</p>" +
                "            <div class=\"footer-links\">" +
                "                <a href=\"https://life2food.com\">Visita nuestro sitio</a> | " +
                "                <a href=\"https://life2food.com/terminos\">Términos</a> | " +
                "                <a href=\"https://life2food.com/privacidad\">Privacidad</a>" +
                "            </div>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    public void sendOrderNotificationToStore(life2food.backend.model.User storeOwner,
            java.util.List<life2food.backend.model.OrderItem> items,
            life2food.backend.model.Order order) {
        try {
            if (fromEmail == null || fromEmail.isEmpty()) {
                System.err.println("Error: No se ha configurado el email remitente (spring.mail.username)");
                return;
            }

            String toEmail = storeOwner.getEmail();
            if (toEmail == null || toEmail.isEmpty()) {
                System.err.println("Error: El email del tendero está vacío para el usuario ID: " + storeOwner.getId());
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("¡Nueva Orden #" + order.getId() + " para preparar!");

            String htmlContent = getOrderNotificationTemplate(storeOwner.getFirst_name(), items, order.getId());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Email de notificación de orden enviado exitosamente a: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Error al enviar email de notificación de orden: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error al enviar email de notificación de orden: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getOrderNotificationTemplate(String storeOwnerName,
            java.util.List<life2food.backend.model.OrderItem> items, Long orderId) {
        StringBuilder itemsHtml = new StringBuilder();
        for (life2food.backend.model.OrderItem item : items) {
            itemsHtml.append("<tr>")
                    .append("<td style=\"padding: 12px; border-bottom: 1px solid #e2e8f0;\">")
                    .append(item.getProduct().getName())
                    .append("</td>")
                    .append("<td style=\"padding: 12px; border-bottom: 1px solid #e2e8f0; text-align: center;\">")
                    .append(item.getQuantity())
                    .append("</td>")
                    .append("</tr>");
        }

        return "<!DOCTYPE html>" +
                "<html lang=\"es\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Nueva Orden</title>" +
                "    <style>" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }" +
                "        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f5f7fa; line-height: 1.6; color: #333; }"
                +
                "        .email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; }" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; color: #ffffff; }"
                +
                "        .content { padding: 30px; }" +
                "        .greeting { font-size: 20px; font-weight: 600; margin-bottom: 20px; color: #2d3748; }" +
                "        .order-info { background-color: #f7fafc; padding: 15px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #667eea; }"
                +
                "        table { width: 100%; border-collapse: collapse; margin-top: 20px; }" +
                "        th { background-color: #edf2f7; padding: 12px; text-align: left; font-weight: 600; color: #4a5568; }"
                +
                "        .footer { background-color: #2d3748; padding: 20px; text-align: center; color: #a0aec0; font-size: 14px; }"
                +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"email-container\">" +
                "        <div class=\"header\">" +
                "            <h1>¡Nueva Orden Recibida! 📦</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <div class=\"greeting\">Hola " + (storeOwnerName != null ? storeOwnerName : "Tendero")
                + ",</div>" +
                "            <p>Has recibido una nueva orden en Life2Food. Por favor, alista los siguientes productos:</p>"
                +
                "            <div class=\"order-info\">" +
                "                <p><strong>Orden #:</strong> " + orderId + "</p>" +
                "            </div>" +
                "            <table>" +
                "                <thead>" +
                "                    <tr>" +
                "                        <th>Producto</th>" +
                "                        <th style=\"text-align: center;\">Cantidad</th>" +
                "                    </tr>" +
                "                </thead>" +
                "                <tbody>" +
                itemsHtml.toString() +
                "                </tbody>" +
                "            </table>" +
                "            <p style=\"margin-top: 30px;\">Gracias por ser parte de Life2Food.</p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>&copy; 2025 Life2Food Business</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}
