package com.order.orderapi.testConfig;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.ordercore.application.service.OrderReadUseCase;
import com.order.ordercore.application.service.OrderWriteUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
