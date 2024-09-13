package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty create(Faculty faculty) {
        logger.info("Was invoked method for \"create\"");
        faculty.setId(null);
        return facultyRepository.save(faculty);
    }

    public void update(long id, Faculty faculty) {
        logger.info("Was invoked method for \"update\"");
        Faculty oldFaculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not faculty with id = " + id);
                    return new FacultyNotFoundException(id);
                });
        oldFaculty.setColor(faculty.getColor());
        oldFaculty.setName(faculty.getName());
        facultyRepository.save(oldFaculty);
    }

    public Faculty get(long id) {
        logger.info("Was invoked method for \"get\"");
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not faculty with id = " + id);
                    return new FacultyNotFoundException(id);
                });
    }

    public Faculty remove(long id) {
        logger.info("Was invoked method for \"remove\"");
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not faculty with id = " + id);
                    return new FacultyNotFoundException(id);
                });
        facultyRepository.delete(faculty);
        return faculty;
    }

    public List<Faculty> filterByColor(String color) {
        logger.info("Was invoked method for \"filterByColor\"");
        return facultyRepository.findAllByColor(color);
    }

    public List<Faculty> filterByColorOrName(String colorOrName) {
        logger.info("Was invoked method for \"filterByColorOrName\"");
        return facultyRepository.findAllByColorIgnoreCaseOrNameIgnoreCase(colorOrName, colorOrName);
    }

    public List<Student> findStudentsByFacultyId(long id) {
        logger.info("Was invoked method for \"findStudentsByFacultyId\"");
        return studentRepository.findAllByFaculty_Id(id);
    }

    public String getFacultyWithLongestName() {
        return facultyRepository.findAll()
                .stream()
                .map(Faculty::getName)
                .max(Comparator.comparing(String::length)).orElseThrow();
    }

    public void getTestParallelInt() {

        long sTime = System.currentTimeMillis();

        int sum = Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .parallel()
                .reduce(0, (a, b) -> a + b);

        long fTime = System.currentTimeMillis();


        logger.info("Sum is " + sum + ". Stream execution time: " + (fTime - sTime) + " ms");

        sum = 0;
        sTime = System.currentTimeMillis();
        for (int i = 0; i < 1_000_000; i++) {
            sum += i;
        }
        fTime = System.currentTimeMillis();

        logger.info("Sum is " + sum + ". Cycle execution time: " + (fTime - sTime) + " ms");
    }


}
