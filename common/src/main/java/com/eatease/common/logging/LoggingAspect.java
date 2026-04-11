package com.eatease.common.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@SuppressWarnings("unused")
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @PostConstruct
    public void init() {
        log.info("[AOP] LoggingAspect initialized and ready to intercept service and controller calls.");
    }

    // Pointcut matches all methods in any package under "com.eatease" that ends
    // with "controller" or "service"
    @Pointcut("within(com.eatease..service..*) || within(com.eatease..controller..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger classLogger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
        String methodName = joinPoint.getSignature().getName();

        if (classLogger.isDebugEnabled()) {
            classLogger.debug("Enter: {}() with argument[s] = {}", methodName, Arrays.toString(joinPoint.getArgs()));
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;

            if (classLogger.isDebugEnabled()) {
                classLogger.debug("Exit: {}() with result = {} (Execution time: {} ms)", methodName, result,
                        elapsedTime);
            } else {
                classLogger.info("[AOP] Executed: {}() in {} ms", methodName, elapsedTime);
            }
            return result;
        } catch (IllegalArgumentException e) {
            classLogger.error("Illegal argument: {} in {}()", Arrays.toString(joinPoint.getArgs()), methodName);
            throw e;
        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - start;
            classLogger.error("[AOP] Exception in {}() after {} ms. Cause: {}", methodName, elapsedTime,
                    e.getMessage());
            throw e;
        }
    }
}
