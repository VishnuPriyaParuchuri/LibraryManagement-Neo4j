package com.libraryManagement_neo4j.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import com.libraryManagement_neo4j.model.UserCollection;

@Repository
public interface UserRepository extends Neo4jRepository<UserCollection, String> {

    Optional<UserCollection> findByEmail(String email);

}
