package object.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Host2Port {

    @Value("${host}")
    private String host;

    @Value("${server.port}")
    private String port;

    public String getImagePath(){
        return host + ":" + port;
    }

    public String getHost() {
        return host;
    }

}
