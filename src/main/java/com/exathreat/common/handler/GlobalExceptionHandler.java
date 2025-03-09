package com.exathreat.common.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ModelAndView handle(AccessDeniedException exception, HttpServletRequest request) {
		return new ModelAndView("common/denied");
	}
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handle(Exception exception, HttpServletRequest request) {
		exception.printStackTrace();
		return new ModelAndView("common/error");
	}
}