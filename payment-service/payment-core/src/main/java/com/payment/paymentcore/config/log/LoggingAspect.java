package com.payment.paymentcore.config.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect {

    private final LoggingProducer loggingProducer;

    @Around("execution(* com.*.*.controller..*(..))")
    public Object aroundControllerMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Result result = getResult(joinPoint);
        return loggingProcess(joinPoint, result);
    }

    @Around("execution(* com.*.*.infrastructure.kafka.PaymentKafkaProducer.*(..))")
    public Object aroundKafkaProducerMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Result result = getResult(joinPoint);
        return loggingProcess(joinPoint, result);
    }

    @Around("execution(* com.payment.paymentconsumer.PaymentEventConsumer.*(..))")
    public Object aroundKafkaConsumerMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Result result = getResult(joinPoint);
        return loggingProcess(joinPoint, result);
    }

    private Object loggingProcess(ProceedingJoinPoint joinPoint, Result result) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object proceed = joinPoint.proceed();
            long endTime = System.currentTimeMillis() - startTime;
            log.info("Method executed: {}.{} in {} ms", result.className(), result.methodName(), endTime);
            loggingProducer.sendMessage("logging-topic",
                "[" + result.traceId() + "-" + result.spanId()
                    + "] Method executed: " + result.className() + "."
                    + result.methodName() + " in " + endTime + " ms");
            return proceed;
        } catch (Throwable throwable) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTrace = sw.toString();
            log.error("Exception in {}.{}() with cause = {} and exception = {}", result.className(),
                result.methodName(), throwable.getCause(), throwable.getMessage());
            loggingProducer.sendMessage("logging-error-topic",
                "[" + result.traceId() + "-" + result.spanId()
                    + "] Method executed: " + result.className() + "."
                    + result.methodName() + " in " + elapsedTime + " ms"
                    + " with exception: " + throwable.getMessage() + "\n Stack Trace: " + stackTrace);
            throw throwable;
        }
    }

    private Result getResult(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
//        Span currentSpan = tracer.currentSpan();
//        String traceId = currentSpan != null ? currentSpan.context().traceId() : "no-trace-id";
//        String spanId = currentSpan != null ? currentSpan.context().spanId() : "no-span-id";
//        return new Result(className, methodName, traceId, spanId);
        return new Result(className, methodName, "traceId", "spanId");
    }

    private record Result(String className, String methodName, String traceId, String spanId) {

    }

}
