package com.example.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangePasswordDTO {

   @NotNull
   private UUID id;

   @Size(min = 10, max = 10, message = "Password must be exactly 10 characters long")
   private String newPassword;

   @NotBlank
   private String entityType;
      
}
