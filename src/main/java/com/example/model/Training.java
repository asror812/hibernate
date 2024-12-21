package com.example.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter@Setter
@NoArgsConstructor
public class Training {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private UUID traineeId;

    @ManyToOne
    @JoinColumn(name="trainer_id")
    private UUID trainerId;

    private String trainingName;

    @ManyToOne
    @JoinColumn(name = "training_type") 
    private String trainingType;

    private LocalDate trainingDate;
}