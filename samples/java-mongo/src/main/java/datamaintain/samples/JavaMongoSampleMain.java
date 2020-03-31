package datamaintain.samples;

import com.mongodb.MongoClient;
import datamaintain.core.Datamaintain;
import datamaintain.core.config.DatamaintainConfig;
import datamaintain.core.db.driver.DatamaintainDriverConfig;
import datamaintain.db.driver.mongo.MongoDriverConfig;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.InputStream;

public class JavaMongoSampleMain {
    private static final String DATABASE_NAME = "datamaintain-sample-java-mongo";

    public static void main (String[] args) throws Exception {
        try {
            // Retrieve datamaintain properties
            final InputStream propertiesInputStream = JavaMongoSampleMain.class.getResourceAsStream("/config/datamaintain.properties");

            String mongoUri = "mongodb://localhost:27017/" + DATABASE_NAME;

            // Instanciation du driver Datamaintain.
            final DatamaintainDriverConfig datamaintainDriverConfig = new MongoDriverConfig(mongoUri);

            // Création de la configuration. À la place de l'inputStream il est possible de passer directement un objet Properties.
            final DatamaintainConfig config = DatamaintainConfig.buildConfig(propertiesInputStream, datamaintainDriverConfig);

            // Launch database update
            new Datamaintain(config).updateDatabase();

            // Print Charmander
            System.out.println(loadCharmander());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static datamaintain.samples.Starter loadCharmander() throws Exception {
        return getStartersCollection().findOne().as(Starter.class);
    }

    @Deprecated
    public static MongoCollection getStartersCollection() throws Exception {
        return new Jongo(new MongoClient().getDB(DATABASE_NAME)).getCollection("starters");
    }

}
