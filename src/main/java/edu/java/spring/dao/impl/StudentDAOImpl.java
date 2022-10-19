package edu.java.spring.dao.impl;

import edu.java.spring.dao.StudentDAO;
import edu.java.spring.model.Student;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class StudentDAOImpl implements StudentDAO, DisposableBean {

    private DataSource dataSource;
    static Logger LOGGER = Logger.getLogger(String.valueOf(StudentDAOImpl.class));
    JdbcTemplate jdbcTemplate;
    private List<Student> students;

    public StudentDAOImpl() {
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    private void createTablefNotExist() throws SQLException {
        DatabaseMetaData dbmd = dataSource.getConnection().getMetaData();
        ResultSet rs = dbmd.getTables(null, null, "STUDENT", null);
        if (rs.next()) {
            LOGGER.info("Table " + rs.getString("TABLE_NAME") + " already exists ! ");
            return;
        }

        jdbcTemplate.execute("create table student (" +
                " id   bigint primary key generated always as identity (start with 1, increment by 1), " +
                " name varchar(1000), " +
                " age  integer)");
    }

    @Override
    public void insert(Student student) {
        jdbcTemplate.update("INSERT INTO STUDENT (name, age) VALUES (?,?)",
                student.getName(), student.getAge());
        LOGGER.info("Created Record Name = " + student.getName());
    }

    @Override
    public List<Student> list(String query) {
        students = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return jdbcTemplate.execute((Statement statement) -> {
                ResultSet rs = statement.executeQuery("Select * from STUDENT");
                while (rs.next()) {
                    students.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getInt("age")));
                }
                return students;
            });
        } else {
            return jdbcTemplate.execute((Statement statement) -> {
                ResultSet rs = statement.executeQuery("Select * from STUDENT where ID=" + query);
                while (rs.next()) {
                    students.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getInt("age")));
                }
                return students;
            });
        }
    }

    @Override
    public void delete(String id) {
        jdbcTemplate.execute("DELETE FROM STUDENT WHERE ID =" + id);
    }

    @Override
    public Student get(String id) {
        return jdbcTemplate.queryForObject("SELECT * FROM STUDENT WHERE ID = " + id, new StudentRowMapper());
    }

    private final static class StudentRowMapper implements RowMapper<Student> {
        Logger LOGGER2 = Logger.getLogger(String.valueOf(StudentRowMapper.class));

        @Override
        public Student mapRow(ResultSet resultSet, int i) throws SQLException {
            try {
                Student student = new Student();
                student.setId(resultSet.getInt("id"));
                student.setName(resultSet.getString("name"));
                student.setAge(resultSet.getInt("age"));
                return student;
            } catch (Exception e) {
                LOGGER2.info(e.getMessage());
                return null;
            }
        }
    }

    @Override
    public int update(Student student) {
        return jdbcTemplate.update("UPDATE STUDENT SET NAME = ? WHERE ID = ?", student.getName(), student.getId());
    }

    @Override
    public void destroy() throws Exception {
        DriverManager.getConnection("jdbc:derby:C:/Temp/sampledb2;shutdown=true");
    }
}
