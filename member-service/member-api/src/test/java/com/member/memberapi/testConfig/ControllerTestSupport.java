package com.member.memberapi.testConfig;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.membercore.application.service.AuthUseCase;
import com.member.membercore.application.service.CartUseCase;
import com.member.membercore.application.service.EmailUseCase;
import com.member.membercore.application.service.InternalCartUseCase;
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
    protected AuthUseCase authUseCase;
    @MockBean
    protected CartUseCase cartUseCase;
    @MockBean
    protected EmailUseCase emailUseCase;
    @MockBean
    protected InternalCartUseCase internalCartUseCase;
}
