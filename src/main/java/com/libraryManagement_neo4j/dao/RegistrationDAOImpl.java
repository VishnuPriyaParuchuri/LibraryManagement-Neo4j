package com.libraryManagement_neo4j.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.libraryManagement_neo4j.model.UserCollection;
import com.libraryManagement_neo4j.repository.UserRepository;

@Service
public class RegistrationDAOImpl implements RegistrationDAO {

    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<UserCollection> isUserExists(String email) {

        return userRepository.findByEmail(email);

    }

    @Override
    public UserCollection createUser(UserCollection userDetails) {

        return userRepository.save(userDetails);
    }
}
