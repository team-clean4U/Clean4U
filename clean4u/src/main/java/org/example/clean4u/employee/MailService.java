package org.example.clean4u.employee;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.utils.MailUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final HttpSession session;
    private final JavaMailSender javaMailSender;

    public void sendEmail (String email) {
        String code = MailUtil.generateRandomCode();

        MimeMessage message =javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[Clean4U] 회원가입 이메일 전송");
            helper.setText("<h3>인증번호는 [" + code + "] 입니다<h3>", true);

            javaMailSender.send(message);

            session.setAttribute("code_" + email, code);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyEmailCode(String email, String code) {
        String savedCode = (String) session.getAttribute("code_" + email);

        if (savedCode != null && savedCode.equals(code)) {
            session.removeAttribute("code_" + email);
            return true;
        }

        return false;
    }
}
