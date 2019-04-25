package edu.aubg.courseproject.server.advice;

import edu.aubg.courseproject.server.exceptions.InvalidBookInfoException;
import edu.aubg.courseproject.server.exceptions.BookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerAdvice {

	@ResponseBody
	@ExceptionHandler(BookNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String studentNotFoundHandler(BookNotFoundException ex) {

		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(InvalidBookInfoException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String invalidStudentInfoHandler(InvalidBookInfoException ex) {

		return ex.getMessage();
	}
}
