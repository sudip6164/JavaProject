package com.advancejava.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Enable web security
public class SecurityConfig {

  @Autowired
  private UserDetailsService userDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable() // Disable csrf for simplicity (consider enabling for production)
        .authorizeRequests()
//          .requestMatchers("/products").hasAnyRole("CUSTOMER") // Allow customer to view products
//          .requestMatchers("/products/**").hasRole("STAFF") // Allow staff to CRUD products
//          .requestMatchers("/admin/**","/admindashboard/**").hasRole("ADMIN") // Admin has full access
          .requestMatchers("/**").permitAll() // Admin has full access
        .and()
          .formLogin().disable() // Disable default login form
          .logout().disable()
          .httpBasic(); // Enable HTTP Basic authentication

    return http.build();
  }
}
