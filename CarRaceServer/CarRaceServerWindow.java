package CarRaceServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import RaceThread.CarRaceServerModel;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This class is the server's log.
 * <br> Charges on connecting the server and preparement to receives new clients(Race threads).
 */
public class CarRaceServerWindow extends Stage{ 

	private static int winnerCar;
	private static final int CAR_RACE_SERVER_PORT = 8000;
	private static final int GAMBLER_SERVER_PORT = 8888;
	private Scene scene;
	private ServerSocket serverSocket,gamblerSocket;
	private CarRaceServerModel serverModel;
	private int gamblerNumber,raceNumber;
	private static TextArea ta;
	private ArrayList<Socket> socketList = new ArrayList<>();
	
	/**
	 * Builds the server log contents.
	 * @param model the class that holds all the data and connect to the database.
	 */
	public CarRaceServerWindow(CarRaceServerModel model){
		this.serverModel = model;
		ta = new TextArea();
		ta.setEditable(false);
		ta.setWrapText(true);
		scene = new Scene(ta);

		this.setScene(scene);
		this.setTitle("CarRaceServer");
		this.setX(0);
		this.setY(0);
		this.setHeight(700);
		this.setWidth(680);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setOnCloseRequest(new EventHandler<WindowEvent>(){
			public void handle(WindowEvent e){
				try {
					serverSocket.close();
					Platform.exit();
					System.exit(0);
				} 
				catch (IOException e1) {
					e1.printStackTrace();
				}
				finally{
					Platform.exit();
					System.exit(0);
				}
			}
		});
		setUpCarRaceServer();
		setUpGamblerServer(serverModel);
	}

	/**
	 * Creates a Server socket and handle a new Clients requests by opening a new threads for each race.
	 */
	public void setUpCarRaceServer(){
		new Thread(new Runnable(){
			public void run(){
				raceNumber = 0;
				try {
					serverSocket = new ServerSocket(CAR_RACE_SERVER_PORT);
					Platform.runLater(new Runnable(){
						public void run(){
							printMsg("CarRace Server started at " + new java.util.Date());
						}
					});

					while(true){
						Integer inte = new Integer(raceNumber+1);
						raceNumber++;
						Socket socket = serverSocket.accept();
						InetAddress clientInfo = socket.getInetAddress();
						Platform.runLater(()->{
							printMsg("Statring a thread for a Race " + inte + " at " + new java.util.Date());
							printMsg("\tRace " + inte + "'s host name is -> " + clientInfo.getHostName());
							printMsg("\tRace " + inte + "'s IP address is -> " + clientInfo.getHostAddress());
						});
						socketList.add(socket);
						new Thread(new CarRaceHandler(socket,inte)).start();

					}
				}
				catch (IOException e) {
				}
			}
		}).start();
	}
	
	/**
	 * Creates a Gambler socket and handles a new gamblers clients(new User/exists user).
	 * @param model the class that holds all the data and connect to the database.
	 */
	public void setUpGamblerServer(CarRaceServerModel model){
		new Thread(new Runnable(){
			public void run(){
				raceNumber = 0;
				try {
					gamblerSocket = new ServerSocket(GAMBLER_SERVER_PORT);
					Platform.runLater(new Runnable(){
						public void run(){
							printMsg("Gambler's Server started at " + new java.util.Date());
						}
					});

					while(true){
						Integer inte = new Integer(gamblerNumber+1);
						gamblerNumber++;
						Socket socket = gamblerSocket.accept();
						InetAddress clientInfo = socket.getInetAddress();
						Platform.runLater(()->{
							printMsg("Statring a thread for a Gambler " + inte + " at " + new java.util.Date());
							printMsg("\tGambler" + inte + "'s host name is -> " + clientInfo.getHostName());
							printMsg("\tGambler " + inte + "'s IP address is -> " + clientInfo.getHostAddress());
						});
						socketList.add(socket);
						new Thread(new GamblerHandler(socket,serverModel)).start();

					}
				}
				catch (IOException e) {
				}
			}
		}).start();
	}

	/**
	 * Return the winner car id.
	 * @return winnerCar the winner car id number.
	 */
	public int getWinnerCarId(){
		return winnerCar;
	}
	
	/**
	 * Set the current winning car Id.
	 * @param id the winner car id number.
	 */
	public static void setWinnerCarId(int id){
		winnerCar = id;
	}

	/**
	 * Close the System Connection.
	 */
	public void closeConnection(){
		try {
			for (Socket s : socketList)
				s.close();
			serverSocket.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Prints in the server's log the new message.
	 * @param str the message to prints into the server log.
	 */
	public static void printMsg(String str){
		ta.appendText(str+"\n");
	}

}

