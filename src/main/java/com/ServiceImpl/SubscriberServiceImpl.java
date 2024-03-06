package com.ServiceImpl;
import com.model.EventData;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.service.SubscriberService;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.model.SubscriberModel;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class SubscriberServiceImpl implements SubscriberService {

    @Autowired
    RestTemplate restTemplate;


    @Value("${filepath}")
    private String filePath;

    @Override
    public HttpStatus subscribe(SubscriberModel subscriber, String brokerUrl) {

        System.out.println("=======Subscriber details to be sent to broker====");
        System.out.println("Subscriber id :::::: "+ subscriber.getSubscriberId());
        System.out.println("selected publishers ::::::: "+ subscriber.getPublishers().toString());
        System.out.println("subscriber url ::::::: "+ subscriber.getUrl());
        System.out.println("subscriber port ::::::: "+ subscriber.getPort());
        System.out.println("=======Sending request to broker to subscribe for ===="+ subscriber.getSubscriberId());

        String url = brokerUrl + "/subscribe";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SubscriberModel> req = new HttpEntity<>(subscriber, headers);

        try {

            ResponseEntity<HttpStatus> responseEntity = restTemplate.exchange(url,
                    HttpMethod.POST, req, HttpStatus.class);


            HttpStatus statusCode = responseEntity.getStatusCode();
            System.out.println("Received response from broker with status code: " + statusCode);
            return statusCode;

        } catch (Exception e) {

            System.out.println("An error occurred while communicating with the broker: " + e.getMessage());
            throw new RuntimeException("An error occurred while communicating with the broker", e);
        }


    }


    @Override
    public HttpStatus unsubscribe(SubscriberModel subscriber, String brokerUrl) {

        System.out.println("=======Unsubscriber details to be sent to broker====");
        System.out.println("Subscriber id :::::: "+ subscriber.getSubscriberId());
        System.out.println("selected publishers ::::::: "+ subscriber.getPublishers().toString());
        System.out.println("subscriber url ::::::: "+ subscriber.getUrl());
        System.out.println("subscriber port :::::: "+ subscriber.getPort());
        System.out.println("=======Sending request to broker to subscribe for ===="+ subscriber.getSubscriberId());

        String unsubscribeUrl = brokerUrl + "/unsubscribe";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SubscriberModel> request = new HttpEntity<>(subscriber, headers);

        try {

            ResponseEntity<HttpStatus> responseEntity = restTemplate.exchange(unsubscribeUrl,
                    HttpMethod.POST, request, HttpStatus.class);


            HttpStatus statusCode = responseEntity.getStatusCode();
            System.out.println("Received response for unsubscribe from broker with status code: " + statusCode);
            return statusCode;

        } catch (Exception e) {

            System.out.println("An error occurred while communicating with the broker: " + e.getMessage());
            throw new RuntimeException("An error occurred while communicating with the broker", e);
        }


    }

    @Override
    public HttpStatus notify(EventData event) {

        System.out.println("=============================================");
        System.out.println("=======Received Events from broker===========");
        System.out.println("=============================================");
        System.out.println("Event Id : " + event.getEventId());
        System.out.println("PublisherName : " + event.getPublisherName());
        System.out.println("Occasion : " + event.getOccasion());
        System.out.println("Event Location : " + event.getEventLocation());
        System.out.println("Message : " + event.getMessage());
        System.out.println("=============================================");


        try {

            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            Files.write(path,
                    (event.getEventId() + "," +
                            event.getPublisherName() + "," +
                            event.getOccasion() + "," +
                            event.getEventLocation() + "," +
                            event.getMessage() +
                            System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            return HttpStatus.OK;

        } catch (IOException e) {
            throw new RuntimeException(e);

        }



    }

}
