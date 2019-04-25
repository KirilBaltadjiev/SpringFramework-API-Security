package edu.aubg.courseproject.server.controllers;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import edu.aubg.courseproject.server.exceptions.InvalidBookInfoException;
import edu.aubg.courseproject.server.model.Book;
import edu.aubg.courseproject.server.model.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/books")
public class BooksV1Controller {

	@Autowired
	private BookRepository bookRepository;

	/**
	 * Retrieves all books.
	 *
	 * @return A list of all books under a single "books" key
	 */

	@GetMapping(
			value = "",
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	public Map<String, Collection<Book>> getAllBooks() {

		// TODO Annotate this method so that it becomes an endpoint that supports the following contract:
		// Request:
		//     GET /v1/books
		// Response:
		//     Status: 200 OK
		//     Header: Content-type: application/json

		return Collections.singletonMap("books", bookRepository.findAll());
	}

	/**
	 * Retrieves a single book by id, if one exists.
	 *
	 * @return A single book instance or a null object
	 */
	@GetMapping(
			value = "/{id}",
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	public Book getBook(@PathVariable Long id) {

		// TODO Annotate this method so that it becomes an endpoint that supports the following contract:
		// Request:
		//     GET /v1/books/{id}
		// Response:
		//     Status: 200 OK
		//     Header: Content-type: application/json

		return bookRepository.findById(id);
	}

	/**
	 * Creates a new book.
	 * <p/>
	 * The specified {@link Book} object must not contain an ID.
	 *
	 * @param book
	 * 		A {@link Book} instance that contains the values to set for the new book.
	 * @return The created book
	 * @throws InvalidBookInfoException
	 * 		if the {@link Book} object contains an ID.
	 */
	@PostMapping(
			value = "",
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
			consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	@ResponseStatus(HttpStatus.CREATED)
	public Book createBook(@RequestBody Book book) {

		// TODO Annotate this method so that it becomes an endpoint that supports the following contract:
		// Request:
		//     POST /v1/books
		//     Header: Content-type: application/json
		// Response:
		//     Status: 201 Created
		//     Header: Content-type: application/json

		if (book.getId() != null) {

			throw new InvalidBookInfoException("Book must not contain an ID");
		}

		return bookRepository.save(book);
	}

	/**
	 * Updates an existing book.
	 * <p/>
	 * The specified {@link Book} object must not contain an ID. Instead, it is specified using the {@code id}
	 * argument.
	 *
	 * @param id
	 * 		The ID of the book to update.
	 * @param book
	 * 		A {@link Book} instance that contains the values to set for the book with the specified {@code} id.
	 * @return The updated book
	 * @throws InvalidBookInfoException
	 * 		if the {@link Book} object contains an ID.
	 */

	@PutMapping(
			value = "/{id}",
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
			consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
	)

	public Book updateBook(@PathVariable Long id, @RequestBody Book book) {

		// TODO Annotate this method so that it becomes an endpoint that supports the following contract:
		// Request:
		//     PUT /v1/books/{id}
		//     Header: Content-type: application/json
		// Response:
		//     Status: 200 OK
		//     Header: Content-type: application/json

		if (book.getId() != null) {

			throw new InvalidBookInfoException("Book must not contain an ID");
		}

		return bookRepository.save(bookRepository.findById(id).from(book));
	}

	/**
	 * Deletes a book by ID.
	 *
	 * @param id
	 * 		The ID of the book to delete
	 */

	@DeleteMapping(
			value = "/{id}"
	)
	@ResponseStatus (HttpStatus.NO_CONTENT)
	public void deleteBook(@PathVariable Long id) {

		// TODO Annotate this method so that it becomes an endpoint that supports the following contract:
		// Request:
		//     DELETE /v1/books/{id}
		//     Header: Content-type: application/json
		// Response:
		//     Status: 204 No Content
	}
}
