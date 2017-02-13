package com.lifeix.football.service.aggregation;

import org.springframework.web.bind.annotation.ControllerAdvice;

import com.lifeix.football.common.application.ExceptionHandlerAdvice;

/**
 * 参见 https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc 异常处理类 将通用的异常放于此处
 * 
 * add not find exception gcc
 * 
 * @author zengguangwei,gcc
 *
 */
@ControllerAdvice
public class MyExceptionHandlerAdvice extends ExceptionHandlerAdvice {

}