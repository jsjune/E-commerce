package com.deliveryservice.common;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.deliveryservice.IntegrationTestSupport;
import com.deliveryservice.utils.AesUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AesUtilTest extends IntegrationTestSupport {

    @Autowired
    private AesUtil aesUtil;

    @DisplayName("암호화 복호화")
    @Test
    void encode_and_decode() throws Exception {
        // given
        String name = "test";

        // when
        String encodeName = aesUtil.aesEncode(name);
        String decodeName = aesUtil.aesDecode(encodeName);

        // then
        assertEquals(decodeName, name);
    }
}
