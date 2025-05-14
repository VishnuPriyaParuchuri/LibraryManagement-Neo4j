package com.libraryManagement_neo4j.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.libraryManagement_neo4j.config.CustomResponse;
import com.libraryManagement_neo4j.dao.RegistrationDAO;
import com.libraryManagement_neo4j.dto.UserInfoDTO;
import com.libraryManagement_neo4j.dto.UserServiceDTO;
import com.libraryManagement_neo4j.model.UserCollection;
import com.libraryManagement_neo4j.utill.JwtTokenProvider;
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

    @Autowired
    JwtTokenProvider jwtTokenProvider;

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

    @Override
    public ResponseEntity<?> authenticateUser(UserCollection userCollection, HttpServletRequest req,
            HttpServletResponse res) {
        try {
            if (userCollection.getEmail().isBlank() || userCollection.getEmail().isEmpty()) {
                String errorMessages = "Email is required!";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                // If the user exists, return a message with a bad status
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            if (userCollection.getPassword().isBlank() || userCollection.getPassword().isEmpty()) {
                String errorMessages = "Password is required!";

                CustomResponse<String> responseBody = new CustomResponse<>(errorMessages, "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST.value(), req.getRequestURI(), LocalDateTime.now());

                // If the user exists, return a message with a bad status
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            UserDetails userDetails = userInfo.loadUserByUsername(userCollection.getEmail());

            System.out.println("userDetails" + " " + userDetails);

            // Load the user by email
            // Manually authenticate using the AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userCollection.getEmail(), userCollection.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Optionally, generate a JWT token for the user (or manage sessions)
            String token = jwtTokenProvider.generateToken(authentication);

            System.out.println("token" + " " + token);

            System.out.println("authentication" + " " + authentication);

            // Prepare user details for the response
            UserInfoDTO user = (UserInfoDTO) authentication.getPrincipal();
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("id", user.getId()); // Assuming you have a getId() method
            response.put("username", user.getUserName());
            response.put("email", user.getEmail());
            response.put("roles",
                    user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

            CustomResponse<?> responseBody = new CustomResponse<>(response, "SUCCESS", HttpStatus.OK.value(),
                    req.getRequestURI(), LocalDateTime.now());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (UsernameNotFoundException ex) {
            // Handle case when user is not found
            CustomResponse<String> responseBody = new CustomResponse<>("User details not found!", "BAD_CREDENTIALS",
                    HttpStatus.UNAUTHORIZED.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
        } catch (BadCredentialsException ex) {
            // Handle case when credentials are invalid
            CustomResponse<String> responseBody = new CustomResponse<>("Incorrect email or password", "BAD_CREDENTIALS",
                    HttpStatus.UNAUTHORIZED.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            // Handle any other exceptions
            CustomResponse<String> responseBody = new CustomResponse<>(ex.getMessage(), "ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), req.getRequestURI(), LocalDateTime.now());
            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
