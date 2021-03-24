package university;

import java.util.Random;
import org.postgresql.ds.PGSimpleDataSource;
import university.dao.Connector;
import university.dao.CourseDao;
import university.dao.DBInitialiser;
import university.dao.GroupDao;
import university.dao.ScriptExecutor;
import university.dao.StudentDao;
import university.dao.impl.ConnectorPostgres;
import university.dao.impl.CourseDaoImpl;
import university.dao.impl.GroupDaoImpl;
import university.dao.impl.StudentDaoImpl;
import university.domain.Controller;
import university.domain.DataGenerator;
import university.io.FileReader;
import university.io.ViewProvider;

public class UniversityLauncher {

    private static final String PROPERTIES_FILE = "postgresdatabase";

    public static void main(String[] args) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        Connector connector = new ConnectorPostgres(PROPERTIES_FILE, dataSource);
        FileReader reader = new FileReader();
        ScriptExecutor executor = new ScriptExecutor(connector, reader);
        Random random = new Random();
        DataGenerator generator = new DataGenerator(random);
        StudentDao studentDao = new StudentDaoImpl(connector);
        CourseDao courseDao = new CourseDaoImpl(connector);
        GroupDao groupDao = new GroupDaoImpl(connector);
        DBInitialiser initialiser = new DBInitialiser(reader, generator, executor, studentDao, courseDao, groupDao);
        initialiser.initDB();
        ViewProvider viewProvider = new ViewProvider();
        Controller controller = new Controller(studentDao, courseDao, groupDao, viewProvider);
        controller.run();
    }
}
