
package de.ea234.securitydemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.security.Principal;

@Controller
public class ViewHomeController {

    @GetMapping("/")
    public String home(Model model, Principal principal) {

        model.addAttribute("username", principal.getName());

        return "home";
    }
}
