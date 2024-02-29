package com.model;


import java.util.List;

public class SubscribeRequest {

    private List<String> selectedPublishers;
    private String subscriberUrl;


    public List<String> getSelectedPublishers() {
        return selectedPublishers;
    }

    public void setSelectedPublishers(List<String> selectedPublishers) {
        this.selectedPublishers = selectedPublishers;
    }

    public String getSubscriberUrl() {
        return subscriberUrl;
    }

    public void setSubscriberUrl(String subscriberUrl) {
        this.subscriberUrl = subscriberUrl;
    }
}
