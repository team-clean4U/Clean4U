package org.example.clean4u.sms;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.exception.SolapiEmptyResponseException;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.exception.SolapiUnknownException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import lombok.extern.slf4j.Slf4j;
import org.example.clean4u._core.errors.exception.Exception500;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class SmsService {

    private final DefaultMessageService messageService;
    private final String from;

    public SmsService(
            @Value("${solapi.api-key}") String apiKey,
            @Value("${solapi.api-secret}") String apiSecret,
            @Value("${solapi.from}") String from
    ) {
        this.messageService = SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);
        this.from = from;
    }

    // SMS 전송
    public void sendOne(String to, String sendMessage) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to.replaceAll("[^0-9]", ""));
        message.setText(sendMessage);

        try {
            var response = messageService.send(message);
            log.info("문자 응답 확인: {}", response);
        } catch (SolapiMessageNotReceivedException | SolapiEmptyResponseException | SolapiUnknownException e) {
            log.error("문자 전송 실패: {}", e.getMessage(), e);
            throw new Exception500("문자 전송에 실패했습니다.");
        }
    }
}
