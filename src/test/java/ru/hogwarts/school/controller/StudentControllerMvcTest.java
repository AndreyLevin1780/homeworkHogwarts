package ru.hogwarts.school.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentController.class)
public class StudentControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private FacultyRepository facultyRepository;
    @MockBean
    private AvatarService avatarService;
    @SpyBean
    private StudentService studentService;

    private final Faker faker = new Faker();

    @Test
    @DisplayName("Корректно находит студента по возрасту")
    void getStudentAge() throws Exception {

        Student student1 = new Student();
        student1.setId(1L);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());

        Student student2 = new Student();
        student1.setId(2L);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());

        when(studentRepository.findAllByAge(10)).thenReturn(Arrays.asList(student1, student2));
        mockMvc.perform(get("/student?age=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Корректно находит студентов по диапазону возрастов")
    void findStudentByAgeBetween() throws Exception {
        Student student1 = new Student();
        student1.setId(1L);
        student1.setAge(15);
        student1.setName(faker.harryPotter().character());

        Student student2 = new Student();
        student1.setId(2L);
        student1.setAge(18);
        student1.setName(faker.harryPotter().character());

        when(studentRepository.findAllByAgeBetween(10, 20)).thenReturn(Arrays.asList(student1, student2));
        mockMvc.perform(get("/student?minAge=10&&maxAge=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }

    @Test
    @DisplayName("Корректно находит факультеты по id")
    void findStudentsFaculty() throws Exception {

        long id = 1L;
        String name = faker.harryPotter().house();
        String color = faker.color().name();
        Faculty faculty = new Faculty();
        faculty.setColor(color);
        faculty.setName(name);
        faculty.setId(id);
        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());
        student1.setFaculty(faculty);

        when(studentRepository.existsById(any())).thenReturn(true);
        when(studentRepository.findById(id)).thenReturn(Optional.of(student1));

        mockMvc.perform(get("/student/" + id + "/faculty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    @DisplayName("Корректно находит студента по id")
    void getStudent() throws Exception {
        long id = 1L;
        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(15);
        student1.setName(faker.harryPotter().character());
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        mockMvc.perform(get("/student/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(student1.getName()))
                .andExpect(jsonPath("$.age").value(student1.getAge()));

    }

    @Test
    @DisplayName("Корректно создает студента")
    void createStudent() throws Exception {
        Student student = new Student(null, "Test student", 20);
        when(studentRepository.save(any())).thenReturn(student);
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(student.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()));
    }

    @Test
    @DisplayName("Корректно обновляет данные студента")
    void updateStudent() throws Exception {

        long id = 1L;
        Faculty faculty = new Faculty();
        faculty.setColor("red");
        faculty.setName(faker.harryPotter().house());
        faculty.setId(id);

        Student student1 = new Student();
        student1.setId(id);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());
        student1.setFaculty(faculty);

        Student student2 = new Student();
        student2.setId(id);
        student2.setAge(12);
        student2.setName(faker.harryPotter().character());
        student2.setFaculty(faculty);

        when(studentRepository.findById(id)).thenReturn(Optional.of(student1));

        mockMvc.perform(put("/student/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(student2.toString()))
                .andExpect(status().isOk());
        verify(studentRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("Корректно удаляет студента")
    void deleteStudent() throws Exception {

        long id = new Random().nextLong(1,3);
        Student student = new Student();
        student.setId(id);
        student.setAge(10);
        student.setName(faker.harryPotter().character());
        System.out.println(student);

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));
        when(studentRepository.existsById(any())).thenReturn(true);

        mockMvc.perform(delete("/student/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(studentRepository, times(1)).delete(student);

    }


    private Faculty generateFaculty() {
        Faculty faculty = new Faculty(faker.harryPotter().house(), faker.color().name());
        return faculty;
    }
}
