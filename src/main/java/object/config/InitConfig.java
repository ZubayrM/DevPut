package object.config;

import object.dto.response.InitResponseDto;
import object.services.TagsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:init.yml")
public class InitConfig {

    @Bean
    public InitResponseDto  initResponseDto(){
        return new InitResponseDto();
    }

}
