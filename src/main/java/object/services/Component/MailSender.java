package object.services.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void send(String mailTo, String subject, String msg){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(mailTo);
        message.setSubject(subject);
        message.setText(msg);
        mailSender.send(message);
    }
}
