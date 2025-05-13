package com.libraryManagement_neo4j.service;

import org.springframework.http.ResponseEntity;

import com.libraryManagement_neo4j.dto.UserServiceDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface RegistrationService {

    ResponseEntity<?> createUserInfo(UserServiceDTO userServiceDTO, HttpServletRequest req,
            HttpServletResponse res);

}
