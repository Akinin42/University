package university.dao;

import java.util.List;
import university.entity.Student;

public interface StudentDao extends CrudDao<Student, Integer> {
    
    List<Student> findAllByCourse(String courseName);
    
    void deleteFromCourse(int studentId, int courseId);
    
    void insertStudentToCourses(Student student, List<Integer> coursesId);
}
