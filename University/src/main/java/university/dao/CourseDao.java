package university.dao;

import java.util.List;
import java.util.Optional;
import university.entity.Course;

public interface CourseDao extends CrudDao<Course, Integer> {
    
    Optional<Course> findByName(String courseName);
    
    List<Course> findAllByStudentId(Integer studentId);
}
