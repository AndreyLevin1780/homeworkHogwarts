package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByAge(int age);

    List<Student> findAllByAgeBetween(int minAge, int maxAge);

    List<Student> findAllByFaculty_Id(long faculty_id);

    @Query(value = "SELECT count(*) AS count FROM students", nativeQuery = true)
    long getAmountOfStudents();

    @Query(value = "SELECT AVG(age) AS avg FROM students", nativeQuery = true)
    double getAverageAgeOfStudents();

    @Query(value = "SELECT * FROM students ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Student> getLastFiveStudents();
}
