package ru.hogwarts.school.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(StudentController.class)
public class StudentControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @Mock
    private AvatarService avatarService;

    @InjectMocks
    private StudentController studentController;

    private ObjectMapper objectMapper;

    private Student testStudent;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("Test Student");
        testStudent.setAge(20);
    }

    @Test
    public void createStudent() throws Exception {
        when(studentService.create(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Student"));
    }

    @Test
    public void updateStudent() throws Exception {
        mockMvc.perform(put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent)))
                .andExpect(status().isOk());

        verify(studentService, times(1)).update(eq(1L), any(Student.class));
    }

    @Test
    public void getStudent() throws Exception {
        when(studentService.get(1L)).thenReturn(testStudent);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Student"));
    }

    @Test
    public void removeStudent() throws Exception {
        when(studentService.remove(1L)).thenReturn(testStudent);

        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Student"));

        verify(studentService, times(1)).remove(1L);
    }

    @Test
    public void filterByAge() throws Exception {
        when(studentService.filterByAge(20)).thenReturn(List.of(testStudent));

        mockMvc.perform(get("/student?age=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Student"));
    }

    @Test
    public void filterByRangeAge() throws Exception {
        when(studentService.filterByRangeAge(18, 22)).thenReturn(List.of(testStudent));

        mockMvc.perform(get("/student?minAge=18&maxAge=22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Student"));
    }

    @Test
    public void findStudentsFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Test Faculty");
        when(studentService.findStudentsFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Faculty"));
    }

    @Test
    public void getAvatarFromDb() throws Exception {
        byte[] avatarData = new byte[]{1, 2, 3};
        when(avatarService.getAvatarFromDb(1L)).thenReturn(Pair.of(avatarData, "image/png"));

        mockMvc.perform(get("/student/1/avatar-from-db"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));
    }

    @Test
    public void getAvatarFromFs() throws Exception {
        byte[] avatarData = new byte[]{1, 2, 3};
        when(avatarService.getAvatarFromFs(1L)).thenReturn(Pair.of(avatarData, "image/png"));

        mockMvc.perform(get("/student/1/avatar-from-fs"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));
    }
}
