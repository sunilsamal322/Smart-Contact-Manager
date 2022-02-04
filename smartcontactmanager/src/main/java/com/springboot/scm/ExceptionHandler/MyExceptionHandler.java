package com.springboot.scm.ExceptionHandler;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Controller
public class MyExceptionHandler implements ErrorController{

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = Exception.class)
	public String exceptionHandler()
	{
		return "Excep_page";
	}
	
	@RequestMapping("/error")
	public String handleError()
	{
		return "Excep_page";
	}
}
