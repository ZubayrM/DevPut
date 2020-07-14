package object.component;

import org.springframework.stereotype.Component;

@Component
public class Image {

    private static final String host = "localhost";

    private static final String port = "8080";

    public String getImage(){
        return "http://" + host + ":" + port + "/api/";
    }

}
