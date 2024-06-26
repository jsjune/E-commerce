package com.loggingproducer;

import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect {

    private final LoggingProducer loggingProducer;

    @Before("execution(* com.*.controller..*(..))")
    public void beforeMethodExecution(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        log.info("Before executing method: " + className + "." + methodName);
        loggingProducer.sendMessage("logging-topic",
            "Before executing method: " + className + "." + methodName);
    }

    @AfterThrowing(pointcut = "execution(* com.*.controller..*(..))", throwing = "exception")
    public void afterThrowingMethodExecution(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();
        loggingProducer.sendMessage("logging-topic",
            "Exception thrown in method: " + className + "." + methodName + " Exception: "
                + exception.getMessage() + "\n Stack Trace: " + stackTrace);
    }

}
