package com.pumping.domain.exercisehistory.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.pumping.domain.member.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PushNotificationService {

    public void sendWeeklyReport(Member member) {
        String deviceToken = member.getFcmToken();

        if (deviceToken == null || deviceToken.isBlank()) {
            log.warn("푸시 전송 스킵 - deviceToken 없음 (memberId: {})", member.getId());
            return;
        }

        Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                        .setTitle("주간 리포트 도착!")
                        .setBody(member.getNickname() + "님의 리포트가 준비되었습니다.")
                        .build())
                .putData("type", "weekly_report")
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error("푸시 전송 실패 - memberId: {}, reason: {}", member.getId(), e.getMessage());
        }
    }
}