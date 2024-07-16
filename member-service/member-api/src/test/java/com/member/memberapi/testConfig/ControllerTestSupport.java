package com.member.memberapi.testConfig;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.memberapi.AppConfig;
import com.member.memberapi.controller.CartController;
import com.member.memberapi.controller.EmailController;
import com.member.memberapi.controller.MemberController;
import com.member.memberapi.controller.internal.CartInternalController;
import com.member.memberapi.controller.internal.MemberInternalController;
import com.member.memberapi.usecase.AuthUseCase;
import com.member.memberapi.usecase.CartUseCase;
import com.member.memberapi.usecase.EmailUseCase;
import com.member.memberapi.usecase.InternalCartUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {CartController.class, EmailController.class, MemberController.class, CartInternalController.class, MemberInternalController.class})
@ContextConfiguration(classes = {AppConfig.class})
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
