package com.example.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class RegisterResponse {
    
    
    
     private Long id;
    
     private String name; 
     
     private String email;
     
     private String role;
    
    }