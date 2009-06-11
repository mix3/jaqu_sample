package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection
					("jdbc:h2:mem:inmemory;DB_CLOSE_DELAY=-1", "sa", "");
			
			conn.setAutoCommit(false);
			
			stmt = conn.createStatement();
			stmt.executeUpdate("CREATE TABLE TEST(ID INT, NAME VARCHAR);");
			stmt.executeUpdate("INSERT INTO TEST VALUES(1, 'Hello');");
			stmt.executeUpdate("INSERT INTO TEST VALUES(2, 'Hello');");
			stmt.executeUpdate("INSERT INTO TEST VALUES(3, 'Hello');");
			if (true) {throw new SQLException();} 
			stmt.executeUpdate("INSERT INTO TEST VALUES(4, 'Hello');");
			stmt.executeUpdate("INSERT INTO TEST VALUES(5, 'Hello');");
			
			conn.commit();
		} catch (SQLException e) {
			System.out.println("SQLException");
			conn.rollback();
		} finally{
			System.out.println("finally");
			conn.setAutoCommit(true);
				
			ResultSet rs = stmt.executeQuery("SELECT * FROM TEST");
			while(rs.next()){
				System.out.println("id:"+rs.getString(1)+" name:"+rs.getString(2));
			}
				
			stmt.close();
			conn.close();
		}
	}
}
