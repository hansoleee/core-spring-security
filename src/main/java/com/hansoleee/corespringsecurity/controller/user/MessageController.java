package com.hansoleee.corespringsecurity.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    @GetMapping("/messages")
    public String messages() {
        return "user/messages";
    }

    @PostMapping("/api/messages")
    public String apiMessage(HttpServletRequest request) {
        log.info(request.getRequestURL().toString() + " " + request.getMethod());
        return "user/messages";
    }
}
