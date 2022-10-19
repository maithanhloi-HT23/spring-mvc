package edu.java.spring.dao;

import edu.java.spring.model.Student;

import java.util.List;

public interface StudentDAO {
    void insert(Student student);
    List<Student> list(String query);
    void delete(String id);
    Student get(String id);
    int update(Student student);
}
