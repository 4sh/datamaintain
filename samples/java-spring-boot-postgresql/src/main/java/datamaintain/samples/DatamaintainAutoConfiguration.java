package datamaintain.samples;

import datamaintain.core.Datamaintain;
import datamaintain.core.config.DatamaintainConfig;
import datamaintain.db.driver.jdbc.JdbcDriverConfig;
import kotlin.text.Regex;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties(DatamaintainProperties.class)
public class DatamaintainAutoConfiguration {
    public DatamaintainAutoConfiguration(DatamaintainProperties datamaintainProperties) {
        this.datamaintainProperties = datamaintainProperties;
    }

    final DatamaintainProperties datamaintainProperties;

    @Bean
    public Datamaintain datamaintain() {
        return new Datamaintain(
                new DatamaintainConfig.Builder()
                        .withIdentifierRegex(new Regex(datamaintainProperties.identifierRegex))
                        .withPath(Paths.get(datamaintainProperties.scriptsPath))
                        .withDriverConfig(new JdbcDriverConfig.Builder()
                                .withUri(datamaintainProperties.dbUri)
                                .build())
                        .build()
        );
    }
}
