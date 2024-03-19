package datamaintain.samples;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "datamaintain")
public class DatamaintainProperties {
    String scriptsPath = "";
    String identifierRegex = "";
    String dbUri = "";

    public String getScriptsPath() {
        return scriptsPath;
    }

    public DatamaintainProperties setScriptsPath(String scriptsPath) {
        this.scriptsPath = scriptsPath;
        return this;
    }

    public String getIdentifierRegex() {
        return identifierRegex;
    }

    public DatamaintainProperties setIdentifierRegex(String identifierRegex) {
        this.identifierRegex = identifierRegex;
        return this;
    }

    public String getDbUri() {
        return dbUri;
    }

    public DatamaintainProperties setDbUri(String dbUri) {
        this.dbUri = dbUri;
        return this;
    }
}