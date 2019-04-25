package edu.aubg.courseproject.server.exceptions;

public class BookNotFoundException extends RuntimeException {

	public BookNotFoundException(Long id) {

		super(String.format("Book with ID %d does not exist", id));
	}
}
