package com.libraryManagement_neo4j.dao;

import java.util.Optional;

import com.libraryManagement_neo4j.model.UserCollection;

public interface RegistrationDAO {

    Optional<UserCollection> isUserExists(String username);

    UserCollection createUser(UserCollection userDetails);

}
