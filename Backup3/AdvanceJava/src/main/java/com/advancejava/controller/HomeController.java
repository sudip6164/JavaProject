package com.advancejava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@GetMapping("/")
	public String firstPage() {
		return "index.html";
	}
	@GetMapping("/404")
	public String ErrorPage() {
		return "404.html";
	}
}
