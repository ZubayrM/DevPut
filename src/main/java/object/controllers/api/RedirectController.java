package object.controllers.api;

import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RedirectController {

    //пока так
    @GetMapping("/{id}")
    @SneakyThrows
    public void getPostById(@PathVariable String id, HttpServletResponse response){
        response.sendRedirect("/api/post/" + id);
    }



}
