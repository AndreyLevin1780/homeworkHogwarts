package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FacultyService facultyService;

    private ObjectMapper objectMapper;

    private Faculty testFaculty;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        testFaculty = new Faculty();
        testFaculty.setId(1L);
        testFaculty.setName("Test Faculty");
        testFaculty.setColor("Green");
    }

    @Test
    public void createFaculty() throws Exception {
        when(facultyService.create(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Faculty"));
    }

    @Test
    public void updateFaculty() throws Exception {
        mockMvc.perform(put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFaculty)))
                .andExpect(status().isOk());

        verify(facultyService, times(1)).update(eq(1L), any(Faculty.class));
    }

    @Test
    public void getFaculty() throws Exception {
        when(facultyService.get(1L)).thenReturn(testFaculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Faculty"));
    }

    @Test
    public void removeFaculty() throws Exception {
        when(facultyService.remove(1L)).thenReturn(testFaculty);

        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Faculty"));

        verify(facultyService, times(1)).remove(1L);
    }

    @Test
    public void filterByColor() throws Exception {
        when(facultyService.filterByColor("Green")).thenReturn(List.of(testFaculty));

        mockMvc.perform(get("/faculty?color=Green"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Faculty"));
    }

    @Test
    public void filterByColorOrName() throws Exception {
        when(facultyService.filterByColorOrName("Green")).thenReturn(List.of(testFaculty));

        mockMvc.perform(get("/faculty?colorOrName=Green"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Faculty"));
    }

    @Test
    public void findStudentsByFacultyId() throws Exception {
        Student testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("Test Student");
        List<Student> students = Collections.singletonList(testStudent);

        when(facultyService.findStudentsByFacultyId(1L)).thenReturn(students);

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Student"));
    }
}
