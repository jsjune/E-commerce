package com.deliveryservice.adapter;

public interface DeliveryAdapter {

    String processDelivery(String productName, Long quantity, String street, String detailAddress, String zipCode, String alias);

    String cancelDelivery(String productName, Long quantity, String street, String detailAddress, String zipCode, String alias);
}
