package io.hhplus.tdd.small.point.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.common.exception.controller.ApiControllerAdvice;
import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import io.hhplus.tdd.mock.PointFixture;
import io.hhplus.tdd.mock.PointLogFixture;
import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.controller.dto.PointDto;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.usecase.PointChargingService;
import io.hhplus.tdd.point.usecase.PointUsageService;
import io.hhplus.tdd.point.usecase.PointViewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * (공통 이유)
 * 성공인 경우 사용자가 의도한 응답 요소와 값들을 전달받는지
 * 오류인 경우 사용자가 의도한 상태코드와 메시지를 받는지 확인.
 * 사용자가 응답을 어떻게 받는지는 중요한 테스트라고 생각하여
 * 모든 메서드의 성공/실패의 경우를 확인하도록 의도
 * */
@WebMvcTest(PointController.class)
@Import(ApiControllerAdvice.class)
@AutoConfigureMockMvc
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PointChargingService pointChargingService;
    @MockBean
    private PointUsageService pointUsageService;
    @MockBean
    private PointViewService pointViewService;

    @Test
    void 포인트를_조회_시_200과_포인트정보를_반환한다() throws Exception {
        //given
        Point point = PointFixture.createDefaultPoint();
        given(pointViewService.findPoint(point.getUserId())).willReturn(point);

        //when & then
        mockMvc.perform(get("/point/{id}", point.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(point.getUserId()))
                .andExpect(jsonPath("$.point").value(point.getAmount()))
                .andExpect(jsonPath("$.updateMillis").value(point.getUpdateMillis()))
                .andDo(print());
    }

    @Test
    void 포인트_로그_조회시_200과_리스트를_반환한다() throws Exception {
        //given
        PointLog pointLog = PointLogFixture.createPointLog(1000L, TransactionType.CHARGE);

        given(pointViewService.findPointLogDesc(pointLog.getId())).willReturn(List.of(pointLog));

        //when & then
        mockMvc.perform(get("/point/{id}/histories", pointLog.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(pointLog.getId()))
                .andExpect(jsonPath("$[0].point").value(pointLog.getPoint()))
                .andExpect(jsonPath("$[0].type").value(pointLog.getType().name()))
                .andExpect(jsonPath("$[0].updateMillis").value(pointLog.getUpdateMillis()))
                .andDo(print());
    }

    @Test
    void 충전이_성공하면_200응답과_충전한_뒤_잔액_포인트를_반환한다() throws Exception {
        //given
        long chargePoint = 1000L;
        Point point = PointFixture.createPoint(chargePoint);
        given(pointChargingService.charge(point.getUserId(), chargePoint)).willReturn(point);

        PointDto.Request request = new PointDto.Request(chargePoint);
        String content = objectMapper.writeValueAsString(request);


        //when & then
        mockMvc.perform(patch("/point/{id}/charge", point.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(point.getUserId()))
                .andExpect(jsonPath("$.point").value(point.getAmount()))
                .andExpect(jsonPath("$.updateMillis").value(point.getUpdateMillis()))
                .andDo(print());
    }

    @Test
    void 포인트_충전_중_정책을_위반하면_400을_반환한다() throws Exception {
        //given
        given(pointChargingService.charge(any(), any())).willThrow(new CommonException(ErrorCode.POLICY_VIOLATION));

        PointDto.Request request = new PointDto.Request(1000L);
        String content = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(patch("/point/{id}/charge", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message", startsWith("정책 위반")))
                .andDo(print());
    }

    @Test
    void 사용에_성공하면_200과_잔액_포인트를_반환한다() throws Exception {
        //given
        long usage = 1000L;
        Point point = PointFixture.createDefaultPoint();
        given(pointUsageService.use(point.getUserId(), usage)).willReturn(point);

        PointDto.Request request = new PointDto.Request(usage);
        String content = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(patch("/point/{id}/use", point.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(point.getUserId()))
                .andExpect(jsonPath("$.point").value(point.getAmount()))
                .andExpect(jsonPath("$.updateMillis").value(point.getUpdateMillis()))
                .andDo(print());
    }

    @Test
    void 포인트_사용_중_정책을_위반하면_400을_반환한다() throws Exception {
        //given
        given(pointUsageService.use(any(), any())).willThrow(new CommonException(ErrorCode.POLICY_VIOLATION));

        PointDto.Request request = new PointDto.Request(100_000L);
        String content = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(patch("/point/{id}/use", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message", startsWith("정책 위반")))
                .andDo(print());
    }

}