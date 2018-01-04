
package CarRaceServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Database.QueriesWindow;
import Gambler.GamblerApp;
import RaceThread.CarRaceServerModel;
import RaceThread.RaceThread;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The main class of the CarRaceServer project.
 */
public class CarRace extends Application{ 

	private static int raceCounter = 0;
	private BackgroundImage bi;
	private Image image;
	private Button btnLanuch,btnNewGambler,btnQueries;
	private CarRaceServerWindow serverWindow;
	private ExecutorService executor;
	private CarRaceServerModel model;
	private GamblerApp gamblerApp;
	private static QueriesWindow queriesWindow;


	@Override
	public void start(Stage primaryStage) {	
		createServerWindow(primaryStage);
		setButtonsAction();
	}

	/**
	 * This functions builds the main server window including his content.
	 * @param primaryStage 
	 */
	public void createServerWindow(Stage primaryStage){
		image = new Image("file://"+CarRace.class.getClassLoader().getResource("CarRaceServer/datacenter.jpg").getPath());
		bi = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
		model = new CarRaceServerModel();
		serverWindow = new CarRaceServerWindow(model);
		serverWindow.show();
		executor = Executors.newFixedThreadPool(model.getNumberOfRaces());
		gamblerApp = new GamblerApp();
		btnLanuch = new Button("Lanuch Races");
		btnNewGambler = new Button("Gambler Window");
		btnNewGambler.setDisable(true);
		btnQueries = new Button("System Queries");
		btnQueries.setDisable(true);

		BorderPane pane = new BorderPane();
		HBox hb = new HBox();
		hb.setSpacing(5.0);
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(btnLanuch,btnQueries,btnNewGambler);
		pane.setBackground(new Background(bi));
		pane.setCenter(hb);
		Scene scene = new Scene(pane,450,100);
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.setTitle("CarRaceServer"); // Set the stage title
		primaryStage.show(); // Display the stage
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){ @Override
			public void handle(WindowEvent event) {	
			try{ 
				if(queriesWindow != null)
					queriesWindow.closeWindow();
				serverWindow.closeConnection();
				executor.shutdownNow();
				Platform.exit();
				System.exit(0);
			} 
			catch (Exception e){
				e.printStackTrace();
			}
		}
		});
	}
	/**
	 * This functions set the buttons components action.
	 */
	public void setButtonsAction(){
		btnNewGambler.setOnAction(e->{
			gamblerApp.createGamblerWindow();
		});

		btnQueries.setOnAction(e->{
			queriesWindow = new QueriesWindow(model);
			queriesWindow.createWindow();
		});

		btnLanuch.setOnAction(new EventHandler<ActionEvent>(){ @Override
			public void handle(ActionEvent event) {	
			for(int i = 0 ; i < model.getNumberOfRaces() ; i++){
				RaceThread race = new RaceThread(++raceCounter,model,i+1);
				executor.execute(race);
			}
			btnLanuch.setDisable(true);
			btnNewGambler.setDisable(false);
			btnQueries.setDisable(false);
		}
		});
	}
	
	/**
	 * This function refresh the contents of the window each time a race is over.
	 */
	public static void refreshQueriesWindow(){
		if (queriesWindow != null)
			queriesWindow.refreshData();

	}

	public static void main(String[] args){	
		launch(args);
	}

}

