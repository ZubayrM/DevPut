package object.services;

import object.dto.response.CaptchaDto;
import object.model.CaptchaCodes;
import object.repositories.CaptchaCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class CaptchaCodesService {

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    public CaptchaDto captcha() {
        CaptchaCodes captchaCodes = new CaptchaCodes();

        //***************************************************************
        captchaCodes.setTime(new Date());
        captchaCodes.setCode( UUID.randomUUID().toString().substring(0,4));
        captchaCodes.setSecretCode(UUID.randomUUID().toString().substring(0,4));

        CaptchaCodes request = captchaCodesRepository.save(captchaCodes);


        return CaptchaDto.builder()
                .image(request.getCode())
                .secret(request.getSecretCode()).build();
    }
}
