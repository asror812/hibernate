package com.example.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAuthDTO {

   @NotNull
   private String username;

   @Size(min = 10, max = 10, message = "Password must be exactly 10 characters long")
   private String password;

}