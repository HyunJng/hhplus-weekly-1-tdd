package io.hhplus.tdd.point.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.point.controller.dto.PointDto;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.infrastruction.database.PointHistoryTable;
import io.hhplus.tdd.point.infrastruction.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserPointTable userPointTable;
    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Test
    void 포인트를_조회_시_200과_포인트정보를_반환한다() throws Exception {
        //given
        userPointTable.insertOrUpdate(1L, 5000L);

        //when & then
        mockMvc.perform(get("/point/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.point").value(5000))
                .andExpect(jsonPath("$.updateMillis").isNotEmpty())
                .andDo(print());
    }

    @Test
    void 포인트_로그_조회시_200과_리스트를_반환한다() throws Exception {
        //given
        pointHistoryTable.insert(1L, 10_000L, TransactionType.CHARGE, 1234L);
        pointHistoryTable.insert(1L, 5000L, TransactionType.USE, 5678L);

        //when & then
        mockMvc.perform(get("/point/{id}/histories", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].point").value(5000))
                .andExpect(jsonPath("$[0].type").value("USE"))
                .andExpect(jsonPath("$[0].updateMillis").value(5678L))
                .andExpect(jsonPath("$[1].userId").value(1))
                .andExpect(jsonPath("$[1].type").value("CHARGE"))
                .andExpect(jsonPath("$[1].point").value(10_000))
                .andExpect(jsonPath("$[1].updateMillis").value(1234L))
                .andDo(print());
    }

    @Test
    void 포인트_로그가_없으면_빈리스트를_반환한다() throws Exception {
        mockMvc.perform(get("/point/{id}/histories", 999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    void 충전이_성공하면_200응답과_충전한_뒤_잔액_포인트를_반환한다() throws Exception {
        //given
        PointDto.Request request = new PointDto.Request(1000L);
        String content = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(patch("/point/{id}/charge", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(3))
                .andExpect(jsonPath("$.point").value(1000))
                .andExpect(jsonPath("$.updateMillis").isNotEmpty())
                .andDo(print());
    }

    @ParameterizedTest(name = "{0}원은 충전금액 정책 위반")
    @ValueSource(longs = {-1000L, 0L, 999L, 10001L})
    void 충전금액_정책에_어긋나는_금액을_보내면_400을_반환한다(long amount) throws Exception {
        //given
        PointDto.Request request = new PointDto.Request(amount);
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
        userPointTable.insertOrUpdate(4L, 15_000L);

        PointDto.Request request = new PointDto.Request(10_000L);
        String content = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(patch("/point/{id}/use", 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(4L))
                .andExpect(jsonPath("$.point").value(5000))
                .andExpect(jsonPath("$.updateMillis").isNotEmpty())
                .andDo(print());
    }

    @Test
    void 잔액보다_많은_포인트를_사용하면_400을_반환한다() throws Exception {
        //given
        userPointTable.insertOrUpdate(5L, 1000L);

        PointDto.Request request = new PointDto.Request(100_000L);
        String content = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(patch("/point/{id}/use", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message", startsWith("정책 위반")))
                .andDo(print());
    }

    @ParameterizedTest(name = "{0}원은 최소 사용 금액 위반")
    @ValueSource(longs = {-1000, 0, 500, 999})
    void 최소_사용금액_위반시_400을_반환한다(long useAmount) throws Exception {
        //given
        userPointTable.insertOrUpdate(6L, 100_000L);
        PointDto.Request request = new PointDto.Request(useAmount);
        String content = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(patch("/point/{id}/use", 6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message", startsWith("정책 위반")))
                .andDo(print());
    }
}