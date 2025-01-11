package com.example.repository;

import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.dto.ActivationDTO;
import com.example.dto.AuthDTO;
import com.example.dto.ChangePasswordDTO;
import com.example.dto.UserAuthDTO;
import com.example.dto.UserCreateDTO;
import com.example.model.Trainee;
import com.example.model.Trainer;
import com.example.model.User;
import com.example.util.ValidationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserRepositoryImpl implements UserRepository {

    EntityManager entityManager;
    private static long serialNumber = 0;

    private static final Logger LOGGER = LogManager.getLogger();

    public UserRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);

            Root<User> root = cq.from(User.class);
            cq.select(root).where(cb.equal(root.get("username"), username));

            TypedQuery<User> query = entityManager.createQuery(cq);
            User result = query.getSingleResult();

            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByUsernameAndPassword(UserAuthDTO authDTO) {

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> root = cq.from(User.class);

            Predicate predicateForUsername = cb.equal(root.get("username"), authDTO.getUsername());
            Predicate predicateForPassword = cb.equal(root.get("password"), authDTO.getPassword());

            Predicate finalPredicate = cb.and(predicateForUsername, predicateForPassword);

            cq.where(finalPredicate);

            User user = entityManager.createQuery(cq).getSingleResult();

            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void authenticate(AuthDTO authDTO) {

        String password = authDTO.getPassword();
        String username = authDTO.getUsername();

        Optional<User> existingUser = findByUsername(username);

        if (existingUser.isEmpty()) {
            LOGGER.warn("User with username {} found", username);
            throw new EntityNotFoundException("User with username " + username + " not found");
        }

        User user = existingUser.get();

        if (!(user.getUsername().equals(username) && user.getPassword().equals(password))) {
            LOGGER.warn("Username and password mismatch");
            throw new SecurityException("Username and password mismatch");
        }

    }

    public String generateUsername(UserCreateDTO createDTO) {
        String firstName = createDTO.getFirstName();
        String lastName = createDTO.getLastName();
        String username = firstName + "." + lastName;
        Optional<User> existingUser = findByUsername(username);

        if (existingUser.isPresent()) {
            username = username + serialNumber;
        }

        return username;
    }

    public void setActivationStatus(AuthDTO authDTO, ActivationDTO activationDTO) {

        authenticate(authDTO);

        try {
            entityManager.getTransaction().begin();

            ValidationUtil.validate(activationDTO);

            Object entity;
            
            String entityType = activationDTO.getEntityType();
            UUID id = activationDTO.getId();
            boolean status = activationDTO.isStatus();

            if ("Trainer".equalsIgnoreCase(entityType)) {
                entity = entityManager.find(Trainer.class, id);
            }
            else if ("Trainee".equalsIgnoreCase(entityType)) {
                entity = entityManager.find(Trainee.class, id);
            }
            else {
                throw new IllegalArgumentException("Invalid entity type: " + entityType);
            }

            if (entity == null) {
                LOGGER.warn(entityType + " with id {} not found", id);
                throw new EntityNotFoundException(entityType + " with id " + id + " not found");
            }

            User user = entity instanceof Trainer ? ((Trainer) entity).getUser()
                    : ((Trainee) entity).getUser();

            if (user == null) {
                throw new IllegalStateException(
                        "User associated with " + entityType + " id " + id + " is null");
            }

            if (user.isActive() == status) {
                throw new RuntimeException(
                        "User is already " + (status ? "activated" : "deactivated"));
            }

            user.setActive(status);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            LOGGER.error("Failed to update activation status for " + activationDTO.getEntityType(), e);
        }
    }

     @Override
    public void changePassword(AuthDTO authDTO, ChangePasswordDTO changePasswordDTO) {

        authenticate(authDTO);

        try {
            entityManager.getTransaction().begin();
            ValidationUtil.validate(changePasswordDTO);

            Object entity;
            UUID id = changePasswordDTO.getId();

            String entityType = changePasswordDTO.getEntityType();
            
            if ("Trainer".equalsIgnoreCase(entityType)) {
                entity = entityManager.find(Trainer.class, id);
            }
            else if ("Trainee".equalsIgnoreCase(entityType)) {
                entity = entityManager.find(Trainee.class, id);
            }
            else {
                throw new IllegalArgumentException("Invalid entity type: " + entityType);
            }

            if (entity == null) {
                LOGGER.warn(entityType + " with id {} not found", id);
                throw new EntityNotFoundException(entityType + " with id " + id + " not found");
            }

            User user = entity instanceof Trainer ? ((Trainer) entity).getUser()
                    : ((Trainee) entity).getUser();

            if (user == null) {
                throw new IllegalStateException(
                        "User associated with " + entityType + " id " + id + " is null");
            }

            user.setPassword(changePasswordDTO.getNewPassword());
            entityManager.persist(user);

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            LOGGER.error("Failed to change password of " + changePasswordDTO.getEntityType(), e);
        }
    }
}
