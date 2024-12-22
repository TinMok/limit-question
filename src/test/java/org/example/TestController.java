package org.example;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class TestController {

    public static final String BASE_URL = "http://localhost:8088";

    public static final String USER1 = "1";
    public static final String USER2 = "2";
    public static final String USER3 = "3";
    public static final String USER4 = "4";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        String object = restTemplate.getForObject(BASE_URL + "/hello", String.class);
        System.out.println(object);
    }

    private volatile long previousMinuteCount = System.currentTimeMillis() / 60000;

    @Test
    public void user1ApiTest() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(10);
        Random random = new Random();
        AtomicInteger sendSecondCount = new AtomicInteger(0);
        CountDownLatch countDownLatch = new CountDownLatch(90 * 500 * 4);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            long currentMinuteCount = System.currentTimeMillis() / 60000;
            int second;
            if (currentMinuteCount == previousMinuteCount) {
                second = sendSecondCount.incrementAndGet();
            } else {
                sendSecondCount.set(1);
                second = 1;
                previousMinuteCount = currentMinuteCount;
            }
            log.info("currentMinute:{},second:{}",currentMinuteCount, second);
            Stream.of(USER1, USER2, USER3, USER4).parallel().forEach(userId -> {
                for (int i = 0; i < 500; i++) {
                    executor.execute(() -> {
                        try {
                            int number = random.nextInt(3);
                            String result = null;
                            switch (number) {
                                case 0:
                                    result = restTemplate.getForObject(BASE_URL + "/api1/" + userId, String.class);
                                    countDownLatch.countDown();
                                    if (second <= 10000/500) {
                                        Assertions.assertEquals("api1 success userId:" + userId, result);
                                    } else {
                                        Assertions.assertEquals("your request over limit", result);
                                    }
                                    break;
                                case 1:
                                    result = restTemplate.postForObject(BASE_URL + "/api2/" + userId, null, String.class);
                                    countDownLatch.countDown();
                                    if (second <= 10000/500) {
                                        Assertions.assertEquals("api2 success userId:" + userId, result);
                                    } else {
                                        Assertions.assertEquals("your request over limit", result);
                                    }
                                    break;
                                case 2:
                                    restTemplate.put(BASE_URL + "/api3/" + userId, null);
                                    countDownLatch.countDown();
                                    break;
                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        }, 1000 - System.currentTimeMillis() % 1000, 1000, TimeUnit.MILLISECONDS);
        countDownLatch.await();
        String result = restTemplate.getForObject(BASE_URL + "/result", String.class);
        log.info(result);
        scheduledExecutorService.shutdown();
        executor.shutdown();
    }

}
