package com.alan.tdd.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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
    public void should_save_student_to_db() {
        List before = manager.createNativeQuery("select id,first_name,last_name,email from STUDENTS s").getResultList();

        // 执行测试
        manager.getTransaction().begin();
        Student saved = repository.save(new Student("john", "smith", "john.smith@email.com"));
        manager.getTransaction().commit();

        // 验证结果
        List result = manager.createNativeQuery("select id,first_name,last_name,email from STUDENTS s").getResultList();
        assertEquals(before.size() + 1, result.size());

        Object[] john = (Object[]) result.get(0);

        assertEquals(BigInteger.valueOf(saved.getId()), john[0]);
        assertEquals(saved.getFirstName(), john[1]);
        assertEquals(saved.getLastName(), john[2]);
        assertEquals(saved.getEmail(), john[3]);
    }

    // 状态验证
    @Test
    public void should_be_able_to_load_saved_student_by_id() {
        Optional<Student> loaded = repository.findById(john.getId());
        assertEquals(john.getFirstName(), loaded.get().getFirstName());
        assertEquals(john.getLastName(), loaded.get().getLastName());
        assertEquals(john.getId(), loaded.get().getId());
    }

    // 行为验证
    @Test
    public void should_be_able_to_load_saved_student_by_id2() {
        Mockito.when(manager.find(any(), any())).thenReturn(john);

        assertEquals(john, repository.findById(1).get());

        verify(manager).find(Student.class, 1L);
    }
}