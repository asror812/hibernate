package com.example.repository;

import java.util.Optional;
import com.example.dto.TrainingTypeCreateDTO;
import com.example.model.TrainingType;

public interface TrainingTypeRepository {
 
   Optional<TrainingType> create(TrainingTypeCreateDTO createDTO);

   Optional<TrainingType> findByName(String trainingTypeName);


}