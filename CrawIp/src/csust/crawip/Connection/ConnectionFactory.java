package csust.crawip.Connection;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionFactory {
	private Connection conn;
	private ResultSet rs;
	private Statement stam;
	private static final String driver = "com.mysql.jdbc.Driver";
	private static final String url = "jdbc:mysql:///cbir";
	private static final String username = "root";
	private static final String password = "123456";
	
	public static Connection getConnection(){
		try {
			Class.forName(driver);
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("##获取数据库连接失败！##");
			return null;
		}
		
	}
	
	public static void close(Connection conn,ResultSet rs,Statement stam){
		try {
			if(conn != null){
				conn.close();
			}
			if(rs != null){
				rs.close();
			}
			if(stam != null){
				stam.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("##数据库操作变量操作变量操作失败！##");
		}
	}
	
	
	
}
