package com.order.orderapi.testConfig;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.orderapi.AppConfig;
import com.order.orderapi.controller.OrderController;
import com.order.orderapi.usecase.OrderReadUseCase;
import com.order.orderapi.usecase.OrderWriteUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {OrderController.class})
@ContextConfiguration(classes = {AppConfig.class})
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected OrderWriteUseCase orderWriteUseCase;
    @MockBean
    protected OrderReadUseCase orderReadUseCase;
}
