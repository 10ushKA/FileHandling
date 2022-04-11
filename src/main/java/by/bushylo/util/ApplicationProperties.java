package by.bushylo.util;

import java.util.ResourceBundle;

public class ApplicationProperties {
    public static final ApplicationProperties APPLICATION_PROPERTIES = new ApplicationProperties();

    public ApplicationProperties(){}

    private String dbUrl;
    private String login;
    private String password;
    private String dbDriver;
    private int initSize;
    private int maxSize;

    private void init(){
        ResourceBundle rb = ResourceBundle.getBundle("config");
        dbUrl = rb.getString("db.url");
        login = rb.getString("db.login");
        password = rb.getString("db.password");
        dbDriver = rb.getString("db.driver");
        initSize = Integer.parseInt(rb.getString("cp.init.size"));
        maxSize = Integer.parseInt(rb.getString("cp.max.size"));
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public int getInitSize() {
        return initSize;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
