package com.example.storeclothes.data.model;

public class PaymentMethod {
    private String methodId;
    private String name;

    public PaymentMethod(String methodId, String name) {
        this.methodId = methodId;
        this.name = name;
    }
    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
