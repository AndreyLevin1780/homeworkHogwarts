package ru.hogwarts.school.controller;


import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    private final Faker faker = new Faker();

    @AfterEach
    public void clearRepository() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    @DisplayName("Корректно создает студента")
    public void createStudentPositiveTest() {
        Student expected = generateStudent();
        ResponseEntity<Student> actual = testRestTemplate.postForEntity(generateURL("/student"), expected, Student.class);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getName()).isEqualTo(expected.getName());
        assertThat(actual.getBody().getAge()).isEqualTo(expected.getAge());

        Student actualInBase = studentRepository.findById(actual.getBody().getId()).orElseThrow();
        assertThat(actualInBase).usingRecursiveComparison().ignoringFields("id", "faculty").isEqualTo(expected);
    }

    @Test
    @DisplayName("Корректно обновляет данные студента")
    public void updateStudentPositiveTest() {

        String name = faker.name().firstName();

        Student expected = generateStudent();
        expected = studentRepository.save(expected);
        expected.setName(name);
        expected.setAge(50);

        testRestTemplate.put(generateURL("/student/" + expected.getId()), expected);

        Student actual = studentRepository.findById(expected.getId()).orElseThrow();

        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getAge()).isEqualTo(50);

    }

    @Test
    @DisplayName("Корректно находит студента")
    public void getStudentPositiveTest() {

        Student expected = generateStudent();
        expected = studentRepository.save(expected);

        ResponseEntity<Student> actual = testRestTemplate.getForEntity(generateURL("/student/" + expected.getId()), Student.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getName()).isEqualTo(expected.getName());
        assertThat(actual.getBody().getAge()).isEqualTo(expected.getAge());
    }

    @Test
    @DisplayName("Корректно удаляет студента")
    public void removeStudentPositiveTest() {

        Student expected = generateStudent();
        expected = studentRepository.save(expected);

        ResponseEntity<Student> actual = testRestTemplate.exchange(generateURL("/student/" + expected.getId()), HttpMethod.DELETE, null, Student.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(actual.getBody().getName()).isNull();
        assertThat(actual.getBody().getAge()).isEqualTo(0);

    }

    @Test
    @DisplayName("Корректно находит студента по возрасту")
    public void filterByAgePositiveTest() {

        Student expected = generateStudent();
        expected = studentRepository.save(expected);

        Student expected2 = generateStudent();
        expected2.setAge(expected.getAge());
        expected2 = studentRepository.save(expected2);

        Student notExpected = generateStudent();
        //notExpected.setAge(expected.getAge());
        notExpected = studentRepository.save(notExpected);

        ResponseEntity<List> students = testRestTemplate.getForEntity("/student?age=" + expected.getAge(), List.class);
        assertThat(students.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(students.getBody()).isNotEmpty();
        assertThat(students.getBody().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Корректно находит студентов по диапазону возрастов")
    public void filterByRangeAgePositiveTest() {

        Student expected = generateStudent();
        expected.setAge(15);
        expected = studentRepository.save(expected);

        Student expected2 = generateStudent();
        expected2.setAge(20);
        expected2 = studentRepository.save(expected2);

        Student expected3 = generateStudent();
        expected3.setAge(100);
        expected3 = studentRepository.save(expected3);

        ResponseEntity<List> students = testRestTemplate.getForEntity("/student?minAge=20&maxAge=30", List.class);
        //System.out.println(students.toString());
        assertThat(students.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(students.getBody()).isNotEmpty();
        assertThat(students.getBody().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Корректно находит факультеты по id")
    public void findStudentsFaculty() {

        Student expected = generateStudent();
        expected = studentRepository.save(expected);
        System.out.println(expected.toString());

        Student expected2 = generateStudent();
        expected2 = studentRepository.save(expected2);
        System.out.println(expected2.toString());


        ResponseEntity<Faculty> facultyResponse = testRestTemplate.getForEntity("/student/2/faculty", Faculty.class);
        assertThat(facultyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(facultyResponse.getBody()).isNotNull();
        assertThat(facultyResponse.getBody().getName()).isEqualTo(expected2.getFaculty().getName());

    }

    private String generateURL(String path) {
        return "http://localhost:%s%s".formatted(port, path);
    }

    private Student generateStudent() {
        Student student = new Student(faker.harryPotter().character(), new Random().nextInt(18, 60));
        Faculty faculty = generateFaculty();
        facultyRepository.save(faculty);
        student.setFaculty(faculty);
        return student;
    }

    private Faculty generateFaculty() {
        Faculty faculty = new Faculty(faker.harryPotter().house(), faker.color().name());
        return faculty;
    }
}
