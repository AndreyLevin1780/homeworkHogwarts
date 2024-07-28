package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student create(Student student) {
        student.setId(null);
        return studentRepository.save(student);
    }

    public void update(long id, Student student) {
        Student oldStudent = studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException(id));
        oldStudent.setAge(student.getAge());
        oldStudent.setName(student.getName());
        studentRepository.save(oldStudent);
    }

    public Student get(long id) {
        return studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException(id));
    }

    public Student remove(long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException(id));
        studentRepository.delete(student);
        return student;
    }

    public List<Student> filterByAge (int age) {
        return studentRepository.findAllByAge(age);
    }
}
