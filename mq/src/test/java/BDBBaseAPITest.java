import com.sleepycat.je.*;

import java.io.File;

public class BDBBaseAPITest {

    public static void main(String[] args) {
        String dataBaseName = "test1";

        String path = System.getProperty("user.dir") + "/mq/bdb";
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);

        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);

        Environment environment = new Environment(file, environmentConfig);
        Database database = environment.openDatabase(null, dataBaseName, databaseConfig);

        DatabaseEntry keyEntry = new DatabaseEntry("key".getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry("value".getBytes());
        database.put(null, keyEntry, valueEntry);
        environment.sync();
        database.put(null, keyEntry, valueEntry);
        System.out.println(database.count());
    }
}
