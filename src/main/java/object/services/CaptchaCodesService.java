package object.services;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import object.dto.response.CaptchaDto;
import object.model.CaptchaCodes;
import object.repositories.CaptchaCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
@Log4j2
public class CaptchaCodesService {

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    @SneakyThrows
    public CaptchaDto captcha() {

        String code = UUID.randomUUID().toString().substring(0, 4);
        String secretCode = UUID.randomUUID().toString().substring(0, 4);

        BufferedImage image = createBufferedImage(secretCode);

        String path = saveImage(image);
        log.info(path);

        Thread.sleep(1000);
        CaptchaCodes captcha = createCaptcha(code, secretCode);
        captchaCodesRepository.save(captcha);


        return CaptchaDto.builder()
                .image(path)
                .secret(code)
                .build();
    }

    private String getPathToImage(File file) {
        int index = file.getAbsolutePath().indexOf("img");
        return file.getAbsolutePath().substring(index-1);
    }


    private String saveImage(BufferedImage img) throws IOException {
       // String path = UUID.randomUUID().toString();
        File dir = new File("D:\\Диплом\\DevPut\\src\\main\\resources/static/img/captcha/");
        dir.mkdir();
        File file = new File(dir.getAbsolutePath() + "/captcha.png");
        ImageIO.write(img, "png", file);
        return getPathToImage(file);
    }


//    @Deprecated
//    private File saveImage(CaptchaCodes captcha) throws IOException {
//        File file = new File("D:\\Диплом\\DevPut\\src\\main\\resources/static/img/captcha.png");
//        byte[] img = Base64.getDecoder().decode(captcha.getCode());
//        BufferedImage bi =  ImageIO.read(new ByteArrayInputStream(img));
//        ImageIO.write(bi, "png", file);
//        return file;
//    }

//    @Deprecated
//    @SneakyThrows
//    private CaptchaCodes createCaptcha(String code, BufferedImage image) {
//
//        String imgBase64 = getImgBase64(image);
//
//        CaptchaCodes captchaCodes = new CaptchaCodes();
//        captchaCodes.setTime(new Date());
//        captchaCodes.setCode(imgBase64);
//        captchaCodes.setSecretCode(code);
//        return captchaCodesRepository.save(captchaCodes);
//    }

    private CaptchaCodes createCaptcha(String code, String secretCode) {
        CaptchaCodes captchaCodes = new CaptchaCodes();
        captchaCodes.setTime(new Date());
        captchaCodes.setCode(code);
        captchaCodes.setSecretCode(secretCode);
        return captchaCodesRepository.save(captchaCodes);
    }

//    private String getImgBase64(BufferedImage image) throws IOException {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ImageIO.write(image, "png", byteArrayOutputStream);
//        byteArrayOutputStream.flush();
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        byteArrayOutputStream.close();
//        return Base64.getEncoder().encodeToString(bytes);
//    }

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
