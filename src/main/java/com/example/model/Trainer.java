package com.example.model;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter@Setter
public class Trainer {
   
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UUID userId;

    private String specialization;
    
}