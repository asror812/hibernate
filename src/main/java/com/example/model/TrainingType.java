package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "training_type")
@ToString
public class TrainingType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "training_type_name", nullable = false)
    private String trainingTypeName;

    @OneToMany(mappedBy = "specialization")
    public List<Trainer> trainers = new ArrayList<>();

    @OneToMany(mappedBy = "trainingType", cascade = {CascadeType.PERSIST,
            CascadeType.MERGE}, orphanRemoval = true)
    public List<Training> trainings = new ArrayList<>();

    public TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}