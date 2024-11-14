package pro.shi.smartcontact.smart_contacts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import pro.shi.smartcontact.smart_contacts.dao.UserRepository;
import pro.shi.smartcontact.smart_contacts.entities.User;
import pro.shi.smartcontact.smart_contacts.helper.Message;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/do_register")
    public String registerUser(@ModelAttribute("user") User user, @RequestParam(value="agreement", defaultValue = "false") boolean agreement, Model model, HttpSession httpSession) {
        try {
            if (!agreement) {
                throw new Exception("You have not agreed to terms and conditions");
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            this.userRepository.save(user);
            
            httpSession.setAttribute("message", new Message("Successfully Registered! Please log in.", "alert-success"));
            return "redirect:/signin";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            httpSession.setAttribute("message", new Message("Something went wrong! " + e.getMessage(), "alert-danger"));
            return "signup";
        }
    }

    @GetMapping("/signin")
    public String customLogin(Model model) {
        model.addAttribute("title", "Login - Smart Contact Manager");
        return "login";
    }
}
