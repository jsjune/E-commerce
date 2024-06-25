package com.deliveryservice.controller.internal;

import com.deliveryservice.controller.internal.req.ProcessDeliveryRequest;
import com.deliveryservice.usecase.DeliveryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryInternalController {

    private final DeliveryUseCase deliveryUseCase;

    @GetMapping("/internal/delivery/{deliveryId}/status")
    public Boolean deliveryStatusCheck(@PathVariable Long deliveryId) {
        return deliveryUseCase.deliveryStatusCheck(deliveryId);
    }

    @PostMapping("/internal/delivery/process")
    public Long processDelivery(@RequestBody ProcessDeliveryRequest request) throws Exception{
        return deliveryUseCase.processDelivery(request.mapToCommand());
    }
}
