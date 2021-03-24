package university.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import university.dao.Connector;
import university.dao.CrudDao;
import university.exceptions.DaoException;

public abstract class AbstractCrudImpl<E> implements CrudDao<E, Integer> {

    protected final Connector connector;
    private final String saveQuery;
    private final String findByIdQuery;
    private final String findAllQuery;
    private final String findAllPaginationQuery;
    private final String deleteByIdQuery;

    protected AbstractCrudImpl(Connector connector, String saveQuery, String getByIdQuery, String getAllQuery,
            String findAllPaginationQuery, String deleteByIdQuery) {
        this.connector = connector;
        this.saveQuery = saveQuery;
        this.findByIdQuery = getByIdQuery;
        this.findAllQuery = getAllQuery;
        this.findAllPaginationQuery = findAllPaginationQuery;
        this.deleteByIdQuery = deleteByIdQuery;
    }

    @Override
    public void save(E entity) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(saveQuery)) {
            insert(statement, entity);
            statement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            throw new DaoException("Can't save " + entity, e);
        }
    }

    @Override
    public Optional<E> findById(Integer id) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(findByIdQuery)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery();) {
                return resultSet.next() ? Optional.ofNullable(createEntityFromResultSet(resultSet)) : Optional.empty();
            }
        } catch (SQLException | NullPointerException e) {
            throw new DaoException("Can't return by id" + id, e);
        }
    }

    @Override
    public List<E> findAll() {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(findAllQuery);
                final ResultSet resultSet = statement.executeQuery()) {
            List<E> entities = new ArrayList<>();
            while (resultSet.next()) {
                entities.add(createEntityFromResultSet(resultSet));
            }
            return entities;
        } catch (SQLException | NullPointerException e) {
            throw new DaoException("Can't return all entities", e);
        }
    }

    @Override
    public List<E> findAll(int limit, int offset) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(findAllPaginationQuery)) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            try (ResultSet resultSet = statement.executeQuery();) {
                List<E> entities = new ArrayList<>();
                while (resultSet.next()) {
                    entities.add(createEntityFromResultSet(resultSet));
                }
                return entities;
            }
        } catch (SQLException | NullPointerException e) {
            throw new DaoException("Can't return all entities from " + (offset + 1) + " to " + (limit + offset), e);
        }

    }

    @Override
    public void deleteById(Integer id) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteByIdQuery)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            throw new DaoException("Can't delete by id " + id, e);
        }
    }

    protected abstract void insert(PreparedStatement statement, E entity) throws SQLException;

    protected abstract E createEntityFromResultSet(ResultSet resultSet) throws SQLException;
}
