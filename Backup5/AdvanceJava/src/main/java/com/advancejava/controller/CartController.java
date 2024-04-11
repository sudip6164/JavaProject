package com.advancejava.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.advancejava.model.Product;
import com.advancejava.model.Cart;
import com.advancejava.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {
	@Autowired
	ProductRepository productRepository;
	
	@GetMapping("/cart")
	public String cartPage() {
		return "cart.html";
	}
	
	@GetMapping("/addtoCart/{productId}")
	public String addToCart(@PathVariable("productId") int productId, Model model) {
	    Product product = productRepository.getById(productId);
	    if (product != null) {
	        Cart cart = new Cart();
	        cart.setProduct(product);
	        cart.setQuantity(1); // You can set the quantity as per requirement
	        
	        List<Cart> cartItems = new ArrayList<>();
	        cartItems.add(cart);
	        
	        model.addAttribute("cartItems", cartItems);
	    }
	    return "redirect:/cart"; // Redirect to homepage or wherever appropriate
	}

}
