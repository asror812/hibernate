package com.example.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TraineeCreateDTO extends UserCreateDTO {

    private LocalDate dateOfBirth;

    private String address;

    public TraineeCreateDTO(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        super(firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
}