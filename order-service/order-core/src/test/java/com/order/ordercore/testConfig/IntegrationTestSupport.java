package com.order.ordercore.testConfig;

import com.order.ordercore.AppConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = AppConfig.class)
@Transactional
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

}
