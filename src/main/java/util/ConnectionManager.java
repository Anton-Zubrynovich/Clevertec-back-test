package util;


import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The type Connection manager.
 */
public final class ConnectionManager {

    private static final String URL = "DB_URL";
    private static final String USERNAME = "DB_USERNAME";
    private static final String PASSWORD = "DB_PASSWORD";
    private static final String POOL_SIZE_KEY = "DB_POOL_SIZE";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final Integer DEFAULT_POOL_SIZE = 10;
    private static BlockingQueue<Connection> pool;
    private static List<Connection> sourceConnections;

    static {
        loadDriver();
        initConnectionPool();
    }

    private ConnectionManager(){
    }

    private static void initConnectionPool() {
        String poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        sourceConnections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open();
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(), new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close")
                    ? pool.add((Connection) proxy)
                    :method.invoke(connection, args));
            pool.add(proxyConnection);
            sourceConnections.add(connection);
        }
    }

    /**
     * Get connection.
     *
     * @return the connection
     */
    public static Connection get() {
        try{
            return pool.take();
        }
        catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static Connection open(){
        try {
            return DriverManager.getConnection(PropertiesUtil.get(URL), PropertiesUtil.get(USERNAME), PropertiesUtil.get(PASSWORD));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadDriver(){
        try{
            Class.forName(DRIVER);
        }
        catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Close pool.
     */
    public static void closePool(){
        try{
            for (Connection sourceConnection : sourceConnections){
                sourceConnection.close();
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


}
