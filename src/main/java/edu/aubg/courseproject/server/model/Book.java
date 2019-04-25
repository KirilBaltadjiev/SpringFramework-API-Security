package edu.aubg.courseproject.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Book {

	private Long id;

	private String title;

	private String author;

	private String genre;

	private Double price;

	public Book(
			@JsonProperty("title") String title,
			@JsonProperty("author") String author,
			@JsonProperty("genre") String genre,
			@JsonProperty("price") Double price) {

		this.title = title;
		this.author = author;
		this.genre = genre;
		this.price = price;
	}

	public Long getId() {

		return id;
	}

	void setId(Long id) {

		this.id = id;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public String getAuthor() {

		return author;
	}

	public void setAuthor(String author) {

		this.author = author;
	}

	public String getGenre() {

		return genre;
	}

	public void setGenre(String genre) {

		this.genre = genre;
	}

	public Double getPrice() {

		return price;
	}

	public void setPrice(Double price) {

		this.price = price;
	}

	public Book from(Book book) {

		setTitle(book.getTitle());
		setAuthor(book.getAuthor());
		setGenre(book.getGenre());
		setPrice(book.getPrice());

		return this;
	}
}
