package com.product.productapi.testConfig;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.productcore.application.service.InternalProductUseCase;
import com.product.productcore.application.service.ProductReadUseCase;
import com.product.productcore.application.service.ProductWriteUseCase;
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
    protected ProductWriteUseCase productWriteUseCase;
    @MockBean
    protected ProductReadUseCase productReadUseCase;
    @MockBean
    protected InternalProductUseCase internalProductUseCase;
}
