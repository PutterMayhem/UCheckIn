package guiclasses;

import java.io.IOException;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import objectclasses.Controller;
import objectclasses.Request;


public class MakeRequestGUI extends Application implements Initializable{
	
	@FXML
	private TableView<ServiceTable> table_room;
	@FXML
	private TableView<FoodTable> table_food;
	@FXML
	private Label label_welcome;
	@FXML
	private Button btn_back;
	@FXML
	private Button btn_submit;
	@FXML
	private TableColumn<ServiceTable, String> col_namefree;
	@FXML
	private TableColumn<FoodTable, String> col_namefood;
	@FXML
	private TableColumn<FoodTable, String> col_price;
	@FXML
	private TableColumn<ServiceTable, CheckBox> col_select;
	@FXML
	private TableColumn<FoodTable, CheckBox> col_selectfood;
	@FXML
    private Button btn_cancel;
    @FXML
    private TableView<RequestTable> table_request;
    @FXML
    private TableColumn<RequestTable, String> col_reqID;
    @FXML
    private TableColumn<RequestTable, String> col_request;
    @FXML
    private TableColumn<RequestTable, Date> col_time;
    @FXML
    private TableColumn<RequestTable, String> col_status;
    @FXML
    private TableColumn<RequestTable, CheckBox> col_cancel;
    @FXML
    private Label label_total;
    @FXML 
    private Button btn_refresh;
	private Controller control = Controller.getInstance();
	private static float totalprice;
	private static int confNum;
	
	
	ObservableList<ServiceTable> servicelist = FXCollections.observableArrayList();
	ObservableList<FoodTable> foodlist = FXCollections.observableArrayList();
	ObservableList<RequestTable> requestlist = FXCollections.observableArrayList();
	
	@Override
	public void start(Stage primary) throws Exception {
		// TODO Auto-generated method stub
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MakeRequest.fxml"));
		try {
			Parent root = loader.load();
			Scene scene = new Scene(root, 1920, 1080);
			primary.setTitle("Make Request");
			primary.setScene(scene);
			primary.show();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public Scene getScene() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MakeRequest.fxml"));
		loader.setController(this);
		try {
			Parent root = loader.load();
			Scene scene = new Scene(root, 1920, 1080);
			return scene;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setInformation(Controller control) {
		this.control = control;
		label_welcome.setText("Make a Request for Room " + control.getRoom().getRoomNumber());
		label_total.setText("Total: $" + totalprice);
		MakeRequestGUI.confNum = control.getBooking().getConfNum();
		MakeRequestGUI.totalprice = control.getAmountOwed();
		System.out.println("test " + control.getBooking().getConfNum());
	}
	
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		btn_submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Stage primary = (Stage) ((Node) event.getSource()).getScene().getWindow();
				// TODO Auto-generated method stub
				ArrayList<Integer> items = new ArrayList<Integer>();
				servicelist.forEach((service) -> {
					if(service.getSelect().isSelected()) {
						String query = "Select * FROM Items WHERE item_Name = '" + service.getName() + "';";
						try {
							ResultSet result = connection().executeQuery(query);
							result.next();
							items.add(result.getInt("item_ID"));
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				foodlist.forEach((food) -> {
					if(food.getSelect().isSelected()) {
						String query = "Select * FROM Items WHERE item_Name = '" + food.getName() + "';";
						try {
							ResultSet result = connection().executeQuery(query);
							result.next();
							items.add(result.getInt("item_ID"));
							totalprice += result.getFloat("item_price");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
				int reqID = Request.createRequest(confNum);
				items.forEach((item) -> {
					Request.createRequestItem(reqID, item);
				});
				control.setAmountOwed(totalprice);
				System.out.println(totalprice);
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setContentText("Requests Have Been Submitted!");
				alert.setTitle("Success!");
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.initOwner(primary);
				alert.showAndWait();
				MakeRequestGUI mr  = new MakeRequestGUI();
				Scene mrs = mr.getScene();
				mr.setInformation(control);
				primary.setScene(mrs);
				primary.show();
				primary.setFullScreen(true);
			}
			
		});
		
		btn_cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				Stage primary = (Stage) ((Node) event.getSource()).getScene().getWindow();
				requestlist.forEach((request) -> {
					if((request.getSelect() != null) & (request.getSelect().isSelected())) {
						String q = "SELECT * FROM RequestItems ri INNER JOIN Items i ON ri.item_ID = i.item_ID "
								+ "WHERE reqitem_ID = " + request.getReqItemID();
						String query = "DELETE FROM RequestItems WHERE reqitem_ID = " + request.getReqItemID();
						try {
							ResultSet rs = connection().executeQuery(q);
							rs.next();
							int i = rs.getInt("req_ID");
							float price = rs.getFloat("item_price");
							rs.close();
							connection().executeUpdate(query);
							totalprice -= price;
							String q2 = "SELECT * FROM RequestItems WHERE req_ID = " + i;
							ResultSet rs2 = connection().executeQuery(q2);
							if (!rs2.next()) {
								Request.deleteRequest(i);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				control.setAmountOwed(totalprice);
				System.out.println(totalprice);
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setContentText("Requests have been canceled");
				alert.setTitle("Success!");
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.initOwner(primary);
				alert.showAndWait();
				MakeRequestGUI mr  = new MakeRequestGUI();
				Scene mrs = mr.getScene();
				mr.setInformation(control);
				primary.setScene(mrs);
				primary.show();
				primary.setFullScreen(true);
			}
			
		});
		
		btn_back.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					LoggedInGUI loggedin  = new LoggedInGUI();
					Scene loggedInScene = loggedin.getScene();
					loggedin.setInformation(control);
					Stage primary = (Stage) ((Node) event.getSource()).getScene().getWindow();
					primary.setScene(loggedInScene);
					primary.show();
					primary.setFullScreen(true);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		btn_refresh.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Stage primary = (Stage) ((Node) event.getSource()).getScene().getWindow();
				MakeRequestGUI mr  = new MakeRequestGUI();
				Scene mrs = mr.getScene();
				mr.setInformation(control);
				primary.setScene(mrs);
				primary.show();
				primary.setFullScreen(true);
			}
		});
		
		try {
			
			String query1 = "SELECT * FROM Items WHERE item_price = 0";
			ResultSet rs = connection().executeQuery(query1);
			while(rs.next()) {
				servicelist.add(new ServiceTable(rs.getString("item_Name"), new CheckBox())); 
			}
			rs.close();
			table_food.setPlaceholder(null);
			String query2 = "SELECT * FROM Items WHERE item_price > 0";
			ResultSet rs1 = connection().executeQuery(query2);
			while(rs1.next()) {
				String price = String.format("%.2f",rs1.getFloat("item_price"));
				foodlist.add(new FoodTable(rs1.getString("item_Name"), "$" + price, new CheckBox()));
			}
			rs1.close();
			String query3 = "SELECT r.conf_ID, ri.reqitem_ID, r.req_ID, i.item_Name, r.req_DateTime, ri.fulfilled FROM Request r " + 
					"INNER JOIN RequestItems ri ON r.req_ID = ri.req_ID " + 
					"INNER JOIN Items i ON i.item_ID = ri.item_ID " + 
					"WHERE conf_ID = " + confNum + " ORDER BY ri.fulfilled";
			System.out.println(confNum);
			ResultSet rs2 = connection().executeQuery(query3);
			while(rs2.next()) {
				String temp = null;
				if (rs2.getInt("fulfilled") == 0) {
					temp = "Pending";
					requestlist.add(new RequestTable(String.valueOf(rs2.getInt("reqitem_ID")), rs2.getString("item_Name"), rs2.getDate("req_DateTime"), temp, new CheckBox())); 
				} else if (rs2.getInt("fulfilled") == 1) {
					temp = "Completed";
					requestlist.add(new RequestTable(String.valueOf(rs2.getInt("reqitem_ID")), rs2.getString("item_Name"), rs2.getDate("req_DateTime"), temp, null)); 
				}
				
			}
			rs2.close();
		
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		col_reqID.setCellValueFactory(new PropertyValueFactory<>("reqItemID"));
		col_request.setCellValueFactory(new PropertyValueFactory<>("name"));
		col_time.setCellValueFactory(new PropertyValueFactory<>("requestTime"));
		col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
		col_cancel.setCellValueFactory(new PropertyValueFactory<>("select"));
		col_namefree.setCellValueFactory(new PropertyValueFactory<>("name"));
		col_namefood.setCellValueFactory(new PropertyValueFactory<>("name"));
		col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
		col_select.setCellValueFactory(new PropertyValueFactory<>("select"));
		col_selectfood.setCellValueFactory(new PropertyValueFactory<>("select"));
		table_room.setItems(servicelist);
		table_food.setItems(foodlist);
		table_request.setItems(requestlist);
	}

}
