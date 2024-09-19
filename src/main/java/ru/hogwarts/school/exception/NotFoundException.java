package ru.hogwarts.school.exception;

public abstract class NotFoundException extends RuntimeException{

    private long id;

    public NotFoundException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
