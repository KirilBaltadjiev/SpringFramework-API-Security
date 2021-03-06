package edu.aubg.courseproject;

import edu.aubg.courseproject.server.model.BookRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}

	@Bean
	public BookRepository getBookRepository() {

		return new BookRepository();
	}

}
