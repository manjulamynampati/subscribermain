package com.core.service;

import com.core.model.EventData;
import com.core.model.SubscriberModel;
import org.springframework.stereotype.Repository;
import org.springframework.http.HttpStatus;

@Repository
public interface SubscriberService {

    public HttpStatus subscribe(SubscriberModel subsriberModel, String brokerUrl) ;
    public HttpStatus unsubscribe(SubscriberModel subsriberModel, String brokerUrl) ;
    public HttpStatus notify (EventData event);
}
