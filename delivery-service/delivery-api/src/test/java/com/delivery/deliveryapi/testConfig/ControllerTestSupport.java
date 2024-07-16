package com.delivery.deliveryapi.testConfig;


import com.delivery.deliveryapi.AppConfig;
import com.delivery.deliveryapi.Controller.DeliveryController;
import com.delivery.deliveryapi.Controller.internal.DeliveryInternalController;
import com.delivery.deliveryapi.usecase.DeliveryAddressUseCase;
import com.delivery.deliveryapi.usecase.InternalDeliveryUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {DeliveryController.class, DeliveryInternalController.class})
@ContextConfiguration(classes = {AppConfig.class})
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected DeliveryAddressUseCase deliveryAddressUseCase;
    @MockBean
    protected InternalDeliveryUseCase internalDeliveryUseCase;
}
