package university.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import university.dao.Connector;
import university.dao.GroupDao;
import university.entity.Group;
import university.exceptions.DaoException;

public class GroupDaoImpl extends AbstractCrudImpl<Group> implements GroupDao {

    private static final String SAVE_QUERY = "INSERT INTO groups (group_name) VALUES(?);";
    private static final String FIND_BY_ID_QUERY = "SELECT  * FROM groups WHERE group_id = ?;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM groups ORDER BY group_id;";
    private static final String FIND_ALL_PAGINATION_QUERY = "SELECT * FROM groups ORDER BY group_id LIMIT ? OFFSET ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM groups WHERE group_id = ?;";
    private static final String FIND_GROUPS_BY_SIZE_QUERY = "SELECT COUNT(*)<=? AS group_size, group_id FROM students "
            + "WHERE group_id >=1 GROUP BY group_id;";
    private static final String GROUP_ID = "group_id";

    public GroupDaoImpl(Connector connector) {
        super(connector, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY, DELETE_BY_ID_QUERY);
    }

    @Override
    protected void insert(PreparedStatement statement, Group group) throws SQLException {
        statement.setString(1, group.getName());
    }

    @Override
    protected Group createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        return Group.builder()
                .withId(resultSet.getInt(GROUP_ID))
                .withName(resultSet.getString("group_name"))
                .build();
    }

    @Override
    public List<Group> findAllBySizeEqualsOrLess(Integer groupSize) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_GROUPS_BY_SIZE_QUERY)) {
            statement.setInt(1, groupSize);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Group> groups = new ArrayList<>();
                while (resultSet.next()) {
                    if (resultSet.getBoolean("group_size")) {
                        Optional<Group> value = findById(resultSet.getInt(GROUP_ID));
                        Group group = value.get();
                        groups.add(group);
                    }
                }
                return groups;
            }
        } catch (SQLException | NullPointerException e) {
            throw new DaoException("Can't return everything groups by size less or equals " + groupSize, e);
        }
    }
}
