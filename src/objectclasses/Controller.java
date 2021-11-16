package objectclasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class Controller {

	private DataStore ds = DataStore.getInstance();

	public Controller() {
	}

	/*
	 * Returns true if customer entered valid data. False if not. Use if statement
	 * to determine actions taken.
	 */
	public boolean logIn(String email, String confID) throws SQLException {
		String sqlQuery1 = "Select * from Customer where cust_email ='" + email + "';";
		String sqlQuery2 = "Select * from Booking where conf_ID = '" + confID + "';";

		ResultSet sqlResults1 = connection().executeQuery(sqlQuery1);
		ResultSet sqlResults2 = connection().executeQuery(sqlQuery2);
		if (sqlResults1.next() && sqlResults2.next()) {
			return true;
		}
		return false;
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

	public ArrayList<Room> getAllAvailableType(String type) {
		return Room.getAllAvailableType(type);
	}

	public ArrayList<Room> getAllAvailable() {
		return Room.getAllAvailable();
	}

	public boolean bookingProcess(int roomNum, Date checkIn, Date checkOut, String type, String ccNum, String expDate) {
		ArrayList<Room> available = ds.getRoom().getAllAvailableType(type);
		// TODO Add code to retrieve data from GUI. Then delete test data
		Room test = Room.getRoomFromDB(roomNum);

		int csc = 456;
		VirtualCCProcessor ccp = new VirtualCCProcessor(ccNum, expDate, csc);
		int token = ccp.hashCode();
		// TODO insert token into booking table
		return false;
	}

	/*
	 * Returns a list of open requests.
	 */
	public ArrayList<Request> checkRequests() {
		String query = "select * from requests where fullfilled = 0";
		try {
			ResultSet result = connection().executeQuery(query);
			ArrayList<Request> request = new ArrayList<>();
			while (result.next()) {
				Request temp = new Request();
				temp.setConf_id(result.getInt("req_ID"));
				temp.setFulfilled(true);
				temp.setItem_id(result.getInt("item_ID"));
				temp.setRequestDate(result.getDate("req_FulfillDate"));
				temp.setType(result.getString("req_Type"));
				temp.setReq_id(result.getInt("req_ID"));
				request.add(temp);
			}
			return request;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean checkIn() {
		return false;
		// TODO add code to check in
	}

	public boolean checkOut() {
		ds.getBooking().checkOut(ds.getRoom().getRoomNumber());
		ds.setBooking(null);
		ds.setRoom(null);
		ds.setUser(null);
		ds.setEmployee(null);
		return true;
	}

	public void createRequest(String type, Date reqDate, Date fulfillDate, int conf_id, int item_id) {
		String query = "insert into request(req_Type, req_DateTime, req_FulfillDate, conf_ID, item_ID values('" + type
				+ "', " + reqDate + "', " + fulfillDate + "', " + conf_id + "'," + item_id + "';";
		try {
			connection().execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean createCustomer(String fName, String lName, String email, String phone) {
		Account newAccount = new Account(fName, lName, phone, email);
		try {
			boolean isCreated = newAccount.createCustomer();
			return isCreated;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean createEmployee(String fName, String lName, String email, String phone, boolean admin) {
		Employee newEmp = new Employee(fName, lName, phone, email, admin);
		try {
			boolean isCreated = newEmp.createEmployee();
			return isCreated;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
