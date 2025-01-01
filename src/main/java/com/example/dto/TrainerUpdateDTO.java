package com.example.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerUpdateDTO extends UserUpdateDTO{

    @NotNull
    private UUID id;
    
    @NotNull
    private String trainingTypeName;
 
    
}