package object.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImagePath {

    @Value("${host}")
    private String host;

    @Value("${server.port}")
    private String port;

    public String getImagePath(){
        return host + ":" + port;
    }

}
