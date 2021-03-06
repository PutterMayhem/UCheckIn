package objectclasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * An guest or employee at the hotel.
 */
public class Account {

	private String email;
	private int custID;
	private String custFName;
	private String custLName;
	private String phone;

	public Account(int custID, String custFName, String custLName, String phone, String email) {
		this.custFName = custFName;
		this.custLName = custLName;
		this.phone = phone;
		this.email = email;
		this.custID = custID;
	}

	public Account(String custFName, String custLName, String phone, String email) {
		this.custFName = custFName;
		this.custLName = custLName;
		this.phone = phone;
		this.email = email;
	}

	public String getFName() {
		return custFName;
	}

	public void setFName(String fName) {
		this.custFName = fName;
	}

	public String getLName() {
		return custLName;
	}

	public void setLName(String lName) {
		this.custLName = lName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String newPhone) {
		this.phone = newPhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getCustID(String email) throws SQLException {
		String sqlQuery = "SELECT cust_ID FROM Customer WHERE cust_Email = '" + email + "';";
		ResultSet sqlResults = connection().executeQuery(sqlQuery);
		if (sqlResults.next()) {
			custID = sqlResults.getInt("cust_ID");
			System.out.println("Customer Data Exists");
			connection().close();
			return custID;
		} else {
			System.out.println("Customer data does not exist");
			connection().close();
			return 0;
		}
	}
	
	public static Account getAccountFromDB(String email) {
		Account account = null;
		String q = "SELECT * FROM Customer WHERE cust_Email = '" + email + "';";
		try {
			ResultSet rs = connection().executeQuery(q);
			rs.next();
			String fname  = rs.getString("cust_Fname");
			String lname = rs.getString("cust_Lname");
			account = new Account(fname, lname, null, email);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return account;
	}

	public boolean createCustomer() throws SQLException {
		String sqlQuery = "INSERT INTO Customer (cust_Fname, cust_Lname, cust_Phone, cust_Email)" + " VALUES ('"
				+ custFName + "', '" + custLName + "', '" + phone + "', '" + email + "');";
		int result = connection().executeUpdate(sqlQuery);

		if (result != 0) {
			System.out.println("Customer Created");
			connection().close();
			return true;
		} else {
			System.out.println("Oops, something went wrong!");
			connection().close();
			return false;
		}
	}

	// Connection to database method
	private static Statement connection() {
		Statement statement = null;
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uCheckIn", "root", "");
			statement = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return statement;
	}

	/*
	 * Checks account status and returns true if exists. If account does not exist
	 * the account is created and returns true. If account can't be created returns
	 * false.
	 */
	public static boolean checkAccount(String customerFName, String customerLName, String phone, String email) {
		// check if customer already exists
		String sqlQuery = "SELECT cust_ID FROM Customer WHERE cust_Email = '" + email + "';";
		ResultSet sqlResults;
		try {
			sqlResults = connection().executeQuery(sqlQuery);

			if (sqlResults.next()) {
				System.out.println("Customer Data Exists");
				return true;
			} else {
				System.out.println("Customer data does not exist");
				sqlQuery = "INSERT INTO Customer (cust_Fname, cust_Lname, cust_Phone, cust_Email)" + " VALUES ('"
						+ customerFName + "', '" + customerLName + "', '" + phone + "', '" + email + "');";
				int result = connection().executeUpdate(sqlQuery);
				if (result != 0) {
					System.out.println("Customer Created");
					return true;
				} else {
					System.out.println("Could not create Customer");
					return false;
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
