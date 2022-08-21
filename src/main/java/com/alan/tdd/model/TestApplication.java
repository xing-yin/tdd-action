package com.alan.tdd.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Optional;

public class TestApplication {

    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("student");

        EntityManager entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();

        StudentRepository repository = new StudentRepository(entityManager);

        Student john = repository.save(new Student("john", "smith","john.smith@email.com"));

        entityManager.getTransaction().commit();
        System.out.println(john.getId());

        Optional<Student> loaded = repository.findById(john.getId());
        System.out.println(loaded);
    }
}
