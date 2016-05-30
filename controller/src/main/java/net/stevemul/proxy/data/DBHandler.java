package net.stevemul.proxy.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class DBHandler.
 */
public class DBHandler {

  private static Log mLogger = LogFactory.getLog(DBHandler.class);
  
  /** The Constant DB_FOLDER. */
  public static final String DB_FOLDER = ".smproxy/db";
  
  /** The Constant DB_FILE. */
  public static final String DB_FILE = "proxydb";
  
  /** The Constant DB_FILE_PATH. */
  public static final String DB_FILE_PATH = System.getProperty("user.home") + File.separator + DB_FOLDER + File.separator + DB_FILE;
  
  /** The Constant JDBC_CONNECTION. */
  public static final String JDBC_CONNECTION = "jdbc:derby:" + DB_FILE_PATH + ";create=true";
  
  /** The Constant mInstance. */
  private static final DBHandler mInstance = new DBHandler();
  
  private Connection mConnection;
  
  /**
   * Instantiates a new DB handler.
   */
  private DBHandler() {
    
    try {
      mLogger.info("Starting up embedded database");
      
      mConnection = DriverManager.getConnection(JDBC_CONNECTION);
      
      if (mConnection != null) {
        mLogger.info("Successfully connected to embedded database");
        
        checkForTables();
      }
    }
    catch (Exception e) {
      mLogger.error("Unable to start database", e);
    }
  }
  
  /**
   * Check for tables.
   */
  private void checkForTables() {
    
    try {
      mLogger.info("Checking database for existence of tables");
      
      DatabaseMetaData dbm = mConnection.getMetaData();
      ResultSet tables = dbm.getTables(null, null, "MODULE_SETTINGS", null);
      
      if (!tables.next()) {
        mLogger.info("Tables not found, creating");
        createTables();
      }
      else {
        mLogger.info("Tables already created");
      }
      
    }
    catch (SQLException e) {
      mLogger.error("Unable to query tables", e);
    }
  }
  
  /**
   * Creates the tables.
   */
  private void createTables() {
    
    try (InputStream in = DBHandler.class.getClassLoader().getResourceAsStream("sql/schema.sql")) {
      String sql = IOUtils.toString(in);
      
      Statement createStmt = mConnection.createStatement();
      createStmt.execute(sql);
      
      mLogger.info("Successfully created tables");
    }
    catch (IOException e) {
      mLogger.error("Unable to read schema", e);
    }
    catch (SQLException e) {
      mLogger.error("Unable to execute schema.sql", e);
    }
  }
  
  /**
   * Gets the connection.
   *
   * @return the connection
   */
  public Connection getConnection() {
    return mConnection;
  }
  
  /**
   * Gets the single instance of DBHandler.
   *
   * @return single instance of DBHandler
   */
  public static DBHandler getInstance() {
    return mInstance;
  }
}
