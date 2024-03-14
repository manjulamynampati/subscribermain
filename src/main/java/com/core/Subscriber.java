package com.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import com.amazonaws.util.EC2MetadataUtils;
import org.springframework.ui.Model;


@SpringBootApplication
public class Subscriber {
    public static void main(String[] args) {
        SpringApplication.run(Subscriber.class, args);
        System.out.println("welcome to subscriber module.........");

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}


@Controller
class HomeController {

    @Value("${brokerIp}")
    private String brokerIp;

    @Value("${subscriberIp}")
    private String subscriberIp;

    @GetMapping("/home")
    public String home(Model model) {
        String privateIpAddress = EC2MetadataUtils.getInstanceInfo().getPrivateIp();
        model.addAttribute("privateIpAddress", privateIpAddress);
        model.addAttribute("brokerIp", brokerIp);
        model.addAttribute("subscriberIp", subscriberIp);
        return "home";
    }

}


