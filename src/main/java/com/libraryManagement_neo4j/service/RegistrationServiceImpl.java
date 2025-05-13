package com.libraryManagement_neo4j.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.libraryManagement_neo4j.config.CustomResponse;
import com.libraryManagement_neo4j.dao.RegistrationDAO;
import com.libraryManagement_neo4j.dto.UserServiceDTO;
import com.libraryManagement_neo4j.model.UserCollection;
import com.libraryManagement_neo4j.utill.UserInfo;
import com.libraryManagement_neo4j.utill.Utills;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    RegistrationDAO registrationDAO;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    Utills utill;

    // @Autowired
    // HtmlTemplate htmlTemplate;

    @Autowired
    UserInfo userInfo;

    // @Autowired
    // JwtTokenProvider jwtTokenProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<?> createUserInfo(@Valid UserServiceDTO userServiceDTO, HttpServletRequest req,
            HttpServletResponse res) {
        try {
            Optional<UserCollection> existingUser = registrationDAO.isUserExists(userServiceDTO.getEmail());

            if (existingUser.isPresent()) {

                String errorMessages = "User email already exists. Try with a different email.";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                // If the user exists, return a message with a bad status
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            String userName = userServiceDTO.getUserName() != null ? userServiceDTO.getUserName() : null;

            String email = userServiceDTO.getEmail() != null ? userServiceDTO.getEmail() : null;

            String role;

            String password = userServiceDTO.getPassword() != null
                    ? passwordEncoder.encode(userServiceDTO.getPassword())
                    : null;

            String uuid = utill.generateString(36);

            LocalDateTime createdAt = LocalDateTime.now();

            String createdBy = uuid;
            SecureRandom secureRandom = new SecureRandom();
            String sixDigitNumber = String.valueOf(100000 + secureRandom.nextInt(900000)); // Generates a number between
                                                                                           // 100000 and 999999
            System.out.println("6-Digit Secure Random Number: " + sixDigitNumber);

            UserCollection userDetails = new UserCollection();

            userDetails.setUserName(userName);
            userDetails.setEmail(email);
            userDetails.setPassword(password);

            if (userServiceDTO.getIsAdmin() != null) {
                role = "ROLE_ADMIN,";
            } else {
                role = "ROLE_STUDENT,";
                userDetails.setRollNo(sixDigitNumber);
            }

            userDetails.setRole(role);
            userDetails.setUuid(uuid);

            userDetails.setCreatedAt(createdAt);
            userDetails.setCreatedBy(createdBy);

            UserCollection userInfo = registrationDAO.createUser(userDetails);

            if (userInfo.getId() == null) {

                String errorMessages = "User not created. Please try again!";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                // If the user exists, return a message with a bad status
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            // userEmailProducer.sendMessage(userData);
            CustomResponse<UserCollection> responseBody = new CustomResponse<>(userInfo, "CREATED",
                    HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (Exception e) {
            String stackTrace = utill.getStackTraceAsString(e);

            CustomResponse<String> responseBody = new CustomResponse<>(e.getMessage(), "BAD_REQUEST",
                    HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }
}
