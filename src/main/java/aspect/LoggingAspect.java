package aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* controller.*.*(..))")
    public void logControllerStart(JoinPoint joinPoint) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        logger.info("REQUEST_START - method={}.{} traceId={}", className, methodName, traceId);
    }

    @AfterReturning("execution(* controller.*.*(..))")
    public void logControllerSuccess(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        logger.info("REQUEST_SUCCESS - method={}.{}", className, methodName);
        MDC.clear();
    }

    @Before("execution(* service.OrderService.placeOrder(..))")
    public void logOrderPlacement(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = args.length > 1 ? (Long) args[1] : null;
        
        logger.info("ORDER_PLACEMENT_START - userId={} transactionActive=true", userId);
    }

    @AfterReturning(pointcut = "execution(* service.OrderService.placeOrder(..))", returning = "result")
    public void logOrderSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userId = args.length > 1 ? (Long) args[1] : null;
        
        logger.info("ORDER_PLACEMENT_SUCCESS - userId={}", userId);
    }

    @AfterThrowing(pointcut = "execution(* service.*.*(..))", throwing = "ex")
    public void logServiceException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        logger.error("SERVICE_ERROR - method={}.{} error={}", className, methodName, ex.getMessage());
    }
}