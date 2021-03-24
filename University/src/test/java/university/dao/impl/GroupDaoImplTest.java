package university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import university.dao.Connector;
import university.dao.ConnectorH2;
import university.entity.Group;
import university.exceptions.DaoException;
import university.utils.TestUtil;

class GroupDaoImplTest {

    private static GroupDaoImpl groupDao;   

    @BeforeAll
    static void init() {
        Connector connector = new ConnectorH2("h2");
        groupDao = new GroupDaoImpl(connector);
    }

    @BeforeEach
    void createTablesAndData() {
        TestUtil.executeScript("\\inittestdb.sql");
    }

    @Test
    void save_ShouldSaveGroupToDB_WhenInputValidGroup() {
        int numberRowBeforeSave = TestUtil.getNumberRow("SELECT COUNT(*) FROM groups;");
        Group group = Group.builder()
                .withId(3)
                .withName("DS-45")
                .build();
        groupDao.save(group);
        int numberRowAfterSave = TestUtil.getNumberRow("SELECT COUNT(*) FROM groups;");
        assertEquals(numberRowBeforeSave + 1, numberRowAfterSave);
    }

    @Test
    void save_ShouldThrowDaoException_WhenInputInvalidGroup() {
        Group invalidGroup = Group.builder()
                .withName(null)
                .build();
        assertThrows(DaoException.class, () -> groupDao.save(invalidGroup));
    }
    
    @Test
    void save_ShouldThrowDaoException_WhenInputNull() {        
        assertThrows(DaoException.class, () -> groupDao.save(null));
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenInputIdNotExists() {
        Optional<Group> expected = Optional.empty();
        Optional<Group> actual = groupDao.findById(10);
        assertEquals(expected, actual);
    }

    @Test
    void findById_ShouldReturnExpectedGroup_WhenInputExistentId() {
        Group expected = Group.builder()
                .withId(1)
                .withName("AB-22")
                .build();
        Group actual = groupDao.findById(1).get();
        assertEquals(expected, actual);
    }
    
    @Test
    void findById_ShouldThrowDaoException_WhenConnectIsNot() {
        Connector connectorMock = mock(Connector.class);
        GroupDaoImpl groupDaoWithFakeConnector = new GroupDaoImpl(connectorMock);
        assertThrows(DaoException.class, () -> groupDaoWithFakeConnector.findById(1));
    }

    @Test
    void findAll_ShouldReturnExpectedGroups_WhenGroupTableNotEmpty() {
        List<Group> expected = createGroups();
        List<Group> actual = groupDao.findAll();
        assertEquals(expected, actual);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenGroupTableEmpty() {
        TestUtil.queryToDB("DELETE FROM groups");
        List<Group> expected = new ArrayList<>();
        List<Group> actual = groupDao.findAll();
        assertEquals(expected, actual);
    }
    
    @Test
    void findAll_ShouldThrowDaoException_WhenConnectIsNot() {
        Connector connectorMock = mock(Connector.class);
        GroupDaoImpl groupDaoWithFakeConnector = new GroupDaoImpl(connectorMock);
        assertThrows(DaoException.class, () -> groupDaoWithFakeConnector.findAll());
    }

    @Test
    void findAll_ShouldReturnExpectedGroups_WhenInputLimitAndOffset() {
        List<Group> expected = createGroups();
        expected.remove(1);
        List<Group> actual = groupDao.findAll(1, 0);
        assertEquals(expected, actual);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenOffsetMoreTableSize() {
        List<Group> expected = new ArrayList<>();
        List<Group> actual = groupDao.findAll(2, 10);
        assertEquals(expected, actual);
    }

    @Test
    void deleteById_ShouldDeleteGroupWithInputId_WhenThisGroupExists() {
        int numberRowBeforeDelete = TestUtil.getNumberRow("SELECT COUNT(*) FROM groups;");
        groupDao.deleteById(1);
        int numberRowAfterDelete = TestUtil.getNumberRow("SELECT COUNT(*) FROM groups;");
        assertEquals(numberRowBeforeDelete - 1, numberRowAfterDelete);
    }

    @Test
    void findAllBySizeEqualsOrLess_ShouldReturnExpectedGroups_WhenInputGroupSizeEqualsExistGroupSize() {
        List<Group> expected = createGroups();
        List<Group> actual = groupDao.findAllBySizeEqualsOrLess(3);
        assertEquals(expected, actual);
    }

    @Test
    void findAllBySizeEqualsOrLess_ShouldReturnExpectedGroups_WhenInputGroupSizeMoreExistGroupSize() {
        List<Group> expected = createGroups();
        List<Group> actual = groupDao.findAllBySizeEqualsOrLess(10);
        assertEquals(expected, actual);
    }

    @Test
    void findAllBySizeEqualsOrLess_ShouldReturnEmptyList_WhenInputGroupSizeLessExistGroupSize() {
        List<Group> expected = new ArrayList<>();
        List<Group> actual = groupDao.findAllBySizeEqualsOrLess(2);
        assertEquals(expected, actual);
    }
    
    @Test
    void findAllBySizeEqualsOrLess_ShouldThrowDaoException_WhenConnectorNull() {        
        GroupDaoImpl groupDao = new GroupDaoImpl(null);
        assertThrows(DaoException.class, () -> groupDao.findAllBySizeEqualsOrLess(10));
    }

    private static List<Group> createGroups() {
        List<Group> groups = new ArrayList<>();
        Group group = Group.builder()
                .withId(1)
                .withName("AB-22")
                .build();
        groups.add(group);
        group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .build();
        groups.add(group);
        return groups;
    }
}
