package ru.hogwarts.school.exception;

public class FacultyNotFoundException extends NotFoundException{

    public FacultyNotFoundException(long id) {
        super(id);
    }

    @Override
    public String getMessage() {
        return "Факультет с id = %d не найден.".formatted(getId());
    }
}
