package com.customer.admin.cpepsi_customers.Java_files;

import java.io.Serializable;

public class NotificationModel implements Serializable {

    private String prId;
    private String customerId;
    private String providerId;
    private String discription;
    private String prostatus;

    public NotificationModel(String prId, String customerId, String providerId, String discription, String prostatus) {
        this.prId = prId;
        this.customerId = customerId;
        this.providerId = providerId;
        this.discription = discription;
        this.prostatus = prostatus;
    }

    public String getPrId() {
        return prId;
    }

    public void setPrId(String prId) {
        this.prId = prId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getProstatus() {
        return prostatus;
    }

    public void setProstatus(String prostatus) {
        this.prostatus = prostatus;
    }
}
