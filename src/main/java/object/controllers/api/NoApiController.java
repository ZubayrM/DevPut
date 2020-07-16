package object.controllers.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NoApiController {

    @GetMapping("/{id}")
    public String getPostById(@PathVariable String id, HttpServletRequest request){
        return "redirect:" + request.getScheme() + "/api/post/" + id;
    }



}
