package kma.health.app.kma_health.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/ui/public/";
    }
}
