package com.model;

import java.util.List;

public class UnsubscribeRequest {

    private List<String> selectedPublishers;
    private String unsubscribeUrl;
    private int subscriberId;

    public List<String> getSelectedPublishers() {
        return selectedPublishers;
    }

    public void setSelectedPublishers(List<String> selectedPublishers) {
        this.selectedPublishers = selectedPublishers;
    }

    public String getUnsubscribeUrl() {
        return unsubscribeUrl;
    }

    public void setUnsubscribeUrl(String unsubscribeUrl) {
        this.unsubscribeUrl = unsubscribeUrl;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }
}
