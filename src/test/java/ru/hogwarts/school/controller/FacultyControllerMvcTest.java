package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FacultyController.class)
public class FacultyControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;

    @SpyBean
    private FacultyService facultyService;

    @SpyBean
    private StudentService studentService;

    private final Faker faker = new Faker();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Создание факультета")
    public void createFacultyTest() throws Exception {

        long id = new Random().nextLong(1,3);

        Faculty faculty = generateFaculty();
        faculty.setId(id);

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", faculty.getName());
        facultyObject.put("id", id);
        facultyObject.put("color", faculty.getColor());

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
        verify(facultyRepository, only()).save(any());
    }

    @Test
    @DisplayName("Изменение факультета")
    void updateFacultyTest() throws Exception {

        long id = new Random().nextLong(1,3);
        String nameNew = "Test faculty";
        String colorNew = "Test color";

        Faculty oldFaculty = generateFaculty();
        oldFaculty.setId(id);

        Faculty newFaculty = new Faculty();
        newFaculty.setName(nameNew);
        newFaculty.setColor(colorNew);
        newFaculty.setId(id);

        JSONObject newFacultyTest = new JSONObject();
        newFacultyTest.put("name", nameNew);
        newFacultyTest.put("id", id);
        newFacultyTest.put("color", colorNew);
        System.out.println(oldFaculty);
        System.out.println(newFaculty);
        when(facultyRepository.findById(id)).thenReturn(Optional.of(oldFaculty));
        when(facultyRepository.save(any())).thenReturn(newFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/faculty/{id}", id)
                .content(newFacultyTest.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        verify(facultyRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("Запрос факультета по id")
    void getFacultyTest() throws Exception {

        long id = new Random().nextLong(1,3);

        Faculty faculty = generateFaculty();
        faculty.setId(id);

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", faculty.getName());
        facultyObject.put("id", id);
        facultyObject.put("color", faculty.getColor());

        when(facultyRepository.findById(any())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
        verify(facultyRepository, only()).findById(any());

    }

    @Test
    @DisplayName("Удаление факультета")
    void deleteFacultyTest() throws Exception {

        long id = new Random().nextLong(1,3);

        Faculty oldFaculty = generateFaculty();
        oldFaculty.setId(id);
        System.out.println(oldFaculty);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(oldFaculty);
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(oldFaculty));
        when(facultyRepository.existsById(any())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/faculty/{id}", id));
        verify(facultyRepository, times(1)).delete(oldFaculty);
    }

    @Test
    @DisplayName("Запрос всех факультетов по цвету")
    void findAllByColor() throws Exception {

        long id1 = 1L;
        long id2 = 2L;
        long id3 = 3L;

        Faculty faculty1 = generateFaculty();
        faculty1.setId(id1);
        Faculty faculty2 = generateFaculty();
        faculty2.setId(id2);
        faculty2.setColor(faculty1.getColor());
        Faculty faculty3 = generateFaculty();
        faculty3.setColor(faculty1.getColor());
        faculty3.setId(id3);


        List<Faculty> facultyList = new ArrayList<>();
        facultyList.add(faculty1);
        facultyList.add(faculty2);
        facultyList.add(faculty3);
        when(facultyRepository.findAllByColor(any())).thenReturn(facultyList);

        System.out.println(facultyList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty?color=" + faculty1.getColor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color").value(faculty1.getColor()))
                .andExpect(jsonPath("$[1].color").value(faculty1.getColor()))
                .andExpect(jsonPath("$[2].color").value(faculty1.getColor()));

        verify(facultyRepository, only()).findAllByColor(any());
    }

    @Test
    @DisplayName("Запрос факультета по имени или цвету(по цвету)")
    void findByNameIgnoreCaseOrColorIgnoreCaseForColor() throws Exception {

        Faculty faculty1 = generateFaculty();
        Faculty faculty2 = generateFaculty();
        faculty2.setColor(faculty1.getColor());
        Faculty faculty3 = generateFaculty();
        faculty3.setColor(faculty1.getColor());

        String color = faculty1.getColor();

        List<Faculty> facultyList = new ArrayList<>();
        facultyList.add(faculty1);
        facultyList.add(faculty2);
        facultyList.add(faculty3);
        when(facultyRepository.findAllByColorIgnoreCaseOrNameIgnoreCase(any(), any())).thenReturn(facultyList);

        System.out.println(facultyList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty?colorOrName=" + faculty1.getColor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].color").value(color))
                .andExpect(jsonPath("$[1].color").value(color))
                .andExpect(jsonPath("$[2].color").value(color));

        verify(facultyRepository, only()).findAllByColorIgnoreCaseOrNameIgnoreCase(any(), any());

    }

    @Test
    @DisplayName("Запрос факультета по имени или цвету(по имени)")
    void findByNameIgnoreCaseOrColorIgnoreCaseForName() throws Exception {

        Faculty faculty1 = generateFaculty();
        Faculty faculty2 = generateFaculty();
        faculty2.setName(faculty1.getName());
        Faculty faculty3 = generateFaculty();
        faculty3.setName(faculty1.getName());

        String name = faculty1.getName();

        List<Faculty> facultyList = new ArrayList<>();
        facultyList.add(faculty1);
        facultyList.add(faculty2);
        facultyList.add(faculty3);
        when(facultyRepository.findAllByColorIgnoreCaseOrNameIgnoreCase(any(), any())).thenReturn(facultyList);

        System.out.println(facultyList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty?colorOrName=" + faculty1.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[1].name").value(name))
                .andExpect(jsonPath("$[2].name").value(name));

        verify(facultyRepository, only()).findAllByColorIgnoreCaseOrNameIgnoreCase(any(), any());

    }

    @Test
    @DisplayName("Нахождение студентов по id факультета")
    void findStudentsByFacultyId() throws Exception {
        //data

        long id = new Random().nextLong(1,3);
        Faculty faculty = generateFaculty();
        faculty.setId(id);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setAge(10);
        student1.setName(faker.harryPotter().character());
        student1.setFaculty(faculty);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setAge(12);
        student2.setName(faker.harryPotter().character());
        student2.setFaculty(faculty);

        Student student3 = new Student();
        student3.setId(3L);
        student3.setAge(12);
        student3.setName(faker.harryPotter().character());
        student3.setFaculty(faculty);

        List<Student> studentList = new ArrayList<>();
        studentList.add(student1);
        studentList.add(student2);
        studentList.add(student3);

        when(studentRepository.findAllByFaculty_Id(any(Long.class))).thenReturn(studentList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + id + "/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        verify(studentRepository, times(1)).findAllByFaculty_Id(id);

    }

    private Faculty generateFaculty() {
        Faculty faculty = new Faculty(faker.harryPotter().house(), faker.color().name());
        return faculty;
    }
}
