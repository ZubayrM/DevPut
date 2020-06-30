package object.services;

import lombok.SneakyThrows;
import object.dto.response.CaptchaDto;
import object.model.CaptchaCodes;
import object.repositories.CaptchaCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.PaintEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.net.URL;
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

        BufferedImage image = getBufferedImage(code);


        File file = new File("D:\\Диплом\\DevPut\\src\\main\\resources/static/img/photo.png");
        ImageIO.write(image, "png", file);

        int index = file.getAbsolutePath().indexOf("img");
        String url = file.getAbsolutePath().substring(index-1);



//        byte[] bytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

//        String imgBase64 = Base64.getEncoder().encodeToString(bytes);


//        CaptchaCodes captchaCodes = new CaptchaCodes();
//        captchaCodes.setTime(new Date());
//        captchaCodes.setCode(imgBase64);
//        captchaCodes.setSecretCode(code);

        //CaptchaCodes captcha = captchaCodesRepository.save(captchaCodes);

        return CaptchaDto.builder()
                .image(url)
                .secret(code)
                .build();
    }

    private BufferedImage getBufferedImage(String code) {
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
