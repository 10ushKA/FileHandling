package by.bushylo.db;

import by.bushylo.util.ApplicationProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionPool {

    private final ConcurrentLinkedQueue<Collection> availableConnection = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Collection> usedConnection =new ConcurrentLinkedQueue<>();

    private final int maxSize = ApplicationProperties.APPLICATION_PROPERTIES.getMaxSize();
    private final int initSize = ApplicationProperties.APPLICATION_PROPERTIES.getInitSize();

    private final String dbUrl = ApplicationProperties.APPLICATION_PROPERTIES.getDbUrl();
    private final String login = ApplicationProperties.APPLICATION_PROPERTIES.getLogin();
    private final String password =  ApplicationProperties.APPLICATION_PROPERTIES.getPassword();
    private final String dbDriver = ApplicationProperties.APPLICATION_PROPERTIES.getDbDriver();

    ConnectionPool(){}

    public final static ConnectionPool CONNECTION_POOL = new ConnectionPool();

    public Connection getConnection(){
        Connection connection;
        if(availableConnection.isEmpty() && (availableConnection.size() + usedConnection.size() + 5 < maxSize)){
            for (int i = 0; i < 5; i++) {
                addConnection();
            }
        }
        connection = (Connection) availableConnection.poll();
        usedConnection.add((Collection) connection);
        return connection;
    }

    public void init() throws ClassNotFoundException {
        Class.forName(dbDriver);
        for (int i = 0; i < initSize; i++) {
            addConnection();
        }
    }

    private Connection addConnection(){
        Connection connection = null;
        try{
            connection = new ConnectionProxy(DriverManager.getConnection(dbUrl, login, password));
        }catch (SQLException e){
            System.out.println(e);
        }
        availableConnection.add((Collection) connection);
        return connection;
    }

    public void returnConnection(Connection connection){
        usedConnection.remove(connection);
        availableConnection.add((Collection) connection);
    }

    public void closeAll() throws SQLException {
        for(Collection connection : availableConnection){
            ((ConnectionProxy)connection).realClose();
        }

        for(Collection connection : usedConnection){
            ((ConnectionProxy)connection).realClose();
        }
    }
}
