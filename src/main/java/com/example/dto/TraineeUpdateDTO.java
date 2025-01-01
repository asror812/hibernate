package com.example.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TraineeUpdateDTO extends UserUpdateDTO{
    @NotNull
    private UUID id;
    
    private LocalDate dateOfBirth;
    private String address;

}