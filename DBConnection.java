import java.sql.*;
class DBConnection
{
	public static Connection connection=null;
		public static Connection getConnection()
		{
			synchronized(DBConnection.class)
			{
				if(connection==null)
				{
				try
					{
						Class.forName("com.mysql.jdbc.Driver");
	 					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vampire_db", "root", "root");
					}
					catch(Exception e)
					{
						System.out.println("SQL ERROR "+e);
					}
					return connection;
				}
				else
				{
					return connection;
				}
			
			}
			
		}
}