package com.alan.tdd.resource;

import com.alan.tdd.model.Student;
import com.alan.tdd.model.StudentRepository;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.inject.Inject;
import java.util.List;

@Path("/students")
public class StudentResource {

    private StudentRepository repository;

    @Inject
    public StudentResource(StudentRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Student> all() {
        return repository.all();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") long id) {
        return repository.findById(id).map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }
}
