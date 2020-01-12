package com.hebaibai.transmit.webconfig;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConfigController {

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }
}
