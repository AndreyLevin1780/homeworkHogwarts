package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.entity.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty create(Faculty faculty) {
        faculty.setId(null);
        return facultyRepository.save(faculty);
    }

    public void update(long id, Faculty faculty) {
        Faculty oldFaculty = facultyRepository.findById(id)
                .orElseThrow(()-> new FacultyNotFoundException(id));
        oldFaculty.setColor(faculty.getColor());
        oldFaculty.setName(faculty.getName());
        facultyRepository.save(oldFaculty);
    }

    public Faculty get(long id) {
        return facultyRepository.findById(id)
                .orElseThrow(()-> new FacultyNotFoundException(id));
    }

    public Faculty remove(long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(()-> new FacultyNotFoundException(id));
        facultyRepository.delete(faculty);
        return faculty;
    }

    public List<Faculty> filterByColor (String color) {
        return facultyRepository.findAllByColor(color);
    }
}
