package com.alan.tdd.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StudentRepository {

    @PersistenceContext(unitName = "student")
    private EntityManager manager;

    public StudentRepository(EntityManager manager) {
        this.manager = manager;
    }

    public Student save(Student student) {
        manager.persist(student);
        return student;
    }

    public Optional<Student> findById(long id) {
        return Optional.ofNullable(manager.find(Student.class, id));
    }

    public List<Student> all() {
        return Collections.emptyList();
    }
}
