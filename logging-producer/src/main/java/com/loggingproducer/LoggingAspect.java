package com.loggingproducer;

import io.micrometer.tracing.Span;
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

    @Before("execution(* com.*.controller..*(..)) || execution(* com.*.usecase.kafka..*(..))")
    public void beforeMethodExecution(JoinPoint joinPoint) {
        Result result = getResult(joinPoint);
        loggingProducer.sendMessage("logging-topic",
            "[" + result.traceId() + "-" + result.spanId()
                + "] Before executing method: " + result.className() + "."
                + result.methodName());
    }

    @AfterThrowing(pointcut = "execution(* com.*.controller..*(..)) || execution(* com.*.usecase.kafka..*(..))", throwing = "exception")
    public void afterThrowingMethodExecution(JoinPoint joinPoint, Throwable exception) {
        Result result = getResult(joinPoint);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();
        loggingProducer.sendMessage("logging-topic",
            "[" + result.traceId + "-" + result.spanId + "] Exception thrown in method: " + result.className + "." + result.methodName + " Exception: "
                + exception.getMessage() + "\n Stack Trace: " + stackTrace);
    }

    private Result getResult(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Span currentSpan = tracer.currentSpan();
        String traceId = currentSpan != null ? currentSpan.context().traceId() : "no-trace-id";
        String spanId = currentSpan != null ? currentSpan.context().spanId() : "no-span-id";
        return new Result(className, methodName, traceId, spanId);
    }

    private record Result(String className, String methodName, String traceId, String spanId) {

    }

}
