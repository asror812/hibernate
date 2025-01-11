package com.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.example.util.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import com.example.model.User;
import com.example.dto.ActivationDTO;
import com.example.dto.AuthDTO;
import com.example.dto.ChangePasswordDTO;
import com.example.dto.TrainerCreateDTO;
import com.example.dto.TrainerUpdateDTO;
import com.example.model.Trainer;
import com.example.model.TrainingType;

public class TrainerRepositoryImpl implements TrainerRepository {

   private static final Logger LOGGER = LogManager.getLogger(TrainerRepositoryImpl.class);
   protected EntityManager entityManager;
   private UserRepositoryImpl userRepositoryImpl;

   public TrainerRepositoryImpl(EntityManager entityManager,
         UserRepositoryImpl userRepositoryImpl) {
      this.entityManager = entityManager;
      this.userRepositoryImpl = userRepositoryImpl;
   }

   @Override
   public Optional<Trainer> create(TrainerCreateDTO createDTO) {

      try {
         entityManager.getTransaction().begin();
         ValidationUtil.validate(createDTO);

         String username = userRepositoryImpl.generateUsername(createDTO);
         String password = PasswordGeneratorUtil.generate();

         User user = new User(null, createDTO.getFirstName(), createDTO.getLastName(), username,
               password, true);

         TrainingType trainingType = new TrainingType(createDTO.getTrainingTypeName());
         entityManager.persist(trainingType);

         Trainer trainer = new Trainer();
         trainer.setSpecialization(trainingType);
         trainer.setUser(user);

         entityManager.persist(trainer);

         entityManager.getTransaction().commit();
         return Optional.of(trainer);

      } catch (Exception e) {
         if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
         }

         LOGGER.error("Failed to create Trainer", e);

         return Optional.empty();
      }
   }

   public List<Trainer> getAll(AuthDTO authDTO) {
      userRepositoryImpl.authenticate(authDTO);

      String hql = "from Trainer ";
      TypedQuery<Trainer> query = entityManager.createQuery(hql, Trainer.class);

      List<Trainer> results = query.getResultList();

      return results;
   }

   @Override
   public Optional<Trainer> findByUsername(AuthDTO authDTO, String username) {

      userRepositoryImpl.authenticate(authDTO);

      Optional<User> existingUser = userRepositoryImpl.findByUsername(username);

      if (existingUser.isEmpty()) {
         LOGGER.error("User with username {} found", username);
         throw new EntityNotFoundException("User with username " + username + " not found");
      }

      try {
         String hql = "FROM Trainer T WHERE T.user.id = :userId";
         TypedQuery<Trainer> query = entityManager.createQuery(hql, Trainer.class);
         query.setParameter("userId", existingUser.get().getId());

         Trainer trainer = query.getSingleResult();

         return Optional.of(trainer);

      } catch (NoResultException e) {
         LOGGER.warn("No Trainer found for username {}", username);
         return Optional.empty();
      }
   }

   @Override
   public void changePassword(AuthDTO authDTO, ChangePasswordDTO changePasswordDTO) {

      userRepositoryImpl.authenticate(authDTO);

      try {
         entityManager.getTransaction().begin();
         ValidationUtil.validate(changePasswordDTO);

         UUID id = changePasswordDTO.getId();

         Trainer trainer = entityManager.find(Trainer.class, id);

         if (trainer == null) {
            LOGGER.warn("Trainer with id {} not found", id);
            throw new EntityNotFoundException("Trainer with id " + id + " not found");
         }

         User user = trainer.getUser();

         user.setPassword(changePasswordDTO.getNewPassword());
         entityManager.persist(user);

         entityManager.getTransaction().commit();
      } catch (Exception e) {
         if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
         }

         LOGGER.error("Failed to change password of trainer", e);
      }
   }

   @Override
   public void update(AuthDTO authDTO, TrainerUpdateDTO updateDTO) {
      userRepositoryImpl.authenticate(authDTO);

      try {
         entityManager.getTransaction().begin();
         ValidationUtil.validate(updateDTO);
         UUID id = updateDTO.getId();

         Trainer trainer = entityManager.find(Trainer.class, id);
         if (trainer == null) {
            throw new EntityNotFoundException("Trainer with id " + id + " not found");
         }

         User user = trainer.getUser();

         user.setFirstName(updateDTO.getFirstName());
         user.setLastName(updateDTO.getLastName());

         TrainingType trainingType = new TrainingType(updateDTO.getTrainingTypeName());
         entityManager.merge(trainingType);

         trainer.setSpecialization(trainingType);

         entityManager.merge(trainer);
         entityManager.getTransaction().commit();

      } catch (Exception e) {

         if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
         }

         LOGGER.error("Failed to update trainer", e);
      }
   }

   @Override
   public void activate(AuthDTO authDTO, UUID id) {
      ActivationDTO activationDTO = new ActivationDTO("Trainer", id, true);
      userRepositoryImpl.setActivationStatus(authDTO, activationDTO);
   }

   @Override
   public void deactivate(AuthDTO authDTO, UUID id) {
      ActivationDTO activationDTO = new ActivationDTO("Trainer", id, false);
      userRepositoryImpl.setActivationStatus(authDTO, activationDTO);
   }

}