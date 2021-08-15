package com.hansoleee.corespringsecurity.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    @GetMapping("/messages")
    public String messages() {
        return "user/messages";
    }

    @GetMapping("/api/messages")
    @ResponseBody
    public String apiMessage() {
        return "message ok";
    }
}
