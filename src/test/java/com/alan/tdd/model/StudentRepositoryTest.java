package com.alan.tdd.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// 演示：有计划的自动化验证（改造"无计划的手动验证"）【不再是通过打印肉眼看的方式去验证】
class StudentRepositoryTest {

    private EntityManagerFactory factory;
    private EntityManager manager;
    private StudentRepository repository;
    private Student john;

    @BeforeEach
    void setUp() {
        factory = Persistence.createEntityManagerFactory("student");
        manager = factory.createEntityManager();
        repository = new StudentRepository(manager);

        manager.getTransaction().begin();
        john = repository.save(new Student("john", "smith", "john.smith@email.com"));
        manager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        manager.clear();
        manager.close();
        factory.close();
    }


    @Test
    void should_generate_id_for_save_entity() {
        // 断言
        assertNotEquals(0, john.getId());
    }

    @Test
    public void should_be_able_to_load_saved_student_by_id() {
        Optional<Student> loaded = repository.findById(john.getId());
        assertEquals(john.getFirstName(), loaded.get().getFirstName());
        assertEquals(john.getLastName(), loaded.get().getLastName());
        assertEquals(john.getId(), loaded.get().getId());
    }

}