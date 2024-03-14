package com.core.controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventsController {

    @GetMapping("/eventsPage")
    public String eventsPage(@RequestParam(name = "publishers") String publishers,
                             @RequestParam(name = "subscriberId") String subscriberId,
                             @RequestParam(name = "subIp") String subIp,
                             @RequestParam(name = "subPrivateIp") String subPrivateIp,
                             @RequestParam(name = "port") String port) {

        return "eventsPage";
    }

    @GetMapping("/unsubscribePage")
    public String unsubscribePage(@RequestParam(name = "publishers") String publishers,
                                  @RequestParam(name = "subscriberId") String subscriberId,
                                  @RequestParam(name = "subIp") String subIp,
                                  @RequestParam(name = "subPrivateIp") String subPrivateIp,
                                  @RequestParam(name = "port") String port) {

        return "unsubscribePage";
    }


}
