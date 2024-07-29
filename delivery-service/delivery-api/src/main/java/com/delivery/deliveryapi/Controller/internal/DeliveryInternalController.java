package com.delivery.deliveryapi.Controller.internal;

import com.delivery.deliverycore.application.service.InternalDeliveryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryInternalController {

    private final InternalDeliveryUseCase internalDeliveryUseCase;

    @GetMapping("/internal/delivery/{deliveryId}/status")
    public Boolean deliveryStatusCheck(@PathVariable Long deliveryId) {
        return internalDeliveryUseCase.deliveryStatusCheck(deliveryId);
    }

}
