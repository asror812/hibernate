package com.example.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ActivationDTO {

   private String entityType;
   private UUID id;
   private boolean status;
}
