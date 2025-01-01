package com.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

   @NotNull
   private String firstName;

   @NotNull
   private String lastName;

   @NotNull
   private String username;

   @NotNull
   private String password;

   private boolean active;

}
