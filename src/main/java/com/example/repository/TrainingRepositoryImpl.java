package com.example.repository;

import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.dto.AuthDTO;
import com.example.dto.TraineeCriteriaDTO;
import com.example.dto.TrainerCriteriaDTO;
import com.example.dto.TrainingCreateDTO;
import com.example.model.Trainee;
import com.example.model.Trainer;
import com.example.model.Training;
import com.example.model.TrainingType;
import com.example.model.User;
import com.example.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class TrainingRepositoryImpl implements TrainingRepository {

   private final static Logger LOGGER = LogManager.getLogger(TraineeRepositoryImpl.class);
   private TraineeRepositoryImpl traineeRepositoryImpl;
   private TrainerRepositoryImpl trainerRepositoryImpl;
   private UserRepositoryImpl userRepositoryImpl;
   private TrainingTypeRepositoryImpl trainingTypeRepositoryImpl;

   private EntityManager entityManager;

   public TrainingRepositoryImpl(EntityManager entityManager,
         TraineeRepositoryImpl traineeRepositoryImpl, TrainerRepositoryImpl trainerRepositoryImpl,
         UserRepositoryImpl userRepositoryImpl,
         TrainingTypeRepositoryImpl trainingTypeRepositoryImpl) {
      this.entityManager = entityManager;
      this.traineeRepositoryImpl = traineeRepositoryImpl;
      this.trainerRepositoryImpl = trainerRepositoryImpl;
      this.trainingTypeRepositoryImpl = trainingTypeRepositoryImpl;
      this.userRepositoryImpl = userRepositoryImpl;
   }

   public Optional<Training> create(AuthDTO authDTO, TrainingCreateDTO createDTO) {

      userRepositoryImpl.authenticate(authDTO);

      try {
         entityManager.getTransaction().begin();
         ValidationUtil.validate(createDTO);
         UUID traineeId = createDTO.getTraineeId();
         UUID trainerId = createDTO.getTrainerId();

         Trainee trainee = entityManager.find(Trainee.class, traineeId);
         Trainer trainer = entityManager.find(Trainer.class, trainerId);

         if (trainee == null || trainer == null) {
            throw new EntityNotFoundException("Trainee or trainer not found");
         }

         trainee.getTrainers().add(trainer);
         trainer.getTrainees().add(trainee);

         entityManager.merge(trainee);
         entityManager.merge(trainer);

         Optional<TrainingType> byName = trainingTypeRepositoryImpl
               .findByName(createDTO.getTrainingTypeName());

         if (byName.isEmpty()) {
            LOGGER.warn("Not existing training type name: {} ",
                  createDTO.getTrainingTypeName());

            throw new IllegalArgumentException(
                  "Not existing training type name " + createDTO.getTrainingTypeName());
         }

         Training training = new Training();
         training.setTrainee(trainee);
         training.setTrainer(trainer);
         training.setTrainingDate(createDTO.getTrainingDate());
         training.setDuration(createDTO.getDuration());
         training.setTrainingName(createDTO.getTrainingTypeName());
         training.setTrainingType(byName.get());

         entityManager.persist(training);

         entityManager.getTransaction().commit();

         return Optional.of(training);
      } catch (Exception e) {

         if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
         }

         LOGGER.error("Failed to create training", e);

         return Optional.empty();
      }
   }

   public List<Training> getTraineeTrainings(AuthDTO authDTO, TraineeCriteriaDTO criteriaDTO) {

      userRepositoryImpl.authenticate(authDTO);

      LocalDate from = criteriaDTO.getFrom();
      LocalDate to = criteriaDTO.getTo();

      String username = criteriaDTO.getUsername();
      String trainerName = criteriaDTO.getTrainerName();
      String trainingType = criteriaDTO.getTrainingType();

      try {
         CriteriaBuilder cb = entityManager.getCriteriaBuilder();
         CriteriaQuery<Training> query = cb.createQuery(Training.class);
         Root<Training> training = query.from(Training.class);

         // Joins
         Join<Training, Trainee> trainee = training.join("trainee");
         Join<Trainee, User> traineeUser = trainee.join("user");
         Join<Training, Trainer> trainer = training.join("trainer");
         Join<Trainer, User> trainerUser = trainer.join("user");
         Join<Training, TrainingType> type = training.join("trainingType");

         // Predicates (Filters)
         List<Predicate> predicates = new ArrayList<>();

         // Mandatory Filter: Trainee Username
         predicates.add(cb.equal(traineeUser.get("username"), username));

         // Optional Filters
         if (from != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), from));
         }

         if (to != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), to));
         }

         if (trainerName != null && !trainerName.trim().isEmpty()) {
            predicates.add(cb.equal(trainerUser.get("username"), trainerName));
         }

         if (trainingType != null && !trainingType.trim().isEmpty()) {
            predicates.add(cb.equal(type.get("trainingTypeName"), trainingType));
         }

         query.where(predicates.toArray(new Predicate[0]));

         return entityManager.createQuery(query).getResultList();
      } catch (Exception e) {
         LOGGER.error(e);
         return Collections.emptyList();
      }
   }

   @Override
   public List<Training> getTrainerTrainings(AuthDTO authDTO, TrainerCriteriaDTO criteriaDTO) {

      userRepositoryImpl.authenticate(authDTO);

      LocalDate from = criteriaDTO.getFrom();
      LocalDate to = criteriaDTO.getTo();

      String username = criteriaDTO.getUsername();
      String traineeName = criteriaDTO.getTraineeName();
      String trainingType = criteriaDTO.getTrainingType();

      try {
         CriteriaBuilder cb = entityManager.getCriteriaBuilder();
         CriteriaQuery<Training> query = cb.createQuery(Training.class);
         Root<Training> training = query.from(Training.class);

         // Joins
         Join<Training, Trainee> trainee = training.join("trainee");
         Join<Trainee, User> traineeUser = trainee.join("user");
         Join<Training, Trainer> trainer = training.join("trainer");
         Join<Trainer, User> trainerUser = trainer.join("user");
         Join<Training, TrainingType> type = training.join("trainingType");

         // Predicates
         List<Predicate> predicates = new ArrayList<>();

         // Mandatory Filter: Trainer Username
         predicates.add(cb.equal(trainerUser.get("username"), username));

         if (from != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), from));
         }

         if (to != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), to));
         }

         if (traineeName != null && !traineeName.trim().isEmpty()) {
            predicates.add(cb.equal(traineeUser.get("username"), traineeName));
         }

         if (trainingType != null && !trainingType.trim().isEmpty()) {
            predicates.add(cb.equal(type.get("trainingTypeName"), trainingType));
         }

         query.where(predicates.toArray(new Predicate[0]));

         return entityManager.createQuery(query).getResultList();
      } catch (Exception e) {

         e.printStackTrace();
         return Collections.emptyList();
      }
   }

   public List<Trainer> getNotAssignedTrainers(AuthDTO authDTO, String traineeUsername) {
      userRepositoryImpl.authenticate(authDTO);

      Optional<Trainee> existingTrainee = traineeRepositoryImpl.findByUsername(authDTO,
            traineeUsername);

      if (existingTrainee.isEmpty()) {
         LOGGER.error("Trainee with username {} not found", traineeUsername);
         throw new EntityNotFoundException(
               "Trainee with username " + traineeUsername + " not found");
      }

      Trainee trainee = existingTrainee.get();
      List<Trainer> trainers = trainee.getTrainers();

      List<Trainer> all = trainerRepositoryImpl.getAll(authDTO);

      all.removeAll(trainers);

      return all;
   }

   public List<Training> getAll(AuthDTO authDTO) {
      userRepositoryImpl.authenticate(authDTO);

      String hql = "from Training ";
      TypedQuery<Training> query = entityManager.createQuery(hql, Training.class);

      List<Training> results = query.getResultList();

      return results;
   }

}
