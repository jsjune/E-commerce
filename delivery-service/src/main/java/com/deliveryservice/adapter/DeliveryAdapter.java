package com.deliveryservice.adapter;

public interface DeliveryAdapter {

    String processDelivery(String productName, int quantity, String street, String detailAddress, String zipCode, String alias);
}
