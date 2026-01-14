package org.example.clean4u.employee;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception500;
import org.example.clean4u._core.utils.MailUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final HttpSession session;
    private final JavaMailSender javaMailSender;

    public void sendEmail (String email) {
        String code = MailUtil.generateRandomCode();

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[Clean4U] 회원가입 이메일 전송");
            helper.setText("<h3>인증번호는 [" + code + "] 입니다<h3>", true);

            javaMailSender.send(message);

            session.setAttribute("code_" + email, code);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage(), e);
            throw new Exception500("이메일 발송에 실패했습니다.");
        }
    }

    public boolean verifyEmailCode(String email, String code) {
        String savedCode = (String) session.getAttribute("code_" + email);

        if (savedCode != null && savedCode.equals(code)) {
            session.removeAttribute("code_" + email);

            session.setAttribute("verified_email_" + email, true);
            return true;
        }

        return false;
    }

    public void sendEmailAndPassword (String email, String resetLink) {

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[Clean4U] 비밀번호 변경 이메일 발송");
            helper.setText(
                    "<h3>비밀번호 재설정 하려면 아래 링크를 클릭하세요.</h3>" +
                    "<a href=\"" + resetLink + "\">링크 클릭</a>" +
                    "<h3>링크는 15분 동안만 유효합니다.</h3>",true);

            javaMailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
