package org.mpay.utilityservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("currentPage", "dashboard");
        return "dashboard/index";
    }

    @GetMapping("/dashboard/settings")
    public String settings(Model model) {
        model.addAttribute("currentPage", "settings");
        return "dashboard/settings";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
