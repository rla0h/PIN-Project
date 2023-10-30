package dbis;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import NWT.*;
public class Entity implements ServiceInterface {
	//static String url = "jdbc:postgresql://localhost:5432/postgres";
	static String url = "jdbc:postgresql://192.168.86.167:30000/postgres";
	static String username = "postgres";
	static String password = "1234";
	int resultCnt = 0;
	public String printMsg(String msg) {
		return msg;
	}
	
	public int insertSQL(String a, String b, String c, String d) throws RemoteException {
		
		
		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			System.out.println("Connected to the PSQL");
			//String selectsql = h.selectSQL();
			String insertsql = //h.insertSQL();
					"insert into public.\"data\" (\"aliasname\", description, \"mrid\", name) VALUES(?, ?, ?, ?)";
			
			PreparedStatement pstmt = conn.prepareStatement(insertsql);
			pstmt.setString(1, a);
			pstmt.setString(2, b);
			pstmt.setString(3, c);
			//String text = String.format("%-" + 1004 + "s", "OpenDDS db insert");
			
			pstmt.setString(4, d);
			//pstmt.setString(4, "g");
			
			pstmt.executeUpdate();
			resultCnt += 1;
			if (resultCnt >= 101) {
				resultCnt = 0;
			}
			System.out.println(resultCnt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println(e.toString());
		}
		return resultCnt;
	}
	/*
	 * public int getCount(int count) { return count; }
	 */
	
}


