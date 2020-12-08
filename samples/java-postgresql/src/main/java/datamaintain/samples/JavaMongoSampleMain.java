package datamaintain.samples;

import datamaintain.core.Datamaintain;
import datamaintain.core.config.DatamaintainConfig;
import datamaintain.core.db.driver.DatamaintainDriverConfig;
import datamaintain.db.driver.jdbc.JdbcDriverConfig;

import java.io.IOException;
import java.util.Properties;

public class JavaPostgresSampleMain {
    private static final String DATABASE_NAME = "datamaintain-sample-java-postgres";

    public static void main(String[] args) throws IOException {
        // Retrieve datamaintain properties
        final Properties properties = new Properties();
        properties.load(JavaPostgresSampleMain.class.getResourceAsStream("/config/datamaintain.properties"));

        // Instantiate mongo driver config
        final DatamaintainDriverConfig datamaintainDriverConfig = JdbcDriverConfig.buildConfig(properties);

        // Instantiate datamaintain config
        final DatamaintainConfig config = DatamaintainConfig.buildConfig(datamaintainDriverConfig, properties);

        // Launch database update
        new Datamaintain(config).updateDatabase();

        // Print Charmander
        System.out.println(loadCharmander());
    }

    private static datamaintain.samples.Starter loadCharmander() {
        return getStartersCollection().findOne().as(Starter.class);
    }

    public static MongoCollection getStartersCollection() {
        return new Jongo(new MongoClient().getDB(DATABASE_NAME)).getCollection("starters");
    }

}
