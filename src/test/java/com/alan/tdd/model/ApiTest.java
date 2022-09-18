package com.alan.tdd.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// 演示伦敦学派（对比经典学派）
public class ApiTest extends JerseyTest{

    private static StudentRepository repository = mock(StudentRepository.class);

//     @Override
//     protected Application configure(){
//         return new StudentApplication(repository);
//     }

    @Test
    public void should_fetch_all_students_from_api() {
        when(repository.all()).thenReturn(Arrays.asList(
                new Student("john", "smith", "john.smith@email.com"),
                new Student("john2", "smith2", "john2.smith2@email.com")
        ));

        Student[] students = target("/students").request().get(Student[].class);
        assertEquals(2, students.length);

        assertEquals("john", students[0].getFirstName());



    }

}
