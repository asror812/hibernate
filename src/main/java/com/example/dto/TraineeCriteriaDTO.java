package com.example.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TraineeCriteriaDTO {
   
   @NotBlank
   private String username;
   
   private String trainerName;
   private LocalDate from;
   private LocalDate to;
   private String trainingType;
   
}
