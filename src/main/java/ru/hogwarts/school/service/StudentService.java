package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public Student create(Student student) {
        logger.info("Was invoked method for \"create\"");
        Faculty faculty = null;
        if (student.getFaculty() != null && student.getFaculty().getId() != null) {
            faculty = facultyRepository.findById(student.getFaculty().getId())
                    .orElseThrow(() -> {
                        logger.error("There is not faculty with id = " + student.getFaculty().getId());
                        return new FacultyNotFoundException(student.getFaculty().getId());
                    });
        }
        student.setFaculty(faculty);
        student.setId(null);
        logger.debug("Was transmitted \"student\"={} in repository from method \"create\"", student);
        return studentRepository.save(student);
    }

    public void update(long id, Student student) {
        logger.info("Was invoked method for \"update\"");
        logger.debug("Was request \"studentRepository.deleteById(id)\"={} " +
                "in repository from method \"update\"", id);
        Student oldStudent = studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not student with id = " + id);
                    return new StudentNotFoundException(id);
                });
        Faculty faculty = null;
        if (student.getFaculty() != null && student.getFaculty().getId() != null) {
            faculty = facultyRepository.findById(student.getFaculty().getId())
                    .orElseThrow(() -> {
                        logger.error("There is not faculty with id = " + student.getFaculty().getId());
                        return new FacultyNotFoundException(student.getFaculty().getId());
                    });
        }
        oldStudent.setAge(student.getAge());
        oldStudent.setName(student.getName());
        oldStudent.setFaculty(faculty);
        logger.debug("Was transmitted \"oldStudent\"={} in repository from method \"update\"", oldStudent);
        studentRepository.save(oldStudent);
    }

    public Student get(long id) {
        logger.info("Was invoked method for \"get\"");
        logger.debug("Was request \"studentRepository.findById(id)\"={} in repository from method \"get\"", id);
        return studentRepository.findById(id).orElseThrow(() -> {
            logger.error("There is not student with id = " + id);
            return new StudentNotFoundException(id);
        });
    }

    public Student remove(long id) {
        logger.info("Was invoked method for \"remove\"");
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not student with id = " + id);
                    return new StudentNotFoundException(id);
                });
        logger.debug("Was request \"studentRepository.deleteById(id)\"={} " +
                "in repository from method \"delete\"", id);
        studentRepository.delete(student);
        return student;
    }

    public List<Student> filterByAge(int age) {
        logger.info("Was invoked method for \"filterByAge\"");
        logger.debug("Was request \"studentRepository.findAllByAge(age)\"={} " +
                "in repository from method \"get\"", age);
        return studentRepository.findAllByAge(age);
    }

    public List<Student> filterByRangeAge(int minAge, int maxAge) {
        logger.info("Was invoked method for \"filterByRangeAge\"");
        logger.info("Was request \"studentRepository.findAllByAgeBetween(minAge, maxAge)\"={},{} " +
                "in repository from method \"filterByRangeAge\"", minAge, maxAge);
        return studentRepository.findAllByAgeBetween(minAge, maxAge);
    }

    public Faculty findStudentsFaculty(long id) {
        logger.info("Was invoked method for \"findStudentsFaculty\"");
        logger.debug("Was request \"getStudent(id).getFaculty()\"={} " +
                "in repository from method \"findStudentsFaculty\"", id);
        return get(id).getFaculty();
    }

    public long getAmountOfStudents() {
        logger.info("Was invoked method for \"getAmountOfStudents\"");
        return studentRepository.getAmountOfStudents();
    }

    public double getAverageAgeOfStudents() {
        logger.info("Was invoked method for \"getAverageAgeOfStudents\"");
        return studentRepository.getAverageAgeOfStudents();
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for \"getLastFiveStudents\"");
        return studentRepository.getLastFiveStudents();
    }

    public List<String> getAllStudentsWithNameStartsWithLetterA() {

        return studentRepository.findAll()
                .stream()
                .map(Student::getName)
                .map(String::toUpperCase)
                .filter(name -> name.startsWith("A"))
                .sorted()
                .collect(Collectors.toList());
    }

    public double getMidAgeOfStudents() {
        return studentRepository.findAll()
                .stream()
                .collect(Collectors.averagingDouble(Student::getAge));
    }
}
