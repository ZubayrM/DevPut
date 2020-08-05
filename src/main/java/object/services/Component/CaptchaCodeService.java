package object.services.Component;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import object.dto.response.CaptchaDto;
import object.model.CaptchaCodes;
import object.repositories.CaptchaCodesRepository;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@Log4j2
public class CaptchaCodeService {

    @Value("${captcha.lifetime}")
    private long captchaLifetime;

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    @SneakyThrows
    public CaptchaDto getCaptchaDto() {
        LocalDateTime time = LocalDateTime.now().minusHours(captchaLifetime);
        captchaCodesRepository.deleteByTimeBefore(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()));

        String code = UUID.randomUUID().toString().substring(0, 4);
        String secretCode = UUID.randomUUID().toString().substring(0, 4);

        BufferedImage image = createBufferedImage(secretCode);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String imageBase64 = Base64.getEncoder().encodeToString(bytes);



        CaptchaCodes captcha = createCaptcha(code, secretCode);
        captchaCodesRepository.save(captcha);


        return CaptchaDto.builder()
                .image("data:image/png;base64," + imageBase64)
                .secret(code)
                .build();
    }



    private CaptchaCodes createCaptcha(String code, String secretCode) {
        CaptchaCodes captchaCodes = new CaptchaCodes();
        captchaCodes.setTime(new Date());
        captchaCodes.setCode(code);
        captchaCodes.setSecretCode(secretCode);
        return captchaCodesRepository.save(captchaCodes);
    }

    private BufferedImage createBufferedImage(String code) {
        BufferedImage image = new BufferedImage(100, 40, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        graphics2D.setColor(Color.white);
        graphics2D.fillRect(0,0,100,50);

        graphics2D.setPaint(new GradientPaint(0f, 0f, Color.DARK_GRAY,100f, 40f, Color.cyan ));
        graphics2D.setFont(new Font("ololo", Font.HANGING_BASELINE, 30));
        graphics2D.drawString(code,10 , 30);
        return image;
    }


}
