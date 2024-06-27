package com.loggingproducer;

import io.micrometer.tracing.Tracer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
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
    private final Tracer tracer;

    @Before("execution(* com.*.controller..*(..))")
    public void beforeMethodExecution(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        String traceId = Objects.requireNonNull(tracer.currentTraceContext().context()).traceId();
        String spanId = Objects.requireNonNull(tracer.currentTraceContext().context()).spanId();
        log.info("Before executing method: " + className + "." + methodName);
        loggingProducer.sendMessage("logging-topic",
            "[" + traceId + "-" + spanId + "] Before executing method: " + className + "."
                + methodName);
    }

    @AfterThrowing(pointcut = "execution(* com.*.controller..*(..))", throwing = "exception")
    public void afterThrowingMethodExecution(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        String traceId = Objects.requireNonNull(tracer.currentTraceContext().context()).traceId();
        String spanId = Objects.requireNonNull(tracer.currentTraceContext().context()).spanId();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();
        loggingProducer.sendMessage("logging-topic",
            "[" + traceId + "-" + spanId + "] Exception thrown in method: " + className + "." + methodName + " Exception: "
                + exception.getMessage() + "\n Stack Trace: " + stackTrace);
    }

}
