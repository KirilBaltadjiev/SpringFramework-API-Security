package edu.aubg.courseproject.server.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ping")
public class PingController {

	@GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
	public String ping() {

		return "PONG";
	}
}
