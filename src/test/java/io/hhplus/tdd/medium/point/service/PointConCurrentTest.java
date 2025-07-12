package io.hhplus.tdd.medium.point.service;

import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.infrastruction.database.UserPointTable;
import io.hhplus.tdd.point.infrastruction.database.entity.UserPoint;
import io.hhplus.tdd.point.usecase.PointChargingService;
import io.hhplus.tdd.point.usecase.PointUsageService;
import io.hhplus.tdd.point.usecase.PointViewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointConCurrentTest {

    @Autowired
    private PointChargingService pointChargingService;
    @Autowired
    private PointUsageService pointUsageService;
    @Autowired
    private PointViewService pointViewService;
    @Autowired
    private UserPointTable userPointTable;

    @Test
    void 동시에_다수의_충전_요청이_들어오더라도_누적되어_충전된다() throws Exception {
        //given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointChargingService.charge(1L, 1000L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        //then
        Point result = pointViewService.findPoint(1L);
        assertThat(result.getAmount()).isEqualTo(10_000);
    }

    @Test
    void 동시에_포인트를_사용하면_순차적으로_포인트가_차감된다() throws Exception {
        //given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        userPointTable.insertOrUpdate(2L, 100_000);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                   pointUsageService.use(2L, 10_000L);
                } catch (Exception ignore) {
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        //then
        Point point = pointViewService.findPoint(2L);
        assertThat(point.getAmount()).isEqualTo(0L);
    }

//    @Test
//    void 충전과_사용이_동시에_일어나도_누락되는_거래는_존재하지_않는다() throws Exception {
//        //given
//        int chargeThread = 10;
//        int useThread = 10;
//
//        ExecutorService executor = Executors.newFixedThreadPool(chargeThread + useThread);
//        CountDownLatch chargeCountDownLatch = new CountDownLatch(chargeThread);
//        CountDownLatch useCountDownLatch = new CountDownLatch(useThread);
//
//        userPointTable.insertOrUpdate(3L, 50_000);
//
//        //when
//        for (int i = 0; i < chargeThread; i++) {
//            executor.execute(() -> {
//                try {
//                    pointChargingService.charge(3L, 1000L);
//                } catch (Exception ignored) {
//                } finally {
//                    chargeCountDownLatch.countDown();
//                }
//            });
//        }
//        for (int i = 0; i < useThread; i++) {
//            executor.execute(() -> {
//                try {
//                    pointUsageService.use(3L, 1000L);
//                } catch (Exception ignored) {
//                } finally {
//                    useCountDownLatch.countDown();
//                }
//            });
//        }
//
//        chargeCountDownLatch.await();
//        useCountDownLatch.await();
//
//        UserPoint result = userPointTable.selectById(3L);
//
//        //then
//        assertThat(result.point()).isEqualTo(50_000);
//    }
}