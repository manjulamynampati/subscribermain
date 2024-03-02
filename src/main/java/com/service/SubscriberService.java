package com.service;

import com.model.EventData;
import org.springframework.stereotype.Repository;
import org.springframework.http.HttpStatus;
import com.model.SubscriberModel;

@Repository
public interface SubscriberService {

    public HttpStatus subscribe(SubscriberModel subsriberModel, String brokerUrl) ;
    public HttpStatus unsubscribe(SubscriberModel subsriberModel, String brokerUrl) ;
    public HttpStatus notify (EventData event);
}
