package com.payment.paymentapi.testConfig;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paymentapi.AppConfig;
import com.payment.paymentapi.controller.PaymentController;
import com.payment.paymentapi.usecase.PaymentMethodUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {PaymentController.class})
@ContextConfiguration(classes = {AppConfig.class})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected PaymentMethodUseCase paymentMethodUseCase;
}


