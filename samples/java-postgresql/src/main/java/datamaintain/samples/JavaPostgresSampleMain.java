package datamaintain.samples;

import datamaintain.core.Datamaintain;
import datamaintain.core.config.DatamaintainConfig;
import datamaintain.core.db.driver.DatamaintainDriverConfig;
import datamaintain.db.driver.jdbc.JdbcDriverConfig;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class JavaPostgresSampleMain {
    public static final String CONNECTION_URL = "jdbc:postgresql://localhost:5432/postgres?user=postgres";

    public static void main(String[] args) throws IOException {
        // Retrieve datamaintain properties
        final Properties properties = new Properties();
        properties.load(JavaPostgresSampleMain.class.getResourceAsStream("/config/datamaintain.properties"));

        // Instantiate jdbc driver config
        final DatamaintainDriverConfig datamaintainDriverConfig = JdbcDriverConfig.buildConfig(properties);

        // Instantiate datamaintain config
        final DatamaintainConfig config = DatamaintainConfig.buildConfig(datamaintainDriverConfig, properties);

        // Launch database update
        new Datamaintain(config).updateDatabase();

        // Print starters
        try {
            System.out.println(loadStarters());
        } catch (SQLException sqlException) {
            System.err.println("Failed execution");
            System.err.println(Arrays.toString(sqlException.getStackTrace()));
        }
    }

    private static List<Starter> loadStarters() throws SQLException {
        final ResultSet findOutput = DriverManager
                .getConnection(CONNECTION_URL)
                .createStatement()
                .executeQuery("SELECT * from starters");
        final List<Starter> starters = new ArrayList<>(2);

        while(findOutput.next()) {
            starters.add(new Starter(findOutput));
        }

        return starters;
    }
}
