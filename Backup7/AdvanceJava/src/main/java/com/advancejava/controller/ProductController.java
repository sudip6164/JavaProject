package com.advancejava.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.advancejava.model.Product;
import com.advancejava.model.User;
import com.advancejava.repository.ProductRepository;
import com.advancejava.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {
	@Autowired
    private ProductRepository productRepository;
	
    @Autowired
    private HttpSession session;
	
	@GetMapping("/productTable")
	public String UserTable(@ModelAttribute Product product, Model model) {
		if (session.getAttribute("activeUser") == null) {
			return "redirect:/login?sessionExpired=true"; 
        }
		model.addAttribute("productList", productRepository.findAll());
		return "dashboardTemplates/productTable.html";
	}
	
	@GetMapping("/addProductPage")
	public String AddProductPage(@ModelAttribute Product product, Model model) {
		if (session.getAttribute("activeUser") == null) {
			return "redirect:/login?sessionExpired=true"; 
        }
		model.addAttribute("productList", productRepository.findAll());
		return "dashboardTemplates/addProduct.html";
	}
	
	 @PostMapping("/addProduct")
	    public String AddProduct(@ModelAttribute Product product,Model model, @RequestParam("productPicture") MultipartFile productPicture) {
		 if (session.getAttribute("activeUser") == null) {
				return "redirect:/login?sessionExpired=true"; 
	        }
		 try {
		        if (!productPicture.isEmpty()) {
		            
		        	
		            File saveDir = new ClassPathResource("static/dashboardStatic/assets/img").getFile();
	                Path imagePath = Paths.get(saveDir.getAbsolutePath() + File.separator + productPicture.getOriginalFilename());
	                Files.copy(productPicture.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
	                product.setProductPictureName(productPicture.getOriginalFilename());
	                System.out.println(imagePath);
		        }

		        // Save user to database
		        productRepository.save(product);
		        model.addAttribute("productList", productRepository.findAll());
				 

		        return "redirect:/productTable";
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return "dashboardTemplates/addProduct.html";
		}
		
		@GetMapping("/editProduct/{productId}")
		public String editUserData(@PathVariable int productId, Model model) {
			if (session.getAttribute("activeUser") == null) {
				return "redirect:/login?sessionExpired=true"; 
	        }
			Product product = productRepository.getById(productId);
			model.addAttribute("p", product);
			return "dashboardTemplates/editProduct.html";
		}


		@PostMapping("/updateProduct")
		public String updateProductData(@ModelAttribute Product product, Model model, @RequestParam("productPicture") MultipartFile productPicture) {
			if (session.getAttribute("activeUser") == null) {
				return "redirect:/login?sessionExpired=true"; 
	        }
			try {
				
				File saveDir = new ClassPathResource("static/dashboardStatic/assets/img").getFile();
				Path imagePath = Paths.get(saveDir.getAbsolutePath() + File.separator + productPicture.getOriginalFilename());
				Files.copy(productPicture.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
				product.setProductPictureName(productPicture.getOriginalFilename());
				System.out.println(imagePath);				

				productRepository.save(product);
				model.addAttribute("productList", productRepository.findAll());
				return "redirect:/productTable";
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "dashboardTemplates/editProduct.html";
		}


		@GetMapping("/deleteProduct/{productId}")
		public String deleteAProduct(@PathVariable int productId, Model model) {
			if (session.getAttribute("activeUser") == null) {
				return "redirect:/login?sessionExpired=true"; 
	        }
			productRepository.deleteById(productId);
			model.addAttribute("productList", productRepository.findAll());
			return "redirect:/productTable";
		}
		
		@GetMapping("/shop")
		public String shopPage(@ModelAttribute Product product, Model model,HttpServletRequest request) {
			model.addAttribute("productList", productRepository.findAll());
			return "shop.html";
		}
		
		@GetMapping("/productDetails/{productId}")
		public String ProductDetailsPage(@PathVariable int productId, @ModelAttribute User u, Model model,HttpServletRequest request) {
		
			Product product = productRepository.getById(productId);
			model.addAttribute("p", product);
			return "productDetails.html";
		}
		
}
	 
