package Gambler;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * GamblerView class is a stage that includes all the view components that the gambler client see's.
 */
public class GamblerView extends Stage {

	private final int MAX_WIDTH_SIZE = 150;
	private Scene scene;
	private Scene scene2;
	private Scene scene3;
	private Text title;
	private Text title2;
	private Text errorText;
	private Text userTxt;
	private Text passTxt;
	private Text raceTxt;
	private Text carTxt;
	private Text balanceTxt;
	private Text betTxt;
	private TextField pickedRace;
	private TextField pickedCar;
	private HBox hButtons;
	private BackgroundImage bi;
	private Image image;
	private VBox vb;
	private BorderPane mainPane;
	private GridPane centerPane;
	private GridPane openPane;
	private GridPane confirmPane;

	ComboBox<String> racesBox;
	ComboBox<String> carsBox;
	ComboBox<String> carsBox1;
	ComboBox<String> carsBox2;
	ComboBox<String> carsBox3;
	TextField tfUser;
	TextField betField;
	TextField ballaneField;
	PasswordField passwordField;
	Button loginButton;
	Button signInButton;
	Button betButton;
	Button confirmButton;
	Button returnButton;
	Button exitButton;

	public GamblerView() {
		image = new Image("file://"+GamblerView.class.getClassLoader().getResource("Gambler/carRace.jpg").getPath());
		bi = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		ballaneField = new TextField("0");
		createButtons();
		createWelcomeView();
		this.setTitle("Gambler Interface");
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		
	}

	public void createButtons(){
		signInButton = new Button("New user");
		loginButton = new Button("Login");
		betButton = new Button("Bet");
		confirmButton = new Button("Confirm");
		returnButton = new Button ("Return");
		exitButton = new Button("Exit");
	}

	/**
	 * The first scene that the user see,include the signIn/Up action.
	 */
	public void createWelcomeView(){
		createUIControlls();
		createCenterPane();
		scene = new Scene(openPane,350,300);
		this.setScene(scene);
	}

	private void createUIControlls(){
		signInButton.setPadding(new Insets(5,5,5,5));
		loginButton.setPadding(new Insets(5,5,5,5));

		errorText = new Text();
		errorText.setFont(Font.font("Times New Roman", FontWeight.BOLD,FontPosture.ITALIC,21));
		errorText.setFill(Color.RED);

		passwordField = new PasswordField();
		tfUser = new TextField();

		title = new Text("Car Race Bets");
		title.setFont(Font.font("Times New Roman", FontWeight.BOLD,FontPosture.ITALIC,30));
		title.setFill(Color.ALICEBLUE);

		userTxt = new Text("User name");
		userTxt.setFill(Color.WHITE);
		passTxt = new Text("Password");
		passTxt.setFill(Color.WHITE);

		hButtons = new HBox();
		hButtons.setSpacing(10);
		hButtons.setAlignment(Pos.CENTER_RIGHT);
		hButtons.getChildren().addAll(signInButton,loginButton);
	}

	private void createCenterPane(){
		openPane = new GridPane();
		openPane.add(title, 0, 0, 2, 1);
		openPane.add(userTxt,0,1);
		openPane.add(tfUser,1,1);
		openPane.add(passTxt,0,2);
		openPane.add(passwordField,1,2);
		openPane.add(hButtons, 0, 3, 2, 1);
		openPane.add(errorText, 0,4,2,1);
		openPane.setHgap(10);
		openPane.setVgap(10);
		openPane.setPadding(new Insets(0,10,5,10));
		openPane.setAlignment(Pos.CENTER);
		openPane.setBackground(new Background(bi));
	}

	/**
	 * makes a red colored text to appear when there is a kind of a error.
	 */
	public void setErrorText(String str){
		errorText.setText(str);
	}

	/**
	 * In charge to change to the main scene after the success of the log/signIn operation.
	 */
	public void changeView(){
		createUIControlls2();
		createCenterPane2();
		scene2 = new Scene(mainPane,350,300);
		this.setScene(scene2);
	}

	/**
	 * creates the comboBoxes in the view and initialze the data inside.
	 * @param races race's names .
	 * @param cars1 cars names in race in thread 1.
	 * @param cars2 cars names in race in thread 2.
	 * @param cars3 cars names in race in thread 3.
	 */
	public void createComboBoxes(ArrayList<String> races,ArrayList<String> cars1,ArrayList<String> cars2,ArrayList<String> cars3){
		racesBox = new ComboBox<>(FXCollections.observableArrayList(races));
		racesBox.setValue(races.get(0));
		racesBox.setMaxWidth(MAX_WIDTH_SIZE);
		carsBox1 = new ComboBox<>(FXCollections.observableArrayList(cars1));
		carsBox1.setValue(cars1.get(0));
		carsBox1.setMaxWidth(MAX_WIDTH_SIZE);
		carsBox2 = new ComboBox<>(FXCollections.observableArrayList(cars2));
		carsBox2.setValue(cars2.get(0));
		carsBox2.setMaxWidth(MAX_WIDTH_SIZE);
		carsBox3 = new ComboBox<>(FXCollections.observableArrayList(cars3));
		carsBox3.setValue(cars3.get(0));
		carsBox3.setMaxWidth(MAX_WIDTH_SIZE);
		carsBox = carsBox1;
	}
	
	private void createUIControlls2(){
		errorText.setText("");
		betButton.setPrefWidth(100);
		exitButton.setPrefWidth(70);
		betField = new TextField();
		betField.setMaxWidth(MAX_WIDTH_SIZE);
		betField.setPromptText("0");
		
		ballaneField.setEditable(false);
		ballaneField.setMaxWidth(MAX_WIDTH_SIZE);

		raceTxt = new Text("Race id");
		raceTxt.setFill(Color.WHITE);
		carTxt = new Text("Car name");
		carTxt.setFill(Color.WHITE);
		balanceTxt = new Text("Your current balance");
		balanceTxt.setFill(Color.WHITE);
		betTxt = new Text("The amount of bet");
		betTxt.setFill(Color.WHITE);
	} 
	

	/**
	 * operate as a listener.
	 * <br> changes the car's names in the carBox comboBox according to the raceId.
	 * @param raceId race id number of the race.
	 */
	public void changeCarsBox(int raceId){
		if (raceId == 1)
			carsBox = carsBox1;
		else if (raceId == 2)
			carsBox = carsBox2;
		else
			carsBox = carsBox3;
	}
	
	private void createCenterPane2(){
		centerPane = new GridPane();
		centerPane.add(raceTxt,0,0);
		centerPane.add(racesBox,1,0);
		centerPane.add(carTxt,0,1);
		centerPane.add(carsBox,1,1);
		centerPane.add(betTxt,0,2);
		centerPane.add(betField,1,2);
		centerPane.add(balanceTxt,0,3);
		centerPane.add(ballaneField,1,3);
		centerPane.add(betButton, 0, 5);
		centerPane.add(exitButton, 1, 5);
		centerPane.setHgap(10);
		centerPane.setVgap(10);
		centerPane.setAlignment(Pos.CENTER);


		vb = new VBox();
		vb.getChildren().addAll(centerPane,betButton,exitButton);
		VBox.setMargin(betButton,new Insets(10,0,0,0));
		VBox.setMargin(exitButton,new Insets(10,0,0,0));
		vb.setAlignment(Pos.CENTER);

		mainPane = new BorderPane();
		mainPane.setCenter(vb);
		mainPane.setBackground(new Background(bi));
	}

	public void createConfirmView(){
		title2 = new Text("Bet Details");
		title2.setFont(Font.font("Times New Roman", FontWeight.BOLD,FontPosture.ITALIC,30));
		title2.setFill(Color.ALICEBLUE);
		pickedRace = new TextField(racesBox.getSelectionModel().getSelectedItem());
		pickedRace.setEditable(false);
		pickedRace.setMaxWidth(150);
		//pickedRace.setFill(Color.WHITE);
		pickedCar = new TextField(carsBox.getSelectionModel().getSelectedItem());
		pickedCar.setEditable(false);
		pickedCar.setMaxWidth(150);
		//pickedCar.setFill(Color.WHITE);
		createConfirmPane();
		scene3 = new Scene(confirmPane,350,300);
		this.setScene(scene3);
	}

	private void createConfirmPane(){
		betField.setEditable(false);
		betField.setFocusTraversable(false);
		confirmPane = new GridPane();
		confirmPane.add(title, 0, 0, 2, 1);
		confirmPane.add(raceTxt,0,1);
		confirmPane.add(pickedRace, 1, 1);
		confirmPane.add(carTxt,0,2);
		confirmPane.add(pickedCar, 1, 2);
		confirmPane.add(betTxt,0,3);
		confirmPane.add(betField, 1, 3);
		confirmPane.add(returnButton, 0,4);
		confirmPane.add(confirmButton,1, 4);
		confirmPane.setHgap(10);
		confirmPane.setVgap(10);
		confirmPane.setPadding(new Insets(0,10,5,10));
		confirmPane.setAlignment(Pos.CENTER);
		confirmPane.setBackground(new Background(bi));
	}

	/**
	 * Make's a pop out of a window with a error sign and a message.
	 * @param msg the message that will print in the window.
	 */
	public void errorAlert(String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(this);
		alert.setTitle("Error");
		alert.setContentText(msg);
		alert.show();
	}
	
	/**
	 * Make's a pop out of a window with a warning sign and a message.
	 * @param msg the message that will print in the window.
	 */
	public void warningAlert(String msg){
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(this);
		alert.setTitle("Warning");
		alert.setContentText(msg);
		alert.show();
	}
	

	
	
}
