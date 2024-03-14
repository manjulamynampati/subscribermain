package com.core.controller;

import com.core.model.SubscriberModel;
import com.core.model.EventData;
import com.core.model.SubscribeRequest;
import com.core.model.UnsubscribeRequest;
import com.core.service.SubscriberService;
import org.springframework.http.ResponseEntity;
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
import java.util.*;


@RestController
@CrossOrigin(origins = "*")
public class SubscriberController {


    @Autowired
    private SubscriberService subscriberService;

    @Value("${brokerIp}")
    private String brokerIp;

    @Value("${ec2Port}")
    private String ec2Port;

    @Value("${filepath}")
    private String filePath;




    RestTemplate restTemplate = new RestTemplate();
    private static Map<String, Integer> eventPublisherMap = new HashMap<>();



    @GetMapping(value = "/getPublishers")
    public ResponseEntity<List<String>> getPublishers() {

        long startTime = System.currentTimeMillis();
        String brokerUrl = "http://" + brokerIp + ":" + ec2Port;
        StringBuilder response = new StringBuilder();

        List<String> publisherList = new ArrayList<>();

        try {


            String appendedUrl = brokerUrl + "/getPublishers";
            System.out.println("Sending request to lead broker for active publishers:::::  : " + appendedUrl);
            ParameterizedTypeReference<List<String>> responseType = new ParameterizedTypeReference<List<String>>() {};


            ResponseEntity<List<String>> responseEntity  = restTemplate.exchange(
                    appendedUrl, HttpMethod.GET, null, responseType);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                publisherList = responseEntity.getBody();
                for (String pub : publisherList) {
                    System.out.println("publisher in publisherList: " + pub);
                }
            }else{
                System.out.println("Request failed with status code: " + responseEntity.getStatusCodeValue());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime; // ms
        System.out.println("Total Time taken to get publishers from the broker in ms" + timeTaken);


        return ResponseEntity.ok().body(publisherList);
    }


    @PostMapping(value = "/subscribe")
    public String subscribe(@RequestBody SubscribeRequest subscribeRequest) {
        long startTime = System.currentTimeMillis();
        String brokerUrl = "http://" + brokerIp + ":" + ec2Port;

        List<String> selectedPublishers = subscribeRequest.getSelectedPublishers();

        SubscriberModel subscriber = new SubscriberModel();

        subscriber.setSubscriberId(subscribeRequest.getSubscriberId());
        subscriber.setPublishers(selectedPublishers);
        subscriber.setUrl(subscribeRequest.getSubscriberUrl());
        subscriber.setPort(subscribeRequest.getPort());

        HttpStatus httpStatus = subscriberService.subscribe(subscriber,brokerUrl);
        System.out.println(httpStatus.value());
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime; // ms
        System.out.println("Total Time taken by subscriber to get subscribed to the publisher in the broker in ms" + timeTaken);

        return String.valueOf(Integer.parseInt(String.valueOf(httpStatus.value())));
    }


    @PostMapping(value = "/unsubscribe")
    public String unsubscribe(@RequestBody UnsubscribeRequest request) {
        long startTime = System.currentTimeMillis();
        String brokerUrl = "http://" + brokerIp + ":" + ec2Port;

        List<String> selectedPublishers = request.getSelectedPublishers();

        SubscriberModel subscriber = new SubscriberModel();

        subscriber.setSubscriberId(request.getSubscriberId());
        subscriber.setPublishers(selectedPublishers);
        subscriber.setUrl(request.getUnsubscribeUrl());
        subscriber.setPort(request.getPort());


        HttpStatus httpStatus = subscriberService.unsubscribe(subscriber,brokerUrl);
        System.out.println(httpStatus.value());
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime; // ms
        System.out.println("Total Time taken by subscriber to get unsubscribed to the publisher in the broker in ms" + timeTaken);
        return String.valueOf(Integer.parseInt(String.valueOf(httpStatus.value())));
    }


    @PostMapping(value = "/notify")
    public ResponseEntity<HttpStatus> notify(@RequestBody EventData event) {

        HttpStatus status = subscriberService.notify(event);
        //Ack
        return ResponseEntity.ok().body(status);
    }

    @GetMapping(value = "/events")
    public List<EventData> getAllEvents() {

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



                String[] eventDetails = line.split(",");

                int eventId = Integer.parseInt(eventDetails[0]);
                String publisherId = eventDetails[1];

                if (!eventPublisherMap.getOrDefault(publisherId,0).equals(eventId)) {
                    EventData event = new EventData();
                    event.setEventId(eventId);
                    event.setPublisherId(eventDetails[1]);
                    event.setOccasion(eventDetails[2]);
                    event.setEventLocation(eventDetails[3]);
                    event.setMessage(eventDetails[4]);
                    eventDataList.add(event);

                    eventPublisherMap.put(publisherId,eventId);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return eventDataList;
    }





}
