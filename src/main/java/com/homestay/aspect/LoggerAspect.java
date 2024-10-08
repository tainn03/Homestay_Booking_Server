package com.homestay.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Aspect
@Component
public class LoggerAspect {
    @Around("execution(* com.homestay.controller.*.*(..))")
    public Object logAroundServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("START METHOD EXECUTION: {}", joinPoint.getSignature());
        Instant start = Instant.now();
        Object result = joinPoint.proceed();
        Instant end = Instant.now();
        log.info("TIME TAKEN: {}ms", end.toEpochMilli() - start.toEpochMilli());
        return result;
    }

    @AfterThrowing(pointcut = "execution(* com.homestay.service.*.*(..))", throwing = "exception")
    public void logAfterThrowingServiceMethods(Exception exception) {
        log.error("Exception occurred in service method {} due to: {}", exception.getStackTrace()[0].getMethodName(), exception.getMessage());
    }
}
