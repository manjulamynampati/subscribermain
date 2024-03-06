package com.controller;

import com.model.SubscriberModel;
import com.model.EventData;
import com.model.SubscribeRequest;
import com.model.UnsubscribeRequest;
import com.service.SubscriberService;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


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


    RestTemplate restTemplate = new RestTemplate();




    @GetMapping(value = "/getPublishers")
    public ResponseEntity<List<String>> getPublishers() {

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
//            URL url = new URL(appendedUrl);
//
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            int responseCode = connection.getResponseCode();
//            System.out.println("Received Response code from Lead Broker ::::: " + responseCode);
//
//
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//                reader.close();
//                System.out.println("Response body: " + response.toString());
//                connection.disconnect();
//                String[] topics = response.toString().split(",");
//
//                for (int i = 0; i < topics.length; i++) {
//                    topics[i] = topics[i].replaceAll("[^a-zA-Z0-9]", "");
//                }
//
//                publisherList = new ArrayList<>(Arrays.asList(topics));
//                for (String pub : publisherList) {
//                    System.out.println("publisher in publisherList: " + pub);
//                }

//            }else {
//                throw new IOException("Failed to fetch topics. Response Code: " + responseCode);
//            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(publisherList);
    }


    @PostMapping(value = "/subscribe")
    public String subscribe(@RequestBody SubscribeRequest subscribeRequest) {

        String brokerUrl = "http://" + brokerIp + ":" + ec2Port;

        List<String> selectedPublishers = subscribeRequest.getSelectedPublishers();

        SubscriberModel subscriber = new SubscriberModel();

        subscriber.setSubscriberId(subscribeRequest.getSubscriberId());
        subscriber.setPublishers(selectedPublishers);
        subscriber.setUrl(subscribeRequest.getSubscriberUrl());
        subscriber.setPort(subscribeRequest.getPort());

        HttpStatus httpStatus = subscriberService.subscribe(subscriber,brokerUrl);
        System.out.println(httpStatus.value());
        return String.valueOf(Integer.parseInt(String.valueOf(httpStatus.value())));
    }


    @PostMapping(value = "/unsubscribe")
    public String unsubscribe(@RequestBody UnsubscribeRequest request) {

        String brokerUrl = "http://" + brokerIp + ":" + ec2Port;

        List<String> selectedPublishers = request.getSelectedPublishers();

        SubscriberModel subscriber = new SubscriberModel();

        subscriber.setSubscriberId(request.getSubscriberId());
        subscriber.setPublishers(selectedPublishers);
        subscriber.setUrl(request.getUnsubscribeUrl());
        subscriber.setPort(request.getPort());


        HttpStatus httpStatus = subscriberService.unsubscribe(subscriber,brokerUrl);
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

                // 1,HandBag,thanksgiving,Sunnyvale,salesmessage

                String[] eventDetails = line.split(",");
                EventData event = new EventData();

                event.setEventId(Integer.parseInt(eventDetails[0]));
                event.setPublisherName(eventDetails[1]);
                event.setOccasion(eventDetails[2]);
                event.setEventLocation(eventDetails[3]);
                event.setMessage(eventDetails[4]);
                eventDataList.add(event);



            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return eventDataList;
    }





}
