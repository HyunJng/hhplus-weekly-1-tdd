package io.hhplus.tdd.point.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 충전이_성공하면_200응답과_충전한_뒤_포인트_값을_반환받는다() throws Exception {
        //given & when & then
        mockMvc.perform(patch("/point/{id}/charge", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.point").value(1000))
                .andExpect(jsonPath("$.updateMillis").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest(name = "{0}원을 요청하면 오류코드와 오류메시지를 반환받는다")
    @ValueSource(strings = {"-1000", "0", "999", "10001"})
    void 충전금액_정책에_맞지_않는_금액을_보내면_400응답과_오류코드와_오류메시지를_반환받는다(String amount) throws Exception {
        //given & when & then
        mockMvc.perform(patch("/point/{id}/charge", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(amount))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("포인트 충전 금액 정책 위반"))
                .andDo(MockMvcResultHandlers.print());
    }
}