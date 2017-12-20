


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLChecker {
	private static String url="jdbc:mysql://localhost:3306/chat?autoReconnect=true&useSSL=false";
	private static Connection connection = null;
	private static Statement statement = null;
	

	public static String createUser(String username,String password) throws SQLException{
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
				} catch(ClassNotFoundException e){
					System.err.println(e.getMessage());
				}
				try{	
					
					
					connection = DriverManager.getConnection(url,SQLCredentials.USER_NAME, SQLCredentials.PASSWORD	);
					connection.clearWarnings();
					if (username.contains("'"))return "Password Can't contain ' ' '";
					statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS WHERE USERNAME = '"+username+"'");
					while (resultSet.next()){
							return "Name is already in use.";
					}
				
					statement = connection.createStatement();
					String query = " insert into USERS (USERNAME,PASSWORD,PRIV)"
					        + " values (?, ?, ?)";
					PreparedStatement preparedStmt = connection.prepareStatement(query);
					preparedStmt.setString (1, username);
					preparedStmt.setString (2, Security.hashSha3(password));
					preparedStmt.setString (3, "1");
					preparedStmt.execute();
					return "Account Created.";
				} catch (Exception e){
					System.out.println(e.getLocalizedMessage());
				}finally{
					connection.close();
				}
				return "Unknown status.";
	}
	public static boolean CheckPassSQL(String USERNAME, String PASSWORD) throws SQLException {
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		try{
			connection = DriverManager.getConnection(url,SQLCredentials.USER_NAME, SQLCredentials.PASSWORD);
			connection.clearWarnings();
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT PASSWORD FROM USERS WHERE USERNAME = '"+USERNAME+"'");
			while (resultSet.next()){
				if (resultSet.getString(1).equals(Security.hashSha3(PASSWORD))){
					connection.close();
					
					return true;
				}
			}
		} catch (Exception e){
			System.out.println(e.getLocalizedMessage());
		}finally{
			connection.close();
		}
		return false;
	}
	public static void changePriv(String user,String newPriv) throws SQLException{
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		try{	
			connection = DriverManager.getConnection(url,SQLCredentials.USER_NAME, SQLCredentials.PASSWORD	);
			connection.clearWarnings();
			
			statement = connection.createStatement();
			
			String query = "update USERS set PRIV = '"+newPriv+"' where USERNAME = '"+user+"'";
			PreparedStatement preparedStmt = connection.prepareStatement(query);
			preparedStmt.execute();
			return;
		} catch (Exception e){
			System.out.println(e.getLocalizedMessage());
		}finally{
			connection.close();
		}
		return;
	}
	
	public static int getPriv(String user) {
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		try{
			connection = DriverManager.getConnection(url,SQLCredentials.USER_NAME, SQLCredentials.PASSWORD);
			connection.clearWarnings();
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT PRIV FROM USERS WHERE USERNAME = '"+user+"'");
			while (resultSet.next()){
				return Integer.parseInt(resultSet.getString(1));
			}
		} catch (Exception e){
			System.out.println(e.getLocalizedMessage());
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		return -1;
	}
	public static boolean isBanned(String ip){
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		try{
			connection = DriverManager.getConnection(url,SQLCredentials.USER_NAME, SQLCredentials.PASSWORD);
			connection.clearWarnings();
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM BANNED;");
			while (resultSet.next()){
				if (resultSet.getString(1).equals(ip)){
					return true;
				}
			}
		} catch (Exception e){
			System.out.println(e.getLocalizedMessage());
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		return false;
	}
	public static void addToBanList(String ip) throws SQLException{
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		try{	
			connection = DriverManager.getConnection(url,SQLCredentials.USER_NAME, SQLCredentials.PASSWORD	);
			connection.clearWarnings();
			
			statement = connection.createStatement();
			
			String query = " insert into BANNED (ip)"
			        + " values (?)";
			PreparedStatement preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString (1, ip);
			preparedStmt.execute();
			return;
		} catch (Exception e){
			System.out.println(e.getLocalizedMessage());
		}finally{
			connection.close();
		}
		return;
	}
}