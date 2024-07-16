package com.product.productapi.testConfig;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.productapi.AppConfig;
import com.product.productapi.controller.ProductController;
import com.product.productapi.controller.internal.ProductInternalController;
import com.product.productapi.usecase.InternalProductUseCase;
import com.product.productapi.usecase.ProductReadUseCase;
import com.product.productapi.usecase.ProductWriteUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {ProductController.class, ProductInternalController.class})
@ContextConfiguration(classes = {AppConfig.class})
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
