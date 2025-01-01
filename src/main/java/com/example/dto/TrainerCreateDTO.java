package com.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerCreateDTO extends UserCreateDTO {

    @NotNull
    private String trainingTypeName;

    public TrainerCreateDTO(String firstName, String lastName, String trainingTypeName){
        super(firstName, lastName);
        this.trainingTypeName = trainingTypeName;
    }
}