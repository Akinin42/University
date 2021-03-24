package university.dao;

import java.util.List;
import university.entity.Group;

public interface GroupDao extends CrudDao<Group, Integer> {
    
    List<Group> findAllBySizeEqualsOrLess(Integer groupSize);
}
