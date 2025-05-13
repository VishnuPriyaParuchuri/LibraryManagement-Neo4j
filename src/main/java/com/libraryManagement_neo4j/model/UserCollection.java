package com.libraryManagement_neo4j.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Node("lm_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCollection {

    @Id
    private String id = UUID.randomUUID().toString();

    @Property("USER_NAME")
    private String userName;

    @Property("EMAIL")
    private String email;

    @Property("PASSWORD")
    private String password;

    @Property("ROLE")
    private String role;

    @Property("UUID")
    private String uuid = UUID.randomUUID().toString();

    @Property("PHONE")
    private String phone;

    @Property("DOB")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private String dob;

    @Property("ROLL_NO")
    private String rollNo;

    @Property("COUNTRY")
    private String country;

    @Property("STATE")
    private String state;

    @Property("GENDER")
    private String gender;

    @Property("CREATED_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Property("CREATED_BY")
    private String createdBy;

    @Property("UPDATED_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Property("UPDATED_BY")
    private String updatedBy;

}
