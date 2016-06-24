import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DbManager {
        static private String databaseURL = "jdbc:mysql://localhost:3306/";
        static private String dbname = "Hadoop";
        static private String username = "root";
        static private String password = "1728";
	
	/**
	 * Opens a database connection
	 * @param dbName The database name
	 * @param readOnly True if the connection should be opened read-only
	 * @return An open java.sql.Connection
	 * @throws SQLException
	 */
	public static Connection getConnection(boolean readOnly)
	throws SQLException {        
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/hadoop/Hadoop.db");
            
			return conn;
        }
	
	private DbManager() {}
	
	static {
		try {
			Class.forName("org.sqlite.JDBC").newInstance();			
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
