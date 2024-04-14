package com.advancejava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@GetMapping("/")
	public String firstPage(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return "index.html";
	}
	@GetMapping("/404")
	public String ErrorPage() {
		return "404.html";
	}
}
