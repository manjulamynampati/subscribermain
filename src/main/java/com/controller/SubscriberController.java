package com.controller;

import com.model.SubscriberModel;
import com.model.EventData;
import com.model.SubscribeRequest;
import com.service.SubscriberService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@RestController
public class SubscriberController {


    @Autowired
    private SubscriberService subscriberService;

    @Value("${brokerIp}")
    private String brokerIp;

    @Value("${ec2Port}")
    private String ec2Port;

    @Value("${filepath}")
    private String filePath;

    // need to verify this part
    private String brokerUrl = "http://" + brokerIp + ":" + ec2Port;

    private static int subscriberId = 0;
    RestTemplate restTemplate = new RestTemplate();




    @GetMapping(value = "/getPublishers")
    public ResponseEntity<List<String>> getPublishers() {

        List<String> publisherList = new ArrayList<>();

        try {


            String appendedUrl = brokerUrl + "/getPublishers";
            System.out.println("Appended URL in : " + appendedUrl);

            ResponseEntity<List<String>> responseEntity = restTemplate.exchange(
                    brokerUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<String>>() {});

            if (responseEntity.getStatusCode().is2xxSuccessful()) {

                publisherList = responseEntity.getBody();
                for (String pub : publisherList) {
                    System.out.println("publisher in publisherList: " + pub);
                }

            }else {
                throw new IOException("Failed to fetch publishers. status Code : " + responseEntity.getStatusCode());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(publisherList);
    }


    @PostMapping(value = "/subscribe")
    public String subscribe(@RequestBody SubscribeRequest subscribeRequest) {

        List<String> selectedPublishers = subscribeRequest.getSelectedPublishers();

        SubscriberModel subscriber = new SubscriberModel();
        int id = incrementId();
        subscriber.setSubscriberId(id);
        subscriber.setPublishers(selectedPublishers);
        subscriber.setUrl(subscribeRequest.getSubscriberUrl());

        HttpStatus httpStatus = subscriberService.subscribe(subscriber,brokerUrl);
        System.out.println(httpStatus.value());
        return String.valueOf(Integer.parseInt(String.valueOf(httpStatus.value())));
    }


    @PostMapping(value = "/notify")
    public ResponseEntity<HttpStatus> notify(@RequestBody EventData event) {
        HttpStatus status = subscriberService.notify(event);
        //Ack
        return ResponseEntity.ok().body(status);
    }

    @GetMapping(value = "/events")
//    public List<EventData> getAllEvents() {
    public List<EventData> getAllEvents(@RequestParam(value = "publishers", required = false) List<String> selectedPublishers) {

        List<EventData> eventDataList = new ArrayList<>();
        Path path = Paths.get(filePath);

        try {


            if (!Files.exists(path)) {
                System.out.println("No events posted yet");
                return new ArrayList<>();
            }

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return new ArrayList<>();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {

                // 1,HandBag,thanksgiving,Sunnyvale,salesmessage

                String[] eventDetails = line.split(",");
                EventData event = new EventData();

                event.setEventId(Integer.parseInt(eventDetails[0]));
                event.setPublisherName(eventDetails[1]);
                event.setOccasion(eventDetails[2]);
                event.setEventLocation(eventDetails[3]);
                event.setMessage(eventDetails[4]);

                // Check if the publisherName is in the selectedPublishers list
                if (selectedPublishers == null || selectedPublishers.contains(event.getPublisherName())) {
                    eventDataList.add(event);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return eventDataList;
    }

    public static int incrementId() {
        subscriberId++;
        return subscriberId;
    }



}
