package kma.health.app.kma_health.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//Controller for register page
@Controller
@RequestMapping("/ui/public")
public class RegisterController {

    @GetMapping("/register")
    public String login()
    {
        return "register";
    }

}
