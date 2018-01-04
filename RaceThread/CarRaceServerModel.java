package RaceThread;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import CarRaceServer.EndOfRaceCar;
import CarRaceServer.RaceResultPane;
import Database.Queries;
import Gambler.GamblerApp;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Hold the system's data and connects with database.
 */
public class CarRaceServerModel{

	Connection connection = null;
	Statement statement;
	String url = "jdbc:mysql://localhost/";
	String db = "CarRaceDB?useSSL=false";
	String driver = "com.mysql.jdbc.Driver";

	private final int NUMBER_OF_CARS = 5;
	private final int NUMBER_OF_RACE_SONGS = 4;
	private final int NUMBER_OF_RACES = 3;
	private final int MAX_SPEED = 15;
	private final int SPEED_CHANGE_RATE = 30;
	private final String DBFile = "CarRaceDBScript.sql";
	private final String UPDATE_SERVER = "UPDATE_SERVER";

	private static int numberOfRace = 3;//the number of races threads since the beginning of the system.
	private static Lock lock = new ReentrantLock();
	private static Lock lockRace = new ReentrantLock();
	private static Condition raceReady1 = lock.newCondition();
	private static Condition raceReady2 = lock.newCondition();
	private static Condition raceReady3 = lock.newCondition();


	private boolean isRaceRunning = false;
	private boolean[] isRaceIdReady = {false,false,false};
	private CarProperties.Color colorOptions[] = CarProperties.Color.values();
	private CarProperties.Size sizeOption[] = CarProperties.Size.values();
	private CarProperties.Type typeOption[] = CarProperties.Type.values();
	private ArrayList<Runnable> threadsList = new ArrayList<>();
	private PreparedStatement isRaceReady1,isRaceReady2,isRaceReady3,checkCashStatement,setWaitingRacesStatement,setUserProfitStatement,numOfLosserStatemnt,
	numOfWinnersStatement,getBalanceStatemnt,setBalanceStatement,setRaceResultStatement;
	private CarPane[][] carsPanes = new CarPane[NUMBER_OF_RACES][NUMBER_OF_CARS];
	private Car[][] cars = new Car[NUMBER_OF_RACES][NUMBER_OF_CARS];
	private Socket[] sockets = new Socket[NUMBER_OF_RACES];
	private CarRaceServerView[] views = new CarRaceServerView[NUMBER_OF_RACES];
	private ArrayList<Lock> threadsLocks = new ArrayList<>();//list of all the locks of the waiting-to-start races threads.
	private ArrayList<Condition> threadsConditions = new ArrayList<>();//list of all locks conditions of the waiting-to-start races threads.
	private int numOfRaceArr[] = new int[3];//keeps the unique number of each waiting-to-start races.
	private Queries queries;
	private String currentRaceName;
	private ObjectOutputStream streamToServer;

	private MediaPlayer carStartAudio = new MediaPlayer(new Media("file://" + CarRaceServerModel.class.getClassLoader().getResource("RaceThread/CarStart.mp3").getPath()));
	private MediaPlayer startRaceAudio = new MediaPlayer(new Media("file://" + CarRaceServerModel.class.getClassLoader().getResource("RaceThread/startRace.mp3").getPath()));
	private MediaPlayer[] mediaPlayers = {carStartAudio,startRaceAudio,null};
	private MediaPlayer raceAudio1 = new MediaPlayer(new Media("file://" + CarRaceServerModel.class.getClassLoader().getResource("RaceThread/Song1.mp3").getPath()));
	private MediaPlayer raceAudio2 = new MediaPlayer(new Media("file://" + CarRaceServerModel.class.getClassLoader().getResource("RaceThread/Song2.mp3").getPath()));
	private MediaPlayer raceAudio3 = new MediaPlayer(new Media("file://" + CarRaceServerModel.class.getClassLoader().getResource("RaceThread/Song3.mp3").getPath()));
	private MediaPlayer raceAudio4 = new MediaPlayer(new Media("file://" + CarRaceServerModel.class.getClassLoader().getResource("RaceThread/Song4.mp3").getPath()));
	private MediaPlayer[] raceAudios = {raceAudio1,raceAudio2,raceAudio3,raceAudio4};

	public CarRaceServerModel(){	
		initializeDB();//Connect to the Database
		mediaPlayers[mediaPlayers.length-1] = raceAudios[(int)Math.floor(Math.random()*NUMBER_OF_RACE_SONGS)];//generate a random race song from the race_song_list.
		for(int i = 0 ; i < NUMBER_OF_RACES ; i++){
			threadsList.add(null);
			threadsLocks.add(null);
			threadsConditions.add(null);
		}
	}

	/**
	 * Gets a object that holds all the queries of the carRace system project.
	 * @return Queries object that has a functions that makes queries to the database.
	 */
	public Queries getQueriesObject(){
		return queries;
	}

	public Statement getStatement(){
		return this.statement;
	}

	/**
	 * Gets a array that hold's a unique race number's according to the race's thread number.
	 * @return array with number of each race according to his number of thread - 1.
	 */
	public int[] getNumOfRaceArr(){
		return numOfRaceArr;
	}

	public ArrayList<Runnable> getThreadsList(){
		return threadsList;
	}

	public Lock getLock(){
		return lock;
	}

	/**
	 * Return the race thrad lock's condition
	 * @return Condition raceReady1
	 */
	public Condition getRaceReady1(){
		return raceReady1;
	}

	/**
	 * Return the race thrad lock's condition
	 * @return Condition raceReady2
	 */
	public Condition getRaceReady2(){
		return raceReady2;
	}

	/**
	 * Return the race thrad lock's condition
	 * @return Condition raceReady3
	 */
	public Condition getRaceReady3(){
		return raceReady3;
	}

	public void setThreadView(CarRaceServerView view,int numOfThread){
		views[numOfThread-1] = view;
	}

	public void setThreadLock(Lock lock,int numOfThread){
		threadsLocks.set(numOfThread-1,lock);
	}

	public void setThreadCondition(Condition c,int numOfThread){
		threadsConditions.set(numOfThread-1,c);
	}

	/**
	 * Creates the current race's ,in the specific thread number, data.
	 *<br> Update's the system database.
	 * @param numOfThread
	 * @param numOfRace
	 */
	public synchronized void createRaceData(int numOfThread,int numOfRace){
		for(int j = 0 ; j < NUMBER_OF_CARS ; j++){
			CarProperties.Color tempColor = colorOptions[(int)Math.floor(Math.random()*colorOptions.length)];
			CarProperties.Type tempType = typeOption[(int)Math.floor(Math.random()*typeOption.length)];
			CarProperties.Size tempSize = sizeOption[(int)Math.floor(Math.random()*sizeOption.length)];

			cars[numOfThread-1][j] = new Car(j,numOfThread,numOfRace);//Create a new Car
			carsPanes[numOfThread-1][j] = new CarPane(cars[numOfThread-1][j],NUMBER_OF_CARS,MAX_SPEED,SPEED_CHANGE_RATE,tempType,tempColor,tempSize);//Add the new car into the carPane with random attributes of speed,size and color.
			try {
				String query = queries.insertToCarTable(cars[numOfThread-1][j].getCarName());//Insert the car name into the query fomulation.
				statement.executeUpdate(query);//Execution of the query
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			setWaitingRacesStatement.setInt(1,numOfRace);
			for (int i = 2 ; i <= 6; i++)
				setWaitingRacesStatement.setString(i, cars[numOfThread-1][i-2].getCarName());
			setWaitingRacesStatement.execute();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts the car's data of the specific race to the database.
	 * @param numOfThread the number of the current race thread.
	 */
	public void insertToCarsRaceId(int numOfThread){
		try {
			String currentRaceId = "RaceId"+cars[numOfThread-1][0].getRace_num();
			for(int j = 0 ; j < NUMBER_OF_CARS ; j++){
				String query = queries.setCarsRaceId(currentRaceId,cars[numOfThread-1][j].getCarName());
				statement.executeUpdate(query);
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void writeToServerLog(String mesg){
		try {
			streamToServer.writeUTF(UPDATE_SERVER);
			streamToServer.flush();
			streamToServer.writeUTF(mesg);
			streamToServer.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the race ,with number of thread 1, have 3 diffrents cars with invested on.<br>if do, the race tell's the server that it's ready to start.
	 * @param numOfThread the number of the current race thread.
	 */
	public void isRace1Ready(int numOfRace){
		lock.lock();
		this.numOfRaceArr[0] = numOfRace;
		int numOfCars,raceId = 1;
		try {
			isRaceReady1.setInt(1,numOfRace);
			ResultSet rs = isRaceReady1.executeQuery();
			rs.next();
			numOfCars = rs.getInt(1);
			while(numOfCars < 3){
				System.out.println("waiting for more " + (3 - numOfCars) + " for race" + numOfRace);
				writeToServerLog("RaceId" +  numOfRace +" waiting for more " + (3 - numOfCars) + " bets on car for to start the race");
				raceReady1.await();
				rs = isRaceReady1.executeQuery();
				rs.next();
				numOfCars = rs.getInt(1);
			}
			if(!isRaceRunning){
				System.out.println("Race" + numOfRace +" start now");
				writeToServerLog("Race" + numOfRace +" start now");
				isRaceRunning = true;
				startRace(raceId,numOfRace);
			}
			else{
				System.out.println("Race" + numOfRace +" is ready to start");
				writeToServerLog("Race" + numOfRace +" is ready to start");
				isRaceIdReady[raceId-1] = true;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
		}
		finally{
			lock.unlock();
		}
	}

	/**
	 * Checks if the race ,with number of thread 2, have 3 diffrents cars with invested on.<br> if do, the race tell's the server that it's ready to start.
	 * @param numOfThread the number of the current race thread.
	 */
	public void isRace2Ready(int numOfRace){
		lock.lock();
		this.numOfRaceArr[1] = numOfRace;
		int numOfCars,raceId = 2;
		try {
			isRaceReady2.setInt(1,numOfRace);
			ResultSet rs = isRaceReady2.executeQuery();
			rs.next();
			numOfCars = rs.getInt(1);
			while(numOfCars < 3){
				System.out.println("waiting for more " + (3 - numOfCars) + " for race" + numOfRace);
				writeToServerLog("RaceId" +  numOfRace +" waiting for more " + (3 - numOfCars) + " bets on car for to start the race");
				raceReady2.await();
				rs = isRaceReady2.executeQuery();
				rs.next();
				numOfCars = rs.getInt(1);
			}
			if(!isRaceRunning){
				System.out.println("Race" + numOfRace +" start now");
				isRaceRunning = true;
				startRace(raceId,numOfRace);
			}
			else{
				System.out.println("Race" +numOfRace +" is ready to start");
				isRaceIdReady[raceId-1] = true;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
		}
		finally{
			lock.unlock();
		}
	}

	/**
	 * Checks if the race ,with number of thread 3, have 3 diffrents cars with invested on.<br>if do, the race tell's the server that it's ready to start.
	 * @param numOfThread the number of the current race thread.
	 */
	public void isRace3Ready(int numOfRace){
		lock.lock();
		this.numOfRaceArr[2] = numOfRace;
		int numOfCars,raceId = 3;
		try {
			isRaceReady3.setInt(1,numOfRace);
			ResultSet rs = isRaceReady3.executeQuery();
			rs.next();
			numOfCars = rs.getInt(1);
			while(numOfCars < 3){
				System.out.println("waiting for more " + (3 - numOfCars) + " for race" + numOfRace);//NEED TO WRITE THIS TO THE SERVER
				writeToServerLog("RaceId" +  numOfRace +" waiting for more " + (3 - numOfCars) + " bets on car for to start the race");
				raceReady3.await();
				rs = isRaceReady3.executeQuery();
				rs.next();
				numOfCars = rs.getInt(1);
			}
			if(!isRaceRunning){
				System.out.println("Race" + numOfRace +" start now");
				isRaceRunning = true;
				startRace(raceId,numOfRace);
			}
			else{
				System.out.println("Race" + numOfRace +" is ready to start");
				isRaceIdReady[raceId-1] = true;
			}

		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
		}
		finally{
			lock.unlock();
		}
	}
	/**
	 * Start the specific race with the fit numOfThread number.
	 * @param numOfThread the number of race thread
	 * @param numOfRace the number of the current race (the number is from the start of the system).
	 * @throws SQLException
	 */
	public void startRace(int numOfThread,int numOfRace) throws SQLException{
		GamblerApp.limitBetOnRace(numOfRace);
		DateFormat df = new SimpleDateFormat("dd/MM/yy-HH:mm:ss");
		Calendar calobj = Calendar.getInstance();
		currentRaceName = "Race"+numOfRace+"-"+df.format(calobj.getTime());
		statement.executeUpdate(queries.insertToPastRaceTable(currentRaceName));
		writeToServerLog(currentRaceName + " is starting");
		views[numOfThread-1].createAllTimelines();
		playAudios(0,numOfThread);
		isRaceIdReady[numOfThread-1] = false;
	}

	/**
	 * Checks and decide which race should start next.
	 * @throws SQLException
	 */
	private void readyToRace() throws SQLException{
		lockRace.lock();
		int raceToStart;
		if(isRaceIdReady[0]){
			if(!isRaceIdReady[1] && !isRaceIdReady[2]){
				isRaceIdReady[0] = false;
				startRace(1,numOfRaceArr[0]);
			}
			else if(isRaceIdReady[1]){
				raceToStart = compareRaces(1,2);
				isRaceIdReady[raceToStart-1] = false;
				startRace(raceToStart,numOfRaceArr[raceToStart-1]);
			}
			else{
				raceToStart = compareRaces(1,3);
				isRaceIdReady[raceToStart-1] = false;
				startRace(raceToStart,numOfRaceArr[raceToStart-1]);
			}
		}
		else if(isRaceIdReady[1]){
			if(!isRaceIdReady[2])
				startRace(2,numOfRaceArr[1]);
			else {
				raceToStart = compareRaces(2,3);
				isRaceIdReady[raceToStart-1] = false;
				startRace(raceToStart,numOfRaceArr[raceToStart-1]);
			}
		}
		else {
			isRaceIdReady[2] = false;
			startRace(3,numOfRaceArr[2]);
		}
		lockRace.unlock();
	}

	/**
	 * Inserts into the data base the last race result.
	 * @param numOfThread the number of the race thread.
	 * @throws SQLException
	 */
	public void createResultTable(int numOfThread) throws SQLException{
		for(CarPane cp : carsPanes[numOfThread-1]){
			setRaceResultStatement.setString(1,cp.getCarModel().getCarName());
			setRaceResultStatement.setString(2,cp.getPlace()+"'st");
			setRaceResultStatement.setInt(3,numOfRaceArr[numOfThread-1]);
			setRaceResultStatement.setString(4, currentRaceName);
			setRaceResultStatement.executeUpdate();
		}	
	}

	/**
	 * Finds out the profit of the system and of the gamblers clients from the last race and updates the database.
	 * @param numOfThread the number of the race thread.
	 */
	public void findOutProfit(int numOfThread){
		int totalInvestment,winnerCarInvestment,profit,gamblerBalance,gamblerInvestment;//winnerId=0,gamblerBalance,gamblerInvestment; 
		String winCarName = null;
		for(CarPane cp : carsPanes[numOfThread-1]){//Find out the winner car name
			if (cp.getPlace() == 1){
				winCarName = cp.getCarModel().getCarName();
				break;
			} 
		}
		try {
			checkCashStatement = queries.raceCashStatemnt();
			checkCashStatement.setInt(1,numOfThread);
			ResultSet rs1 = checkCashStatement.executeQuery();
			rs1.next();
			totalInvestment = rs1.getInt(1);
			ResultSet rs2 = statement.executeQuery(queries.getCarInvestment(winCarName));
			rs2.next();
			winnerCarInvestment = rs2.getInt(1);
			if(winnerCarInvestment == 0)
				profit = totalInvestment;
			else{
				profit = (int)(totalInvestment*0.05);
				numOfWinnersStatement.setString(1,winCarName);
				ResultSet rs3 = numOfWinnersStatement.executeQuery();
				if(rs3.next()){
					do{
						String tempGamblerName = rs3.getString(1);
						gamblerInvestment = rs3.getInt(2);
						getBalanceStatemnt.setString(1,tempGamblerName);
						ResultSet rs4 = getBalanceStatemnt.executeQuery();
						rs4.next();
						gamblerBalance = rs4.getInt(1);
						System.out.println( tempGamblerName + " old balance : " + gamblerBalance);
						System.out.println(tempGamblerName + " has invest : " + gamblerInvestment);
						int tempGamblerProfit = (int)((gamblerInvestment*(totalInvestment*0.95))/winnerCarInvestment) - gamblerInvestment;//Total profit from the race
						gamblerBalance += tempGamblerProfit;
						System.out.println(tempGamblerName +" new  balance : " + gamblerBalance);
						setUserProfitStatement.setString(1,tempGamblerName);
						setUserProfitStatement.setString(2,currentRaceName);
						setUserProfitStatement.setInt(3,tempGamblerProfit);
						setUserProfitStatement.execute();
						setBalanceStatement.setInt(1,gamblerBalance);
						setBalanceStatement.setString(2,tempGamblerName);
						setBalanceStatement.executeUpdate();
					}while(rs3.next());
				}
			}
			numOfLosserStatemnt.setString(1,winCarName);
			numOfLosserStatemnt.setString(2,new Integer(numOfRaceArr[numOfThread-1]).toString());
			ResultSet rs5 = numOfLosserStatemnt.executeQuery();
			if(rs5.next()){
				do{
					String tempGamblerName = rs5.getString(1);
					gamblerInvestment = rs5.getInt(2);
					gamblerInvestment*=(-1);
					ResultSet rs= statement.executeQuery(queries.isUserWon(tempGamblerName,currentRaceName));
					if(!(rs.next())){//If user to bet on the winner car,his details has inserted already.
						setUserProfitStatement.setString(1,tempGamblerName);
						setUserProfitStatement.setString(2,currentRaceName);
						setUserProfitStatement.setInt(3,gamblerInvestment);
						setUserProfitStatement.execute();
					}
				}while(rs5.next());
			}
			statement.executeUpdate(queries.updatePastRaceDetailsTable(winCarName,numOfRaceArr[numOfThread-1],profit, totalInvestment, currentRaceName));
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Checks which race should start between 2 races.<br>The race with the higest Investment on is the one that will start.
	 * @param raceId1 race id number of some race
	 * @param raceId2 race id number of another race
	 * @return the next race to start id number.
	 */
	public int compareRaces(int raceId1,int raceId2)
	{
		int sum1,sum2;
		try {
			checkCashStatement.setInt(1,raceId1);
			ResultSet rs1 = checkCashStatement.executeQuery();
			rs1.next();
			sum1 = rs1.getInt(1);
			System.out.println("Total money in race thread number " + raceId1 + " is " + sum1);
			writeToServerLog("Total money in race thread number " + raceId1 + " is " + sum1);
			checkCashStatement.setInt(1,raceId2); 
			ResultSet rs2 = checkCashStatement.executeQuery();
			rs2.next();
			sum2 = rs2.getInt(1);
			System.out.println("Total money in race thread number " + raceId2 + " is " + sum2);
			writeToServerLog("Total money in race thread number " + raceId2 + " is " + sum2);
			if(sum1 > sum2){
				writeToServerLog("Race thread number " + raceId1 + " is getting ready to start");
				return raceId1;
			}
			else
				writeToServerLog("Race thread number " + raceId2 + " is getting ready to start");
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return raceId2;

	}

	public void closeSocket(int numOfThread){
		try {
			sockets[numOfThread-1].close();
		} 
		catch (IOException e) {
			Platform.exit();
		}
	}

	/**
	 * Plays a music during the race.when the final song is done,the race is over.(Recursive function)
	 * @param j int that become greater until it reach to the size of the array of songs.
	 * @param numOfThread the number of the race thread.
	 */
	public void playAudios(int j,int numOfThread){//The race is playing until the end of the last song in the mediaPlayers array.
		mediaPlayers[j].play();
		mediaPlayers[j].setOnEndOfMedia(new Runnable(){
			public void run(){
				{
					if(j == 2)
						mediaPlayers[j] = raceAudios[(int)Math.floor(Math.random()*NUMBER_OF_RACE_SONGS)];//Plays a random race song from 4 options.
					mediaPlayers[j].getOnReady();
					mediaPlayers[j].seek(Duration.ZERO);
					mediaPlayers[j].stop();
					int x = j+1;
					if(x < mediaPlayers.length){
						playAudios(x,numOfThread);
					}
					else{
						Timeline tl1 = new Timeline(new KeyFrame(Duration.millis(2000),(e)->{
							stopAnimations(numOfThread);
						}));
						tl1.setCycleCount(1);
						tl1.play();

						Timeline tl3= new Timeline(new KeyFrame(Duration.millis(1000*60),(e)->{
							try {
								threadsLocks.get(numOfThread-1).lock();
								threadsConditions.get(numOfThread-1).signal();
								threadsLocks.get(numOfThread-1).unlock();
								if(isRaceIdReady[0] || isRaceIdReady[1] || isRaceIdReady[2])
									readyToRace();
								else
									isRaceRunning = false;
								startNewRace(numOfThread);
							} 
							catch (SQLException e1) {
								e1.printStackTrace();
							}

						}));
						tl3.setCycleCount(1);

						Timeline tl2 = new Timeline(new KeyFrame(Duration.millis(10000),(e)->{
							try {
								createResultTable(numOfThread); 
								views[numOfThread-1].setBorderPane(fillResultTable(numOfThread));
								Platform.runLater(()->{
									threadsLocks.get(numOfThread-1).lock();
									threadsConditions.get(numOfThread-1).signal();
									threadsLocks.get(numOfThread-1).unlock();
								});
								findOutProfit(numOfThread);
								GamblerApp.refreashBalance();
								tl3.play();
							} 
							catch (SQLException e1) {
								e1.printStackTrace();
							} 
						}));
						tl2.setCycleCount(1);
						tl2.play();
					}
				}
			}
		});
	}

	/**
	 * Insert into the RaceResultPane the race result so the gambler's can view the race's result.
	 * @param numOfThread the number of the race thread.
	 * @return a pane with a table fill with the result race data of race.
	 * @throws SQLException
	 */
	public RaceResultPane fillResultTable(int numOfThread) throws SQLException{
		RaceResultPane resultPane = new RaceResultPane();
		ResultSet queryResult = statement.executeQuery(queries.getRaceResult(numOfRaceArr[numOfThread-1]));
		queryResult.next();
		do{
			resultPane.data.add(new EndOfRaceCar(queryResult.getString(1),queryResult.getString(2)));
		}while(queryResult.next());
		return resultPane;
	}

	/**
	 * Creating a new race with the thread number of the last race.
	 * @param numOfThread number of the race thread.
	 * @throws SQLException
	 */
	public void startNewRace(int numOfThread) throws SQLException{
		statement.executeUpdate(queries.deletePastRace(numOfRaceArr[numOfThread-1]));
		threadsList.set(numOfThread - 1,null);
		numOfRaceArr[numOfThread-1] = ++numberOfRace;
		RaceThread newRace = new RaceThread(numberOfRace, this,numOfThread);
		new Thread(newRace).start();
	}

	/**
	 * Tells to all the cars in the race that its the final round of the race.
	 * @param numOfThread number of the race thread.
	 */
	public void stopAnimations(int numOfThread){
		for(CarPane cp : carsPanes[numOfThread-1])
			cp.setFlag(true);
	}

	public MediaPlayer[] getAudios(){
		return mediaPlayers;
	}

	public int getNumberOfCars(){
		return NUMBER_OF_CARS;
	}

	public void setSocket(Socket socket,int numOfThread){
		this.sockets[numOfThread-1] = socket;
		setStreamsToServer(numOfThread);
	}

	public Socket getSocket(int numOfThread){
		return sockets[numOfThread-1];
	}

	/**
	 * Send's to each car in a specific race a direct stream to the server.
	 * @param numOfThread number of the race thread.
	 */
	public void setStreamsToServer(int numOfThread){
		try {
			streamToServer = new ObjectOutputStream(sockets[numOfThread-1].getOutputStream());
			for(Car c : cars[numOfThread-1])
				c.setStreamToServer(streamToServer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void changeColor(int id,Color color,int numOfThread){	
		getCarById(numOfThread,id).setColor(color);
	}

	public void changeRadius(int id,int radius,int numOfThread){
		getCarById(numOfThread,id).setRadius(radius);
	}

	public void changeSpeed(int id,double speed,int numOfThread){
		getCarById(numOfThread,id).setSpeed(speed);
	}

	public CarPane[] getCarsPanes(int numOfThread){
		return carsPanes[numOfThread-1];
	}

	public Car getCarById(int numOfThread,int id){	
		return cars[numOfThread-1][id];
	}

	public int getNumberOfRaces(){
		return NUMBER_OF_RACES;
	}

	/**
	 * Connects to the CarRace project database.
	 * <br>Uses in CarRaceDBScript.sql script.
	 */
	@SuppressWarnings("resource")
	private void initializeDB(){
		try{
			Class.forName(driver);//Load the JDBC Driver,implement the Driver interface from java.sql
			System.out.println("JDBC Driver loaded");
			connection = DriverManager.getConnection(url+db,"scott","tiger");//Establish connection
			System.out.println("Connection Established");
			statement = connection.createStatement();//The "cart" that delivers the SQL queries through the connection.
			queries = new Queries(connection);
			setDBStatements();
			FileInputStream fstream = new FileInputStream(DBFile);// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) { //Read DataBase init File Line By Line
				System.out.println(strLine);
				if (strLine != null && !strLine.equals(""))
					statement.execute(strLine);          
			}
		}
		catch(Exception ex){
			try {
				System.out.println(ex.getMessage());
				connection.close();
				ex.printStackTrace();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		finally{

		}
	}

	/**
	 * Gets a ready Prepare Statments queries for use in the system database.
	 * @throws SQLException
	 */
	public void setDBStatements() throws SQLException{
		isRaceReady1 = queries.isRaceReadyStatement();
		isRaceReady2 = queries.isRaceReadyStatement();
		isRaceReady3 = queries.isRaceReadyStatement();
		checkCashStatement = queries.raceCashStatemnt();
		setWaitingRacesStatement = queries.setWaitingRacesStatement();
		numOfWinnersStatement = queries.winnerGamblersStatement();
		numOfLosserStatemnt = queries.loserGamblersStatement();
		getBalanceStatemnt =  queries.getUserBalanceStatemnt();
		setBalanceStatement = queries.setUserBalanceStatemnt();
		setRaceResultStatement = queries.setRaceResultStatemnt();
		setUserProfitStatement = queries.setUserProfitStatemnt();
	}
}
