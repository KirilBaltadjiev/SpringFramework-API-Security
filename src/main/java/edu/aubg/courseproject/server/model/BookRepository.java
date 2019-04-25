package edu.aubg.courseproject.server.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import edu.aubg.courseproject.server.exceptions.InvalidBookInfoException;
import edu.aubg.courseproject.server.exceptions.BookNotFoundException;
import org.springframework.data.repository.Repository;

/**
 * Thread-safe book repository.
 */
public class BookRepository implements Repository<Book, Long> {

	private static final List<String> GENRES =
			Collections.unmodifiableList(Arrays.asList("horror", "sci-fi", "adventure", "romance"));


	private final Map<Long, Book> bookStorage = new HashMap<>();

	private final AtomicLong idGenerator = new AtomicLong(0);

	public BookRepository() {

		// Initialize the repository with some dummy data
		save(new Book("The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "sci-fi", 5.66));
		save(new Book("The Notebook", "Nicholas Sparks", "romance", 10.40));
	}

	public synchronized Book save(Book book) {

		validate(book);

		if (book.getId() == null) {

			book.setId(idGenerator.incrementAndGet());
		}

		bookStorage.put(book.getId(), book);

		return book;
	}

	public synchronized Book findById(Long id) {

		Book book = bookStorage.get(id);

		if (book == null) {

			throw new BookNotFoundException(id);
		}

		return book;
	}

	public synchronized Collection<Book> findAll() {

		return bookStorage.values();
	}

	public synchronized boolean existsById(Long id) {

		return bookStorage.containsKey(id);
	}

	public synchronized long count() {

		return bookStorage.size();
	}

	public synchronized void deleteById(Long id) {

		Book book = bookStorage.remove(id);

		if (book == null) {

			throw new BookNotFoundException(id);
		}
	}

	public synchronized void delete(Book book) {

		deleteById(book.getId());
	}

	public synchronized void deleteAll() {

		bookStorage.clear();
	}

	private <B extends Book> B validate(B book) {

		if (book.getId() != null && !existsById(book.getId())) {

			throw new BookNotFoundException(book.getId());
		}

		if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {

			throw new InvalidBookInfoException("name");
		}

		if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {

			throw new InvalidBookInfoException("author");
		}

		if (book.getGenre() == null || book.getGenre().trim().isEmpty()
				|| !GENRES.contains(book.getGenre())) {

			throw new InvalidBookInfoException("genre", GENRES);
		}

		if (book.getPrice() == null || book.getPrice() < 0) {

			throw new InvalidBookInfoException("Price");
		}

		return book;
	}
}
