package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BusinessController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/api1/{userId}")
    public String api1(@PathVariable String userId) {
        return "api1 success userId:" + userId;
    }

    @PostMapping("/api2/{userId}")
    public String api2(@PathVariable String userId) {
        return "api2 success userId:" + userId;
    }

    @PutMapping("/api3/{userId}")
    public String api3(@PathVariable String userId) {
        return "api3 success userId:" + userId;
    }

    @GetMapping("/result")
    public String testResult() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("size:").append(SimpleStatisticsUtil.getQueueCount()).append(",min:")
                .append(SimpleStatisticsUtil.getMin()).append(",max:").append(SimpleStatisticsUtil.getMax())
                .append(",average:").append(SimpleStatisticsUtil.getAverage());
        return stringBuilder.toString();
    }

}
