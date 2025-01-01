package com.example;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.example.dto.AuthDTO;
import com.example.dto.ChangePasswordDTO;
import com.example.dto.TraineeCreateDTO;
import com.example.dto.TrainerCreateDTO;
import com.example.dto.TrainerCriteriaDTO;
import com.example.dto.TrainingCreateDTO;
import com.example.model.Trainee;
import com.example.model.Trainer;
import com.example.model.Training;
import com.example.model.User;
import com.example.repository.TraineeRepositoryImpl;
import com.example.repository.TrainerRepositoryImpl;
import com.example.repository.TrainingRepositoryImpl;
import com.example.repository.TrainingTypeRepositoryImpl;
import com.example.repository.UserRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class App {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {

        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
                EntityManager em = emf.createEntityManager()) {

            UserRepositoryImpl userRepository = new UserRepositoryImpl(em);

            TraineeRepositoryImpl traineeRepositoryImpl = new TraineeRepositoryImpl(em,
                    userRepository);

            TrainingTypeRepositoryImpl trainingTypeRepositoryImpl = new TrainingTypeRepositoryImpl(em);        

            // Create
            LOGGER.info("CREATE TRAINEE");
            TraineeCreateDTO createDTO = new TraineeCreateDTO("A", "B", LocalDate.now(), "T");
            Optional<Trainee> optional = traineeRepositoryImpl.create(createDTO);;

            LOGGER.info("{}  created", optional.get());

            // Get all
            LOGGER.info("GET ALL");

            User user = optional.get().getUser();

            AuthDTO authDTO = new AuthDTO();
            authDTO.setPassword(user.getPassword());
            authDTO.setUsername(user.getUsername());

            List<Trainee> all = traineeRepositoryImpl.getAll(authDTO);
            for (Trainee trainee : all) {
                LOGGER.info("{} ", trainee);
            }

            // Find by username
            LOGGER.info("FIND BY USERNAME");
            Optional<Trainee> existingTrainee = traineeRepositoryImpl.findByUsername(authDTO,
                    "A.B");

            if (existingTrainee.isPresent()) {
                Trainee trainee = existingTrainee.get();

                UUID id = trainee.getId();
                LOGGER.info("FIND BY USERNAME : ");
                LOGGER.info("{} ", trainee);

                // Password change
                LOGGER.info("Previous password : {} ", trainee.getUser().getPassword());
                userRepository.changePassword(authDTO,
                        new ChangePasswordDTO(id, "1234567890", "Trainee"));

                LOGGER.info("Current password : {} ", trainee.getUser().getPassword());

                // deactivate user
                authDTO.setPassword("1234567890");
                LOGGER.info("Current state : {} ", trainee.getUser().isActive());
                traineeRepositoryImpl.deactivate(authDTO, id);
                LOGGER.info("Current state: {} ", trainee.getUser().isActive());

                // activate user
                traineeRepositoryImpl.activate(authDTO, id);
                LOGGER.info("Future state  : {} ", trainee.getUser().isActive());

                /*
                 * // delete LOGGER.info("DELETE TRAINEE"); traineeRepositoryImpl.delete("A.B");
                 * Optional<Trainee> deletedTrainee =
                 * traineeRepositoryImpl.findByUsername("A.B");
                 * LOGGER.info("Deleted trainee : {} ", deletedTrainee);
                 */
            }

            TrainerRepositoryImpl trainerRepositoryImpl = new TrainerRepositoryImpl(em,
                    userRepository);

            // Create
            LOGGER.info("CREATE TRAINER");
            TrainerCreateDTO createDTO1 = new TrainerCreateDTO("B", "B", "T");
            Optional<Trainer> optional1 = trainerRepositoryImpl.create(createDTO1);;

            LOGGER.info("{}  created", optional1.get());

            // Get all
            System.out.println("GET ALL");
            List<Trainer> allTrainers = trainerRepositoryImpl.getAll(authDTO);
            for (Trainer trainer : allTrainers) {
                LOGGER.info("{} ", trainer);;
            }

            // Find by username
            LOGGER.info("FIND BY USERNAME");
            Optional<Trainer> existingTrainer = trainerRepositoryImpl.findByUsername(authDTO,
                    "B.B");

            if (existingTrainer.isPresent()) {
                Trainer trainer = existingTrainer.get();

                UUID id = trainer.getId();
                LOGGER.info("FIND BY USERNAME : ");
                LOGGER.info("{} ", trainer);

                // Password change
                LOGGER.info("CHANGE PASSWORD");
                LOGGER.info("Previous password : {} ", trainer.getUser().getPassword());
                trainerRepositoryImpl.changePassword(authDTO,
                        new ChangePasswordDTO(id, "1234567890", "Trainee"));

                LOGGER.info("Current password : {} ", trainer.getUser().getPassword());

                // deactivate use
                LOGGER.info("DEACTIVATE USER");

                LOGGER.info("CURRENT STATE : {} ", trainer.getUser().isActive());
                trainerRepositoryImpl.deactivate(authDTO, id);
                LOGGER.info("Current state: {} ", trainer.getUser().isActive());

                // activate user
                LOGGER.info("ACTIVATE USER");
                trainerRepositoryImpl.activate(authDTO, id);
                LOGGER.info("FUTURE STATE  : {} ", trainer.getUser().isActive());

            }

            if (existingTrainee.isPresent() && existingTrainer.isPresent()) {

                LOGGER.info("CREATE TRAINING");
                Trainee trainee = existingTrainee.get();
                Trainer trainer = existingTrainer.get();

                TrainingRepositoryImpl trainingRepositoryImpl = new TrainingRepositoryImpl(em,
                        traineeRepositoryImpl, trainerRepositoryImpl, userRepository, trainingTypeRepositoryImpl);

                TrainingCreateDTO trainingCreateDTO = new TrainingCreateDTO();

                trainingCreateDTO.setDuration(1.5);
                trainingCreateDTO.setTraineeId(trainee.getId());
                trainingCreateDTO.setTrainerId(trainer.getId());
                trainingCreateDTO.setTrainingDate(LocalDate.now());
                trainingCreateDTO.setTrainingTypeName("T");

                trainingRepositoryImpl.create(authDTO, trainingCreateDTO);

                // Find all
                List<Training> trainings = trainingRepositoryImpl.getAll(authDTO);

                LOGGER.info("FIND ALL");
                for (Training t : trainings) {
                    LOGGER.info("{} ", t);
                }

                // Not Assigned trainers
                LOGGER.info("NOT ASSIGNED TRAINERS ");
                List<Trainer> notAssignedTrainers = trainingRepositoryImpl
                        .getNotAssignedTrainers(authDTO, "A.B");

                for (Trainer notAssignedTrainee : notAssignedTrainers) {
                    LOGGER.info("{} ", notAssignedTrainee);
                }

                // Trainee trainings
                LOGGER.info("Trainee trainings ");
                TrainerCriteriaDTO trainerCriteriaDTO = new TrainerCriteriaDTO();
                trainerCriteriaDTO.setFrom(LocalDate.now().minusDays(10));
                trainerCriteriaDTO.setTo(LocalDate.now().plusDays(10));
                trainerCriteriaDTO.setTraineeName("A.B");
                trainerCriteriaDTO.setTrainingType(trainer.getSpecialization().getTrainingTypeName());

                List<Training> trainerTrainings = trainingRepositoryImpl
                        .getTrainerTrainings(authDTO, trainerCriteriaDTO);

                for (Training t : trainerTrainings) {
                    LOGGER.info("{} ", t);
                }

            }
        }

    }
}
