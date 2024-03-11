package com.core.model;


import java.util.List;

public class SubscribeRequest {

    private List<String> selectedPublishers;
    private String subscriberUrl;
    private int subscriberId;
    private int port;

    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
