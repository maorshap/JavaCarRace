package Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import RaceThread.CarRaceServerModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Charges to perform queries options and create's there table result.
 * <br>Suppose to be in use only by the maintains workers of the system.
 */
public class QueriesWindow extends Stage {

	private final String str1 = "Cars_Without_Investment";
	private final String str2 = "Race_Gamblers";
	private final String str3 = "Cars_With_Investment";
	private final String str4 = "Races";
	private final String str5 = "Gamblers";
	private final String str6 = "All_Races";
	private final String str8 = "Cars_in_the_Race";
	private final String str10 = "Race_Gamblers";
	private final String str11 = "all_Races_Data";
	private final String str12= "Gambler_Past_Bets";
	private final String str13 = "Participate_In_Races";
	private final int COLUMN_MIN_WIDTH = 212;
	private final int SCENE_WIDTH = 850;
	private final int SCENE_HEIGHT = 550;
	
	private CarRaceServerModel model;
	private Text titleTxt,statisticsTxt,presentTxt,historyTxt; 
	private BackgroundImage bi;
	private Image image;
	private Button btnSystemProfit = new Button("Show system's profit");
	private Button btnUsersBalance = new Button("Show all users balance");
	private Button btnAllBets = new Button("Show all system's bets");
	private Button btnPresent= new Button("Show Contents");
	private Button btnHistory = new Button("Show Contents");
	private ComboBox<String> presentCB1 = new ComboBox<>();
	private ComboBox<String> presentCB2 = new ComboBox<>();
	private ComboBox<String> historyCB1 = new ComboBox<>();
	private ComboBox<String> historyCB2 = new ComboBox<>();
	private ComboBox<String> historyCB3 = new ComboBox<>();
	private ArrayList<String> historyCB2List;
	private HBox hBStatistic = new HBox(5);
	private HBox hBPresent = new HBox(5);
	private HBox hBHistory = new HBox(5);
	private HBox hBTitle = new HBox(5);
	private VBox bottomPane = new VBox(5);
	private BorderPane mainPane = new BorderPane();
	private TableView<?> tableView = new TableView<Object>();

	/**
	 * Each query window gets the model object that is used for to access to the system database.
	 * <br> Changes the comboBoxes content according to the user choices in the comboBoxes in the specific row.
	 * <br>Fires queries according to the user choices in the comboBoxes in the suitable row of the pushed button.
	 * @param model the class that hold all the data of the system and communicate with the database.
	 */
	public QueriesWindow(CarRaceServerModel model){
		this.model = model;
		image = new Image("file://"+ CarRaceServerModel.class.getClassLoader().getResource("CarRaceServer/ServerQueries.jpg").getPath());
		bi = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

		titleTxt = new Text("Server Queries");
		titleTxt.setFont(Font.font("Times New Roman", FontWeight.BOLD,FontPosture.ITALIC,30));
		titleTxt.setFill(Color.ALICEBLUE);
		statisticsTxt = new Text("System Statistics :");
		statisticsTxt.setFont(Font.font("Times New Roman", FontWeight.BOLD,FontPosture.ITALIC,20));
		statisticsTxt.setFill(Color.ALICEBLUE);
		presentTxt = new Text("Present Queries :");
		presentTxt.setFont(Font.font("Times New Roman", FontWeight.BOLD,FontPosture.ITALIC,20));
		presentTxt.setFill(Color.ALICEBLUE);
		historyTxt = new Text("History Queries :");
		historyTxt.setFont(Font.font("Times New Roman", FontWeight.BOLD,FontPosture.ITALIC,20));
		historyTxt.setFill(Color.ALICEBLUE);

		hBStatistic.getChildren().addAll(statisticsTxt,btnSystemProfit,btnUsersBalance,btnAllBets);
		hBStatistic.setSpacing(50);
		hBStatistic.setAlignment(Pos.CENTER_LEFT);
		Platform.runLater(()->{
			hBStatistic.setStyle("-fx-padding: 10;" + 
					"-fx-border-style: solid inside;" + 
					"-fx-border-width: 2;" + 
					"-fx-border-radius: 5;" + 
					"-fx-border-color: white;");
		});
		hBPresent.getChildren().addAll(presentTxt,presentCB1,presentCB2,btnPresent);
		hBPresent.setSpacing(50);
		hBPresent.setAlignment(Pos.CENTER_LEFT);
		Platform.runLater(()->{
			hBPresent.setStyle("-fx-padding: 10;" + 
					"-fx-border-style: solid inside;" + 
					"-fx-border-width: 2;" + 
					"-fx-border-radius: 5;" + 
					"-fx-border-color: white;");
		});
		hBHistory.getChildren().addAll(historyTxt,historyCB1,historyCB2,historyCB3,btnHistory);
		hBHistory.setSpacing(9);
		hBHistory.setAlignment(Pos.CENTER_LEFT);
		Platform.runLater(()->{
			hBHistory.setStyle("-fx-padding: 10;" + 
					"-fx-border-style: solid inside;" + 
					"-fx-border-width: 2;" + 
					"-fx-border-radius: 5;" + 
					"-fx-border-color: white;");
		});
		hBTitle.getChildren().add(titleTxt);
		hBTitle.setAlignment(Pos.CENTER);
		bottomPane.getChildren().addAll(hBStatistic,hBPresent,hBHistory);

		mainPane.setBackground(new Background(bi));
		mainPane.setTop(hBTitle);
		mainPane.setCenter(tableView);
		mainPane.setBottom(bottomPane);

		Scene scene = new Scene(mainPane,SCENE_WIDTH,SCENE_HEIGHT);
		this.setScene(scene);
		this.setResizable(false);
		this.setTitle("Queries Window");
		this.setAlwaysOnTop(true);
		setPresentCB1Data();
		setPresentCB2Data();
		setHistoryCB1Data();
		setHistoryRacesCBData();
		setHistoryAllRacesCBData();

		historyCB1.valueProperty().addListener(e->{
			if (historyCB1.getSelectionModel().getSelectedItem().equals(str4)){
				setHistoryRacesCBData();
				setHistoryAllRacesCBData();
			}
			else{
				setHistoryGamblerCBData();
			}
		});

		historyCB2.valueProperty().addListener(e->{
			Platform.runLater(()->{
				if(historyCB1.getSelectionModel().getSelectedItem().equals(str4)){
					if(historyCB2.getSelectionModel().getSelectedItem().equals(str6))
						setHistoryAllRacesCBData();
					else{
						setHistoryRaceIdCBData();
					}
				}
			});

		});

		btnSystemProfit.setOnAction(e->{
			String query = model.getQueriesObject().systemProfitQuery();
			try {
				ResultSet rs = model.getStatement().executeQuery(query);
				populateTableView(rs, tableView);
			}
			catch (SQLException e1) {
			}
		});

		btnUsersBalance.setOnAction(e->{
			String query = model.getQueriesObject().usersBalanceQuery();
			try {
				ResultSet rs = model.getStatement().executeQuery(query);
				populateTableView(rs, tableView);
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		btnAllBets.setOnAction(e->{
			String query = model.getQueriesObject().getAllBets();
			try{
				ResultSet rs = model.getStatement().executeQuery(query);
				populateTableView(rs, tableView);
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		btnPresent.setOnAction(e->{
			;//From RaceIdX to X
			String requestedQuery = presentCB2.getSelectionModel().getSelectedItem();
			String query = null;
			try {
				switch(requestedQuery){
					case str1: query = model.getQueriesObject().carsWithoutInvestmentQuery(presentCB1.getSelectionModel().getSelectedItem());
					break;

					case str2: query = model.getQueriesObject().getRaceGamblersQuery(presentCB1.getSelectionModel().getSelectedItem());
					break;

					case str3: query = model.getQueriesObject().carsWithInvestmentQuery(presentCB1.getSelectionModel().getSelectedItem());
					break;
				}
				ResultSet rs = model.getStatement().executeQuery(query);
				populateTableView(rs, tableView);
			} 
			catch (SQLException e1) {
				e1.printStackTrace();
			}
		});


		btnHistory.setOnAction(e->{
			String requestedQuery = historyCB3.getSelectionModel().getSelectedItem();
			String query = null;
			switch(requestedQuery){
				case str8: query = model.getQueriesObject().getCarsFromPast(historyCB2.getSelectionModel().getSelectedItem());
				break;

				case str10:query = model.getQueriesObject().getPastRaceGamblers(historyCB2.getSelectionModel().getSelectedItem());
				break;

				case str11:query = model.getQueriesObject().getAllPastRacesDetails();
				break;

				case str12:query = model.getQueriesObject().getGamblerPastBets(historyCB2.getSelectionModel().getSelectedItem());
				break;

				case str13:query = model.getQueriesObject().getGamblerPastRaces(historyCB2.getSelectionModel().getSelectedItem());
				break;

			}
			try {
				populateTableView(model.getStatement().executeQuery(query), tableView);
			} 
			catch (SQLException e1) {
			}
		});
	}

	public void refreshData(){
		setPresentCB1Data();
		if(historyCB1.getSelectionModel().getSelectedItem().equals(str4))
			setHistoryRacesCBData();
	}

	public void createWindow(){
		this.show();
	}

	public void closeWindow(){
		this.close();
		Platform.exit();
	}

	/**
	 * @return the names of races that has finished.
	 */
	@SuppressWarnings("finally")
	public ResultSet getPastRaces(){
		ResultSet rs = null;
		try {
			rs = model.getStatement().executeQuery(model.getQueriesObject().getRacesNames());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			return rs;
		}
	}

	/**
	 * @return the id numbers of the races that are waiting to start.
	 */
	@SuppressWarnings("finally")
	public ResultSet getRacesId(){
		ResultSet rs = null;
		try {
			rs = model.getStatement().executeQuery(model.getQueriesObject().getWaitingRace());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			return rs;
		}
	}


	/**
	 * @return the user's names of all the system users from the beginning of the system server.
	 */
	@SuppressWarnings("finally")
	public ResultSet getGamblersName(){
		ResultSet rs = null;
		try {
			rs = model.getStatement().executeQuery(model.getQueriesObject().getUsersNames());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			return rs;
		}
	}

	public void setPresentCB1Data(){
		try {
			presentCB1.getItems().clear();
			ResultSet rs = getRacesId();
			while (rs.next()){ 
				presentCB1.getItems().add("RaceId"+rs.getInt(1));
			}
			presentCB1.getSelectionModel().selectFirst();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setPresentCB2Data(){
		presentCB2.getItems().add(str1);
		presentCB2.getItems().add(str2);
		presentCB2.getItems().add(str3);
		presentCB2.getSelectionModel().selectFirst();
	}

	public void setHistoryCB1Data(){
		String[] tempArr = {str4,str5};
		historyCB1.getItems().addAll(FXCollections.observableArrayList(tempArr));
		historyCB1.getSelectionModel().selectFirst();
	}

	public void setHistoryRacesCBData(){
		historyCB2.getItems().clear();
		historyCB2List = new ArrayList<>();
		try {
			ResultSet rs = getPastRaces();
			while (rs.next()){ 
				historyCB2List.add(rs.getString(1));
			}
			historyCB2List.add(str6);
			historyCB2.getItems().addAll(FXCollections.observableArrayList(historyCB2List));
			historyCB2.getSelectionModel().selectLast();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setHistoryGamblerCBData(){
		historyCB2.getItems().clear();
		ResultSet rs = getGamblersName();
		try{
			while (rs.next()){ 
				historyCB2.getItems().add(rs.getString(1));
			}
			historyCB2.getSelectionModel().selectFirst();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		historyCB3.getItems().clear();
		historyCB3.getItems().add(str12);
		historyCB3.getItems().add(str13);
		historyCB3.getSelectionModel().selectFirst();
	}

	public void setHistoryRaceIdCBData(){
		historyCB3.getItems().clear();
		historyCB3.getItems().add(str8);
		historyCB3.getItems().add(str10);
		historyCB3.getSelectionModel().selectFirst();
	}

	public void setHistoryAllRacesCBData(){
		historyCB3.getItems().clear();
		historyCB3.getItems().add(str11);
		historyCB3.getSelectionModel().selectFirst();
	}

	/**
	 * Populates the table according to the queries that has been asked (by the ResultSet).
	 * @param rs the resultset that created by the query to the server database.
	 * @param tableView the table that will hold the data of the resultSet.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void populateTableView(ResultSet rs, TableView tableView){
		tableView.getColumns().clear();
		ObservableList<ObservableList> data =  FXCollections.observableArrayList();
		tableView.setItems(data);
		try{
			int numOfColumns = rs.getMetaData().getColumnCount();//obtain the number of columns from specific table.
			for(int i = 1; i <= numOfColumns ; i++){
				TableColumn column = new TableColumn(rs.getMetaData().getColumnName(i));//Create a column with appropriate name.
				column.setStyle( "-fx-alignment: CENTER;");
				column.setMinWidth(COLUMN_MIN_WIDTH);
				final int columNum = i - 1;
				column.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
						if (param.getValue().get(columNum) == null)
							return new SimpleStringProperty("");
						else
							return new SimpleStringProperty(param.getValue().get(columNum).toString());
					}
				});

				tableView.getColumns().addAll(column);
			}
			while(rs.next()){
				ObservableList row = FXCollections.observableArrayList();
				for(int i = 1 ; i<= numOfColumns ;i++)
					row.add(rs.getString(i));
				data.add(row);
			}

		} 
		catch (Exception e){
			e.printStackTrace();
			System.out.println("Error on Building Data");
		}
	}

}
