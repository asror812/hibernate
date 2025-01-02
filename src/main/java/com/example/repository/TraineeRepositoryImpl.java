package com.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.dto.ActivationDTO;
import com.example.dto.AuthDTO;
import com.example.dto.TraineeCreateDTO;
import com.example.dto.TraineeUpdateDTO;
import com.example.model.Trainee;
import com.example.model.User;
import com.example.util.PasswordGeneratorUtil;
import com.example.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TraineeRepositoryImpl implements TraineeRepository {

    private final EntityManager entityManager;
    private static final Logger LOGGER = LogManager.getLogger(TraineeRepositoryImpl.class);
    private UserRepositoryImpl userRepositoryImpl;

    public TraineeRepositoryImpl(EntityManager entityManager,
            UserRepositoryImpl userRepositoryImpl) {
        this.entityManager = entityManager;
        this.userRepositoryImpl = userRepositoryImpl;
    }

    @Override
    public Optional<Trainee> create(TraineeCreateDTO createDTO) {
        try {
            entityManager.getTransaction().begin();
            ValidationUtil.validate(createDTO);

            String username = userRepositoryImpl.generateUsername(createDTO);
            String password = PasswordGeneratorUtil.generate();

            User user = new User(null, createDTO.getFirstName(), createDTO.getLastName(), username,
                    password, true);     


            Trainee trainee = new Trainee();
            trainee.setDateOfBirth(createDTO.getDateOfBirth());
            trainee.setAddress(createDTO.getAddress());
            trainee.setUser(user);

            entityManager.persist(trainee);

            entityManager.getTransaction().commit();
            return Optional.of(trainee);
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            LOGGER.error("Failed to create Trainee", e);

            return Optional.empty();
        }
    }

    @Override
    public Optional<Trainee> findByUsername(AuthDTO authDTO, String username) {

        userRepositoryImpl.authenticate(authDTO);

        Optional<User> existingUser = userRepositoryImpl.findByUsername(username);

        if (existingUser.isEmpty()) {
            LOGGER.error("Ustraineeer with username {} not found", username);
            throw new EntityNotFoundException("User with username " + username + " not found");
        }

        try {
            String hql = "FROM Trainee T WHERE T.user.id = :userId";
            TypedQuery<Trainee> query = entityManager.createQuery(hql, Trainee.class);
            query.setParameter("userId", existingUser.get().getId());

            Trainee trainee = query.getSingleResult();
            return Optional.of(trainee);

        } catch (NoResultException e) {
            LOGGER.warn("No Trainee found for username {}", username);
            return Optional.empty();
        }
    }

    @Override
    public void update(AuthDTO authDTO, TraineeUpdateDTO updateDTO) {

        userRepositoryImpl.authenticate(authDTO);

        try {
            entityManager.getTransaction().begin();

            ValidationUtil.validate(updateDTO);
            UUID id = updateDTO.getId();

            Trainee trainee = entityManager.find(Trainee.class, id);
            if (trainee == null) {
                throw new EntityNotFoundException("Trainee with id " + id + " not found");
            }

            User user = trainee.getUser();
            user.setFirstName(updateDTO.getFirstName());
            user.setLastName(updateDTO.getLastName());
            user.setActive(updateDTO.isActive());

            trainee.setAddress(updateDTO.getAddress());
            trainee.setDateOfBirth(updateDTO.getDateOfBirth());

            entityManager.merge(trainee);

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            LOGGER.error("Failed to update trainee", e);
        }
    }

    public List<Trainee> getAll(AuthDTO authDTO) {

        userRepositoryImpl.authenticate(authDTO);

        String hql = "from Trainee ";
        TypedQuery<Trainee> query = entityManager.createQuery(hql, Trainee.class);

        List<Trainee> results = query.getResultList();

        return results;
    }

   

    @Override
    public void activate(AuthDTO authDTO, UUID id) {
        ActivationDTO activationDTO = new ActivationDTO("Trainee", id , true);
        
        userRepositoryImpl.setActivationStatus(authDTO, activationDTO);
    }

    @Override
    public void deactivate(AuthDTO authDTO, UUID id) {
        ActivationDTO activationDTO = new ActivationDTO("Trainee", id, false);
        userRepositoryImpl.setActivationStatus(authDTO, activationDTO);
    }

    @Override
    public void delete(AuthDTO authDTO, String username) {

        userRepositoryImpl.authenticate(authDTO);

        try {
            entityManager.getTransaction().begin();
            Optional<Trainee> existingTrainee = findByUsername(authDTO, username);;

            if (existingTrainee.isEmpty()) {
                LOGGER.error("Trainee with username {} found", username);
                throw new EntityNotFoundException("Trainee with username " + username + " not found");
            }

            Trainee trainee = existingTrainee.get();
            entityManager.remove(trainee);

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            LOGGER.error("Failed to delete trainee", e);
        }
    }

}