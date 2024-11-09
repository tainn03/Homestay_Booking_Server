package com.homestay.service.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class EmailService {
    JavaMailSender mailSender;

    public void sendEmail(String to, String name, String link, String message, String subject, String buttonText) {
        CompletableFuture.runAsync(() -> {
            try {
                String email = buildEmail(name, link, message, subject, buttonText);
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setText(email, true);
                helper.setTo(to);
                helper.setSubject("Thư xác thực từ Huta Homestay");
                helper.setFrom("homestay.huta@gmail.com");
                mailSender.send(mimeMessage);
            } catch (MessagingException e) {
                log.error("failed to send email", e);
                throw new IllegalStateException("failed to send email");
            }
        });
    }


    private String buildEmail(String name, String link, String message, String subject, String buttonText) {
        return "<div style=\"font-family:Arial,sans-serif;background-color:#f4f4f4;padding:20px\">\n" +
                "    <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:600px;margin:auto;background-color:#ffffff;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,0.1)\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "        <tr>\n" +
                "            <td style=\"background-color:#ffffff;color:#007BFF;padding:20px;border-top-left-radius:8px;border-top-right-radius:8px;text-align:center;font-size:24px;font-weight:bold\">\n" +
                "                " + subject + "\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"padding:30px;font-size:16px;color:#333333\">\n" +
                "                <p style=\"margin:0 0 15px 0;\">Xin chào <strong>" + name + "</strong>,</p>\n" +
                "                <p style=\"margin:0 0 15px 0;\">" + message + "</p>\n" +
                "                <p style=\"margin:0 0 30px 0;\">Vui lòng bấm vào nút bên dưới để " + buttonText + ":</p>\n" +
                "                <div style=\"text-align:center;margin-bottom:30px;\">\n" +
                "                    <a href=\"" + link + "\" style=\"background-color:#28a745;color:#ffffff;text-decoration:none;padding:15px 25px;border-radius:5px;font-weight:bold;display:inline-block\">" + buttonText + "</a>\n" +
                "                </div>\n" +
                "                <p style=\"margin:0 0 15px 0;font-size:14px;color:#999999\">Liên kết sẽ hết hạn sau 1 ngày. Nếu bạn không yêu cầu chức năng này, vui lòng bỏ qua email này.</p>\n" +
                "                <p style=\"margin:0;\">Hẹn gặp lại,<br/>Đội ngũ hỗ trợ</p>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"background-color:#f9f9f9;padding:20px;text-align:center;font-size:12px;color:#aaaaaa;border-bottom-left-radius:8px;border-bottom-right-radius:8px\">\n" +
                "                <p style=\"margin:0;\">© 2024 Công ty của bạn. Mọi quyền được bảo lưu.</p>\n" +
                "                <p style=\"margin:0;\">Nếu bạn gặp vấn đề, vui lòng liên hệ <a href=\"mailto:support@yourcompany.com\" style=\"color:#007BFF;text-decoration:none;\">hỗ trợ</a>.</p>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</div>";
    }

}
