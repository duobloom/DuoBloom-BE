package POT.DuoBloom.domain.user.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private static final long CODE_EXPIRE_MINUTES = 5; // 인증 코드 유효 시간 (5분)

    // 인증 번호 생성
    public static int createNumber() {
        return (int)(Math.random() * 900000) + 100000; // 6자리 랜덤 숫자
    }

    // 이메일 작성
    public MimeMessage createMail(String recipientEmail, int authCode) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
            message.setSubject("[DuoBloom] 회원가입을 위한 이메일 인증");

            // 이메일 본문
            // TODO: 디자인 맞추어 색 변경
            String body = "<h2 style='color:#212721; font-family: Pretendard, sans-serif;'>소중한 길을 따뜻하게 동행하는<br>DuoBloom</h2>"
                    + "<h3 style='color:#212721; font-family: Pretendard, sans-serif;'>회원가입을 위해 요청하신 인증 번호입니다.</h3>"
                    + "<div align='center' style='background-color:#FFF5F5; border-radius:15px; padding:20px; font-family: Pretendard, sans-serif;'>"
                    + "<h2 style='color:#FF5A5A; font-family: Pretendard, sans-serif;'>회원가입 인증 코드입니다.</h2>"
                    + "<h1 style='color:#FF5A5A; font-family: Pretendard, sans-serif;'>" + authCode + "</h1>"
                    + "</div>";

            message.setText(body, "UTF-8", "html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    // 이메일 전송 및 Redis 저장
    public void sendEmail(String recipientEmail) {
        int authCode = createNumber();
        MimeMessage message = createMail(recipientEmail, authCode);

        // Redis에 인증 코드 저장, 5분 후 만료
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(recipientEmail, String.valueOf(authCode), CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 이메일 전송
        javaMailSender.send(message);
    }

    // 인증 코드 확인
    public boolean verifyAuthCode(String email, String inputCode) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String storedCode = valueOperations.get(email);

        return storedCode != null && storedCode.equals(inputCode);
    }
}
