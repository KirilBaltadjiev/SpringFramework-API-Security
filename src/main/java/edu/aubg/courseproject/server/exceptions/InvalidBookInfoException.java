package edu.aubg.courseproject.server.exceptions;

import java.util.List;

public class InvalidBookInfoException extends RuntimeException {

	public InvalidBookInfoException(String fieldName) {

		super(String.format("Book#%s must be set to a non-null value.", fieldName));
	}

	public InvalidBookInfoException(String fieldName, List<String> allowedValues) {

		super(String.format("Book#%s must be set to one of the following values: %s", fieldName, allowedValues));
	}

	public InvalidBookInfoException(String fieldName, Number minValue, Number maxValue) {

		super(String
				.format("Book#%s must be set to a value within the following range: [%d:%d]", fieldName, minValue,
						maxValue));
	}
}
