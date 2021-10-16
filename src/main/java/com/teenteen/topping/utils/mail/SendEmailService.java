package com.teenteen.topping.utils.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendEmailService {
    private final JavaMailSender javaMailSender;
    private static final String FROM_ADDRESS = "Topping";

    public MailDto createMail(String userEmail){
        String str = getTempNumber();

        MailDto dto = new MailDto(userEmail,
                "Topping의 이메일 인증번호 입니다.",
                "안녕하세요. Topping의 이메일 인증 번호입니다. 인증번호는 "+str+" 입니다.",
                str);
        return dto;
    }

    public String getTempNumber(){
        char[] numSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        String str = "";

        int idx = 0;
        for (int i = 0; i < 6; i++) {
            idx = (int) (numSet.length * Math.random());
            str += numSet[idx];
        }
        return str;
    }

    public void mailSend(MailDto mailDto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getAddress());
        message.setFrom(FROM_ADDRESS);
        message.setSubject(mailDto.getTitle());
        message.setText(mailDto.getMessage());

        javaMailSender.send(message);
    }
}
