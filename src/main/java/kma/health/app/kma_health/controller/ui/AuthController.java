package kma.health.app.kma_health.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Controller for login page
@Controller
@RequestMapping("/ui/public")
public class AuthController {

    @GetMapping("/login")
    public String login()
    {
        return "login";
    }

}
