package com.libraryManagement.neo4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication(scanBasePackages = "com.libraryManagement.neo4j")
@ComponentScan(basePackages = "com.libraryManagement_neo4j")
@EnableNeo4jRepositories(basePackages = "com.libraryManagement_neo4j.repository")
public class LibraryManagementNeo4jApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementNeo4jApplication.class, args);

		System.out.println("Application started!");
	}

}
