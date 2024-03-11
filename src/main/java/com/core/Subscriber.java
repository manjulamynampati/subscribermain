package com.core;

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

    @GetMapping("/home")
    public String home(Model model) {
        String privateIpAddress = EC2MetadataUtils.getInstanceInfo().getPrivateIp();
        //String privateIpAddress = "192.168.4.26";
        model.addAttribute("privateIpAddress", privateIpAddress);
        return "home";
    }

}


