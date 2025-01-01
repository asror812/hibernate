package com.example.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.dto.AuthDTO;
import com.example.dto.ChangePasswordDTO;
import com.example.dto.TrainerCreateDTO;
import com.example.dto.TrainerUpdateDTO;
import com.example.model.Trainer;

public interface TrainerRepository {
    Optional<Trainer> create(TrainerCreateDTO createDTO);

    void update(AuthDTO authDTO, TrainerUpdateDTO updateDTO);

    void activate(AuthDTO authDTO, UUID id);

    void deactivate(AuthDTO authDTO, UUID id);

    Optional<Trainer> findByUsername(AuthDTO authDTO, String username);

    void changePassword(AuthDTO authDTO, ChangePasswordDTO ChangePasswordDTO);
}