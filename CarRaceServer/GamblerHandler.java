package CarRaceServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Gambler.GamblerMessage;
import RaceThread.CarRaceServerModel;

/**
 * This class is in charge to handle a new online gambler.
 * <br> The class is uses inside the CarRaceServerWindow.
 * <br> The class is access to the server database according to the action of the gambler client.
 */
public class GamblerHandler implements Runnable {

	private final String NEW_GAMBLER = "newGambler";
	private final String CHECK_USER = "checkUser";
	private final String INSERT_BET = "insertBet";
	private final String SET_BALANCE = "setUserBalance";
	private final String GET_CARS_NAMES = "getcarsName";
	private final String GET_WAITING_RACES = "getWaitingRaces";
	private final String GET_NUM_OF_RACES_ARR = "getNumOfRacesArr";
	private final String GET_RACE_READY = "getRaceReady";
	private final String GET_BALANCE = "getBalance";
	private final String EXIT = "exit";

	private Socket socket;
	private ObjectOutputStream streamToClient;
	private ObjectInputStream streamFromClient;
	private String typeOfMessage;
	private CarRaceServerModel model;

	/**
	 * GamblerHandler gets the created gambler socket and the server model who charge on the access to the database.
	 * <br> Created by the CarRace Server.
	 * @param socket the socket that connect the server with the specific gambler-client.
	 * @param model the class that in charge to communicate with the system database.
	 */
	public GamblerHandler(Socket socket,CarRaceServerModel model) {
		this.socket = socket;
		this.model = model;
	}

	@Override
	public void run() {
		try {
			streamToClient = new ObjectOutputStream(socket.getOutputStream());
			streamToClient.flush();
			streamFromClient = new ObjectInputStream(socket.getInputStream());
			while (true) {
				synchronized (streamFromClient) {
					typeOfMessage = streamFromClient.readUTF();
					GamblerMessage gamblerMessage = (GamblerMessage) streamFromClient.readObject();
					switch (typeOfMessage) {

						case NEW_GAMBLER:
							try{
								model.getStatement().executeUpdate(model.getQueriesObject().insertToUsersTable(gamblerMessage.getUserName(), gamblerMessage.getPassword()));
								CarRaceServerWindow.printMsg("The User known as " +gamblerMessage.getUserName() + " is online");
								gamblerMessage.setFlag(1);
							}
							catch(Exception ex){
								gamblerMessage.setFlag(0);
								System.out.println("User is already exists in the system");
							}
							finally{
								streamToClient.writeObject(gamblerMessage);
								streamToClient.flush();
							}
							break;

						case CHECK_USER:
							try{
								ResultSet resultSet = model.getStatement().executeQuery(model.getQueriesObject().checkUserQuery(gamblerMessage.getUserName(), gamblerMessage.getPassword()));
								if(resultSet.next()){
									gamblerMessage.setFlag(1);
									gamblerMessage.setBalance(resultSet.getInt(1));
									CarRaceServerWindow.printMsg("The User known as " +gamblerMessage.getUserName() + " is online");
								}
								else{
									gamblerMessage.setFlag(0);
								}
							}
							catch(Exception ex){
							}
							finally{
								streamToClient.writeObject(gamblerMessage);
								streamToClient.flush();
							}
							break;

						case INSERT_BET:
							try{
								gamblerMessage.setTotalInvestment(0);
								ResultSet rs = model.getStatement().executeQuery(model.getQueriesObject().getCarInvestment(gamblerMessage.getCarName()));
								if(rs.next())
									gamblerMessage.setTotalInvestment(rs.getInt(1) + gamblerMessage.getMoney());
								model.getStatement().executeUpdate(model.getQueriesObject().setCarInvestment(gamblerMessage.getTotalInvestment(),gamblerMessage.getCarName()));
								model.getStatement().executeUpdate(model.getQueriesObject().insertToUserBets(
										gamblerMessage.getUserName(),gamblerMessage.getCarName(),gamblerMessage.getMoney(), gamblerMessage.getRaceId()));

							}
							catch(SQLException ex){
								ex.printStackTrace();
							}
							break;

						case SET_BALANCE:
							PreparedStatement preStatement = model.getQueriesObject().setUserBalanceStatemnt();
							preStatement.setInt(1,gamblerMessage.getBalance());
							preStatement.setString(2,gamblerMessage.getUserName());
							preStatement.execute();
							break;

						case GET_CARS_NAMES:
							ResultSet resultSet = model.getStatement().executeQuery(model.getQueriesObject().getRacesCarsNames());
							resultSet.next();
							gamblerMessage.clearCarNameLists();
							ArrayList<String> tempList = new ArrayList<>();
							for(int i = 1 ; i <= model.getNumberOfCars() ; i++)
								tempList.add(resultSet.getString(i));
							gamblerMessage.setCarName1(tempList);
							tempList.clear();
							resultSet.next();
							for(int i = 1 ; i <= model.getNumberOfCars() ; i++)
								tempList.add(resultSet.getString(i));
							gamblerMessage.setCarName2(tempList);
							tempList.clear();
							resultSet.next();
							for(int i = 1 ; i <= model.getNumberOfCars() ; i++)
								tempList.add(resultSet.getString(i));
							gamblerMessage.setCarName3(tempList);
							tempList.clear();
							streamToClient.writeObject(gamblerMessage);
							streamToClient.flush();
							break;

						case GET_WAITING_RACES:
							ResultSet rs = model.getStatement().executeQuery(model.getQueriesObject().getWaitingRace());
							gamblerMessage.clearRacesNameList();
							ArrayList<String> raceslist = new ArrayList<>();
							while(rs.next()){
								raceslist.add(rs.getString(1));
							}
							gamblerMessage.setRaceNames(raceslist);
							streamToClient.writeObject(gamblerMessage);
							streamToClient.flush();
							break;

						case GET_NUM_OF_RACES_ARR:
							gamblerMessage.setNumOfRaceArr(model.getNumOfRaceArr());
							streamToClient.writeObject(gamblerMessage);
							streamToClient.flush();
							break;

						case GET_RACE_READY:
							int raceId = gamblerMessage.getRaceId();
							model.getLock().lock();
							if(raceId == 1)
								model.getRaceReady1().signalAll();
							else if(raceId == 2)
								model.getRaceReady2().signalAll();
							else
								model.getRaceReady3().signalAll();
							model.getLock().unlock();
							break;

						case GET_BALANCE:
							PreparedStatement ps = model.getQueriesObject().getUserBalanceStatemnt();
							ps.setString(1,gamblerMessage.getUserName());
							ResultSet resultset = ps.executeQuery();
							if(resultset.next())
								gamblerMessage.setBalance(resultset.getInt(1));
							streamToClient.writeObject(gamblerMessage);
							streamToClient.flush();
							break;

						case EXIT:
							CarRaceServerWindow.printMsg("The User known as " +gamblerMessage.getUserName() + " is offline.");
							break;

						default:
							System.out.println("Uknown case from streamFromServer");
							break;
					}
				}
			}
		} 
		catch (IOException ex) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		catch (Exception e) {
		}
	}
}
