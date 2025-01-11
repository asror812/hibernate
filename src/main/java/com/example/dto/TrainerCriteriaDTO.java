package com.example.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerCriteriaDTO {
 
   @NotBlank
   private String username;

   private LocalDate from;
   private LocalDate to;
   private String traineeName;
   private String trainingType;
   
}
