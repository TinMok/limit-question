package org.example;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LimitInterceptor implements HandlerInterceptor {

    LimitInterceptor(StringRedisTemplate stringRedisTemplate, DefaultRedisScript<Boolean> redisScript) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisScript = redisScript;
    }

    DefaultRedisScript<Boolean> redisScript;

    StringRedisTemplate stringRedisTemplate;

    public static final String limit = "10000";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        Map pathVariables = (Map)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables == null) {
            return true;
        }
        long minuteCount = startTime / 60000;
        Object userId = Optional.ofNullable(pathVariables.get("userId")).orElse("");
        if (!"".equals(userId)) {
            List<String> keys = Arrays.asList("user:" + userId.toString() + ":" + minuteCount);
            Boolean execute = stringRedisTemplate.execute(redisScript, keys, limit);
            if (Boolean.TRUE == execute) {
                // over limit
                response.setStatus(500);
                response.getWriter().print("your request over limit");
                SimpleStatisticsUtil.addQueue(System.currentTimeMillis() - startTime);
                return false;
            } else {
                // not over limit
                SimpleStatisticsUtil.addQueue(System.currentTimeMillis() - startTime);
                return true;
            }
        }
        return true;
    }

}
