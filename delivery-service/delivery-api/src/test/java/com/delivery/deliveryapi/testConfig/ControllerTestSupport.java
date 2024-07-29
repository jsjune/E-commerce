package com.delivery.deliveryapi.testConfig;


import com.delivery.deliverycore.application.service.impl.DeliveryAddressService;
import com.delivery.deliverycore.application.service.impl.InternalDeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected DeliveryAddressService deliveryAddressUseCase;
    @MockBean
    protected InternalDeliveryService internalDeliveryUseCase;
}
