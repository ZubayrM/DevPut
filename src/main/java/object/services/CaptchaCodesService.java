package object.services;

import lombok.SneakyThrows;
import object.dto.response.CaptchaDto;
import object.model.CaptchaCodes;
import object.repositories.CaptchaCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class CaptchaCodesService {

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    @SneakyThrows
    public CaptchaDto captcha() {

        String code = UUID.randomUUID().toString().substring(0, 4);

        BufferedImage image = new BufferedImage(120, 50, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        graphics2D.setFont(new Font("ololo", Font.BOLD, 40));
        graphics2D.drawString(code,10 , 40);

        //для просмотра
        ImageIO.write(image, "png", new File("photo.png"));

        byte[] bytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        String img = Base64.getEncoder().encodeToString(bytes);


        CaptchaCodes captchaCodes = new CaptchaCodes();
        captchaCodes.setTime(new Date());
        captchaCodes.setCode(img);
        captchaCodes.setSecretCode(code);

        CaptchaCodes request = captchaCodesRepository.save(captchaCodes);


        return CaptchaDto.builder()
                .image("data:image/png;base64, "+request.getCode())
                .secret(request.getSecretCode())
                .build();
    }



}
