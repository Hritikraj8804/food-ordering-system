package aspect;

//package com.foodapp.food_ordering_system.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect // 1. Defines this class as the Aspect
@Component // 2. Makes it a Spring Bean
public class LoggingAspect {

 private final Logger logger = LoggerFactory.getLogger(this.getClass());

 /**
  * @Before advice: Executes BEFORE the target method runs.
  * The Pointcut expression targets the 'placeOrder' method in the OrderService.
  */
 @Before("execution(* com.foodapp.food_ordering_system.service.OrderService.placeOrder(..))")
 public void logOrderPlacement(JoinPoint joinPoint) {
     
     Object[] args = joinPoint.getArgs();
     // The userId is the second argument (index 1) passed to the placeOrder method
     Long userId = (Long) args[1]; 

     logger.info("==================================================");
     logger.info("⚡ AOP INTERCEPTED: Order placement initiated.");
     logger.info("⚡ User ID {} is attempting to place an order.", userId);
     logger.info("⚡ Spring @Transactional has been activated.");
     logger.info("==================================================");
 }
}
