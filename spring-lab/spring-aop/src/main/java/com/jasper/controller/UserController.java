package com.jasper.controller;

import com.jasper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;


@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    public String getUser() {
        return "jasper";
    }


}
