package university.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import university.dao.Connector;
import university.dao.ConnectorH2;
import university.exceptions.DaoException;
import university.io.FileReader;

public class TestUtil {

    private static Connector connector = new ConnectorH2("h2");
    private static FileReader reader = new FileReader();

    public static void queryToDB(String query) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Execute query failed!", e);
        }
    }

    public static int getNumberRow(String query) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DaoException("Can't return number row", e);
        }
    }

    public static void executeScript(String scriptPath) {
        String queryCreateDB = createSqlQuery(reader.read(scriptPath));
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryCreateDB)) {
            statement.execute();
        } catch (SQLException e) {
            throw new DaoException("Unable to execute script", e);
        }
    }

    private static String createSqlQuery(List<String> scriptFileContent) {
        return scriptFileContent.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
