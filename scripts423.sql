SELECT students.name, students.age, faculties.name
FROM students
LEFT JOIN faculties ON students.faculty_id = faculties.id
SELECT students.name, students.age
FROM students
INNER JOIN avatar ON avatar.student_id = students.id