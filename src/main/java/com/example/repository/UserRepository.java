package com.example.repository;

import java.util.Optional;
import com.example.dto.ActivationDTO;
import com.example.dto.AuthDTO;
import com.example.dto.ChangePasswordDTO;
import com.example.dto.UserAuthDTO;
import com.example.dto.UserCreateDTO;
import com.example.model.User;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(UserAuthDTO authDTO);

    void authenticate(AuthDTO authDTO);

    void setActivationStatus(AuthDTO authDTO, ActivationDTO activationDTO);


    void changePassword(AuthDTO authDTO, ChangePasswordDTO changePasswordDTO);

}
