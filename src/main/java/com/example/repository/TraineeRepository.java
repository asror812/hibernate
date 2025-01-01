package com.example.repository;

import java.util.Optional;
import java.util.UUID;
import com.example.dto.AuthDTO;
import com.example.dto.TraineeCreateDTO;
import com.example.dto.TraineeUpdateDTO;
import com.example.model.Trainee;

public interface TraineeRepository {
    Optional<Trainee> create(TraineeCreateDTO createDTO);

    void update(AuthDTO authDTO, TraineeUpdateDTO updateDTO);

    void activate(AuthDTO authDTO, UUID id);

    void deactivate(AuthDTO authDTO, UUID id);

    void delete(AuthDTO authDTO, String targetUsername);

    Optional<Trainee> findByUsername(AuthDTO authDTO, String username);


}