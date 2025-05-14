package com.thiCK.Nhom16.config;

import com.thiCK.Nhom16.entity.User;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalModelAttribute {

    @ModelAttribute("user")
    public User userModel() {
        return new User();
    }
}
