package eLibrary.controller;

import eLibrary.domain.Role;
import eLibrary.domain.User;
import eLibrary.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER_OVERSEER')")
    @GetMapping("/registration")
    public String registration(Model model){

        model.addAttribute("roles", Role.values());
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @Valid User user,
            BindingResult bindingResult,
            Model model,
            @RequestParam Map<String, String> form
    ){

        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            return "registration";
        }

        if(!userService.addUser(user)){
            model.addAttribute("UsernameError", "User exists");
            model.addAttribute("roles", Role.values());
            return "registration";
        }
        userService.saveRoles(user,form);

        model.addAttribute("user",null);
        model.addAttribute("success",true);
        model.addAttribute("roles", Role.values());

        return "registration";
    }

    @GetMapping("/recover")
    public String passwordRecover(Model model){
        return "passwordRecoverEmail";
    }

    @PostMapping("/recover")
    public String sendEmail(
            Model model,
            @RequestParam("email") String email
            ){
        if(!email.isEmpty()) {
            userService.sendMail(email);
        }

        return "passwordRecoverEmail";
    }

    @GetMapping("/recover/{code}")
    public String changePassword(
            Model model,
            @PathVariable String code
    ){
        User user = userService.findByActivationCode(code);
        if(user!=null) {
            model.addAttribute("username", user.getUsername());
        }
        else{
            model.addAttribute("message", "Activation code not found");
        }
        return "passwordChange";
    }

    @PostMapping("/recover/newPassword")
    public String savePassword(
            Model model,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("password2") String password2
            ){

        User user = userService.findByUsername(username);
        if(!password.equals(password2)){
            model.addAttribute("message", "Passwords are different");
            return "passwordChange";
        }

        userService.saveUser(user, password, null);
        return "redirect:/login";
    }
}
