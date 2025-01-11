package com.example.repository;

import java.util.List;
import java.util.Optional;

import com.example.dto.AuthDTO;
import com.example.dto.TraineeCriteriaDTO;
import com.example.dto.TrainerCriteriaDTO;
import com.example.dto.TrainingCreateDTO;
import com.example.model.Trainer;
import com.example.model.Training;

public interface TrainingRepository {

   Optional<Training> create(AuthDTO authDTO, TrainingCreateDTO createDTO);

   List<Training> getTraineeTrainings(AuthDTO authDTO, TraineeCriteriaDTO criteriaDTO);

   List<Training> getTrainerTrainings(AuthDTO authDTO, TrainerCriteriaDTO criteriaDTO);

   List<Trainer> getNotAssignedTrainers(AuthDTO authDTO, String traineeUsername);

}