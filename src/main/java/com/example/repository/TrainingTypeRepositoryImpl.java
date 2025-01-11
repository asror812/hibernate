package com.example.repository;

import java.util.Optional;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.example.dto.TrainingTypeCreateDTO;
import com.example.model.TrainingType;
import com.example.util.ValidationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {

   private EntityManager entityManager;
   private static final Logger LOGGER = LogManager.getLogger(TrainingTypeRepositoryImpl.class);

   public TrainingTypeRepositoryImpl(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   @Override
   public Optional<TrainingType> create(TrainingTypeCreateDTO createDTO) {
      try {
         entityManager.getTransaction().begin();
         ValidationUtil.validate(createDTO);

         TrainingType trainingType = new TrainingType(createDTO.getTrainingTypeName());

         entityManager.persist(trainingType);
         entityManager.getTransaction().commit();

         return Optional.of(trainingType);
      } catch (Exception e) {
         if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
         }

         LOGGER.error("Failed to create TrainingType", e);

         return Optional.empty();
      }
   }

   @Override
   public Optional<TrainingType> findByName(String trainingTypeName) {
      // Query to find the TrainingType by its name
      String hql = "from TrainingType where trainingTypeName = :name";
      TypedQuery<TrainingType> query = entityManager.createQuery(hql, TrainingType.class);
      query.setParameter("name", trainingTypeName);

      try {
         TrainingType result = query.getSingleResult();
         return Optional.of(result); 
      } catch (NoResultException e) {
         return Optional.empty(); 
      }
   }

}