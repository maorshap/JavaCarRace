package Gambler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *Holds all the data that is need by the gambler's app.
 *<br> The class is obtains the data from the connection with the server in which obatins the data from the database.
 */
public class GamblerModel{

	private final int NUMBER_OF_RACES = 3;
	private final String NEW_GAMBLER = "newGambler";
	private final String CHECK_USER = "checkUser";
	private final String INSERT_BET = "insertBet";
	private final String SET_BALANCE = "setUserBalance";
	private final String GET_CARS_NAMES = "getcarsName";
	private final String GET_WAITING_RACES = "getWaitingRaces";
	private final String GET_NUM_OF_RACES_ARR = "getNumOfRacesArr";
	private final String GET_RACE_READY = "getRaceReady";
	private final String GET_BALACE = "getBalance";
	private final String EXIT = "exit";
	private int balance;
	private int flag = 0;
	private  boolean isOnline = true;
	private String userName;
	private ArrayList<String> carsNames1 = new ArrayList<>();
	private ArrayList<String> carsNames2 = new ArrayList<>(); 
	private ArrayList<String> carsNames3 = new ArrayList<>(); 
	private ArrayList<String> racesNames = new ArrayList<>();
	private int[] numOfRaces = new int[NUMBER_OF_RACES];
	private Socket socket = null;
	private ObjectInputStream streamFromServer;
	private ObjectOutputStream streamToServer;

	DateFormat df = new SimpleDateFormat("dd/MM/yy-HH:mm:ss");
	Calendar calobj = Calendar.getInstance();
	Connection connection = null;
	Statement statement;
	String url = "jdbc:mysql://localhost/";
	String db = "CarRaceDB?useSSL=false";
	String driver = "com.mysql.jdbc.Driver";

	/**
	 * @param socket the socket which connects the gambler to the server.
	 */
	public GamblerModel(Socket socket){
		try{
			this.socket = socket;
			streamToServer = new ObjectOutputStream(socket.getOutputStream());
			streamToServer.flush();
			streamFromServer = new ObjectInputStream(socket.getInputStream());
			getCarsNames();
			getRacesNames();

		}
		catch (IOException e1) {
			if(isOnline)
				e1.printStackTrace();
		}
	}

	public Socket getSocket(){
		return socket;
	}
	
	public boolean isOnline(){
		return isOnline;
	}

	/**
	 * Sends a checking request to the server if the specific race have 3 invested cars.
	 * @param raceId the race id number of the wanted check race.
	 */
	public void getRaceReady(int raceId){
		GamblerMessage sentObject = new GamblerMessage();
		sentObject.setRaceId(raceId);
		try{
			streamToServer.writeUTF(GET_RACE_READY);
			streamToServer.flush();
			streamToServer.writeObject(sentObject);
			streamToServer.flush();
		}
		catch(IOException ex){
			if(isOnline)
				ex.printStackTrace();
		}
	}

	@SuppressWarnings("finally")
	public int[] getNumOfRaceArr(){
		try{
			streamToServer.writeUTF(GET_NUM_OF_RACES_ARR);
			streamToServer.flush();
			GamblerMessage sentObject = new GamblerMessage();
			streamToServer.writeObject(sentObject);
			streamToServer.flush();
			GamblerMessage receviedObject = (GamblerMessage) streamFromServer.readObject();
			for(int i = 0 ; i < NUMBER_OF_RACES ; i++){
				this.numOfRaces[i] = receviedObject.getNumOfRaceArr()[i];
			}
		}
		catch(IOException ex){
			if(isOnline)
				ex.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			if(isOnline)
				e.printStackTrace();
		}
		finally{
			return this.numOfRaces;
		}
	}

	/**
	 * Gets from the server all the waiting races names.
	 * @return ArrayList<String> with the races names data.
	 */
	public ArrayList<String> getRacesNames(){
		this.racesNames.clear();
		try {
			streamToServer.writeUTF(GET_WAITING_RACES);
			streamToServer.flush();
			GamblerMessage sentObject = new GamblerMessage();
			streamToServer.writeObject(sentObject);
			streamToServer.flush();
			GamblerMessage receviedObject = (GamblerMessage) streamFromServer.readObject();
			this.racesNames.addAll(receviedObject.getRaceNameList());
		}
		catch (ClassNotFoundException e) {
			if(isOnline)
				e.printStackTrace();
		}
		catch (IOException e) {
			if(isOnline)
				e.printStackTrace();
		}
		return racesNames;
	}

	public ArrayList<String> getRace1Cars(){
		return carsNames1;
	}

	public ArrayList<String> getRace2Cars(){
		return carsNames2;
	}

	public ArrayList<String> getRace3Cars(){
		return carsNames3;
	}

	/**
	 * Initiliaze inside carsNames1/2/3 ArrayLists the matching cars names of each race by the race number of thread.
	 */
	public void getCarsNames(){
		carsNames1.clear();
		carsNames2.clear();
		carsNames3.clear();
		try {
			streamToServer.writeUTF(GET_CARS_NAMES);
			streamToServer.flush();
			GamblerMessage sentObject = new GamblerMessage();
			streamToServer.writeObject(sentObject);
			streamToServer.flush();
			GamblerMessage receviedObject = (GamblerMessage) streamFromServer.readObject();
			carsNames1.addAll(receviedObject.getcarName1List());
			carsNames2.addAll(receviedObject.getcarName2List());
			carsNames3.addAll(receviedObject.getcarName3List());	
		}
		catch (IOException e) {
			if(isOnline)
				e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			if(isOnline)
				e.printStackTrace();
		}
	}

	/**
	 * Updates the current gambler balance account.
	 * @param money the amount of money that the gambler puts in the specific bet.
	 */
	public void drawMoney(int money){
		balance = balance - money;
		try {
			streamToServer.writeUTF(SET_BALANCE);
			streamToServer.flush();
			GamblerMessage sentObject = new GamblerMessage();
			sentObject.setBalance(balance);
			sentObject.setUserName(userName);
			streamToServer.writeObject(sentObject);
			streamToServer.flush();
		} 
		catch (IOException e) {
			if(isOnline)
				e.printStackTrace();
		}
	}

	/**
	 * Inserts all the necessary data for to approvement of the bet by the system.
	 * @param money the amount of money that the gambler puts in the specific bet.
	 * @param carName the name of the car who client puts his money on.
	 * @param RaceId the race id number of the race that the car in participant.
	 */
	public void completeBet(int money,String carName,int RaceId){
		try {
			streamToServer.writeUTF(INSERT_BET);
			streamToServer.flush();
			GamblerMessage sentObject = new GamblerMessage();
			sentObject.setUserName(userName);
			sentObject.setCarName(carName);
			sentObject.setMoney(money);
			sentObject.setRaceId(RaceId);
			streamToServer.writeObject(sentObject);
			streamToServer.flush();
		}
		catch (IOException e) {
			if(isOnline)
				e.printStackTrace();
		}
	}

	/**
	 * Checks if the user have a account in the system.
	 * @param userName the name of the user.
	 * @param password the password of the user.
	 * @return int 1 if the user in the system,else 0.
	 */
	@SuppressWarnings("finally")
	public int checkUser(String userName,String password){
		try{
			streamToServer.writeUTF(CHECK_USER);
			streamToServer.flush();
			GamblerMessage sentObject = new GamblerMessage();
			sentObject.setUserName(userName);
			sentObject.setPassword(password);
			streamToServer.writeObject(sentObject);
			streamToServer.flush();
			GamblerMessage receviedObject = (GamblerMessage) streamFromServer.readObject();
			this.flag = receviedObject.getFlag();
			if(flag == 1){
				this.userName = receviedObject.getUserName();
				this.balance = receviedObject.getBalance();
			}
		}
		catch(IOException ex){
			if(isOnline)
				System.out.println("The User name " + userName + " is not exists in the system ");
		}
		catch (ClassNotFoundException e) {
			if(isOnline)
				e.printStackTrace();
		}
		finally{
			return flag;
		}
	}


	/**
	 * Creating a new account to the current user.
	 * @see The process is success only if there is not another user in system with the same user name.
	 * @param userName the name of the user.
	 * @param password the password of the user.
	 * @return int 1 if the user in the system,else 0.
	 */
	@SuppressWarnings("finally")
	public int newUser(String userName,String password){
		try{
			streamToServer.writeUTF(NEW_GAMBLER);
			streamToServer.flush();
			GamblerMessage gm = new GamblerMessage();
			gm.setUserName(userName);
			gm.setPassword(password);
			streamToServer.writeObject(gm);
			streamToServer.flush();
			GamblerMessage receviedObject = (GamblerMessage) streamFromServer.readObject();
			this.flag = receviedObject.getFlag();
			this.userName =userName;

		}
		catch(IOException ex){
			if(isOnline)
				System.out.println("The User name " + userName + " is already exist in the system 216");
		}
		catch (ClassNotFoundException e) {
			if(isOnline)
				e.printStackTrace();
		}
		finally{
			return flag;
		}
	}

	/**
	 * Returns the current user balance from the sever->datbase.
	 * @return int user's balance.
	 */
	public int getBalance(){
		try {
			streamToServer.writeUTF(GET_BALACE);
			streamToServer.flush();
			GamblerMessage gm = new GamblerMessage();
			gm.setUserName(userName);
			streamToServer.writeObject(gm);
			streamToServer.flush();
			GamblerMessage receviedObject = (GamblerMessage) streamFromServer.readObject();
			this.balance = receviedObject.getBalance();

		}
		catch (IOException e) {
			if(isOnline)
				e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			if(isOnline)
				e.printStackTrace();
		}
		return balance;
	}

	/**
	 * Sends a disconnect annoucment to the server.
	 */
	public void exit(){
		try{
			isOnline = false;
			streamToServer.writeUTF(EXIT);
			streamToServer.flush();
			GamblerMessage gm = new GamblerMessage();
			gm.setUserName(userName);
			streamToServer.writeObject(gm);
			streamToServer.flush();
		}
		catch (IOException e) {
			if(isOnline)
				e.printStackTrace();
		}
	}

}
