package kma.health.app.kma_health.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/public")
public class HomeController
{
    @GetMapping("/")
    public String home()
    {
        return "home";
    }
}
