package ru.hogwarts.school.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

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
    @DisplayName("Корректно создает факультет")
    public void createFacultyPositiveTest() {

        Faculty expected = generateFaculty();
        facultyRepository.save(expected);
        System.out.println(expected.toString());

        ResponseEntity<Faculty> actual = testRestTemplate.postForEntity(generateURL("/faculty"), expected, Faculty.class);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getName()).isEqualTo(expected.getName());
        assertThat(actual.getBody().getColor()).isEqualTo(expected.getColor());

        Faculty actualInBase = facultyRepository.findById(actual.getBody().getId()).orElseThrow();
        assertThat(actualInBase).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }

    @Test
    @DisplayName("Корректно обновляет данные факультета")
    public void updateFacultyPositiveTest() {

        String name = "TestFaculty";

        Faculty expected = generateFaculty();
        expected = facultyRepository.save(expected);
        expected.setName(name);
        expected.setColor("Pink");

        testRestTemplate.put(generateURL("/faculty/" + expected.getId()), expected);

        Faculty actual = facultyRepository.findById(expected.getId()).orElseThrow();

        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getColor()).isEqualTo("Pink");

    }

    @Test
    @DisplayName("Корректно находит факультет")
    public void getFacultyPositiveTest() {

        Faculty expected = generateFaculty();
        expected = facultyRepository.save(expected);

        ResponseEntity<Faculty> actual = testRestTemplate.getForEntity(generateURL("/faculty/" + expected.getId()), Faculty.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getName()).isEqualTo(expected.getName());
        assertThat(actual.getBody().getColor()).isEqualTo(expected.getColor());
    }

    @Test
    @DisplayName("Корректно удаляет факультет")
    public void removeFacultyPositiveTest() {

        Faculty expected = generateFaculty();
        expected = facultyRepository.save(expected);

        ResponseEntity<Faculty> actual = testRestTemplate.exchange(generateURL("/faculty/" + expected.getId()), HttpMethod.DELETE, null, Faculty.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(actual.getBody().getName()).isNull();
        assertThat(actual.getBody().getColor()).isEqualTo(null);

    }

    @Test
    @DisplayName("Корректно находит факультет по цвету")
    public void filterByColorPositiveTest() {

        Faculty expected = generateFaculty();
        expected = facultyRepository.save(expected);
        System.out.println(expected.toString());

        Faculty notExpected = generateFaculty();
        //notExpected.setColor(expected.getColor());
        notExpected = facultyRepository.save(notExpected);
        System.out.println(notExpected.toString());


        ResponseEntity<List> actual = testRestTemplate.getForEntity(generateURL("/faculty?color=") + expected.getColor(), List.class);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotEmpty();
        assertThat(actual.getBody().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Корректно сортирует факультеты по цвету или названию")
    public void filterByColorOrNamePositiveTest() {

        Faculty expected = generateFaculty();
        expected = facultyRepository.save(expected);
        System.out.println(expected.toString());

        Faculty notExpected = generateFaculty();
        //notExpected.setColor(expected.getColor());
        notExpected = facultyRepository.save(notExpected);
        System.out.println(notExpected.toString());


        ResponseEntity<List> actual = testRestTemplate.getForEntity(generateURL("/faculty?colorOrName=") + expected.getColor(), List.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotEmpty();
        assertThat(actual.getBody().size()).isEqualTo(1);

        actual = testRestTemplate.getForEntity(generateURL("/faculty?colorOrName=") + notExpected.getName(), List.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotEmpty();
        assertThat(actual.getBody().size()).isEqualTo(1);
    }

    @Test
    public void findStudentsByFacultyIdPositiveTest() {

        Student expected = generateStudent();
        expected = studentRepository.save(expected);
        //System.out.println(expected.toString());

        Student expected2 = generateStudent();
        expected2.setFaculty(expected.getFaculty());
        expected2 = studentRepository.save(expected2);
        //System.out.println(expected2.toString());

        ResponseEntity<List<Student>> actualStudents = testRestTemplate.exchange(generateURL("/faculty/") + expected.getFaculty().getId() + "/students",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        List<Student> students = actualStudents.getBody();
        //System.out.println(students);
        assertThat(actualStudents.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(students).contains(expected);
        // при expected2.setFaculty(expected.getFaculty());
        assertThat(students).contains(expected2);
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
