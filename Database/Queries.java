package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *Charge on obtains all the queries that in use in the system by the Races and gamblers clients.
 */
public class Queries{
	private Connection connection;
	private String prepareQuery;

	public Queries(Connection con){
		this.connection = con;
	}
	
/////////////////////Inserts to table queries///////////////////////////////////////////////////////////
	
	public String insertToUsersTable(String userName,String password){
		return  "Insert into Users (UserName,Password) Values('" + userName + "','" + password + "');";
	}
	
	public String insertToCarTable(String carName){
		return "Insert into Car (Car_Name) value('" + carName +"');";
	}
	
	public String insertToUserBets(String userName,String carName,int money,int RaceId){
		return "Insert Into UserBets Values ('" + userName + "','" + carName +"'," + money + "," + RaceId +");";
	}
	
	public String insertToPastRaceTable(String currentRaceName){
		return "Insert into PastRaceDetails (RaceName) Values('"+currentRaceName+"');";
	}
	
	public PreparedStatement setUserProfitStatemnt() throws SQLException{
		prepareQuery = "Insert into UserProfits Values(?,?,?);";
		return connection.prepareStatement(prepareQuery);
	} 
	
	public PreparedStatement setRaceResultStatemnt() throws SQLException{
		prepareQuery = "Insert into RaceResult(CarName,Place,RaceNumber,RaceName) Values(?,?,?,?);";
		return connection.prepareStatement(prepareQuery);
	} 
	
	public PreparedStatement setWaitingRacesStatement() throws SQLException{
		prepareQuery = "Insert into WaitingRaces (IdNumber,CarName1,CarName2,CarName3,CarName4,CarName5)" +
				"Values(?,?,?,?,?,?)";
		return connection.prepareStatement(prepareQuery);
	}
	
///////////////////Update Queries///////////////////////////////////////////////////////////////////////
	
	public PreparedStatement setUserBalanceStatemnt() throws SQLException{
		prepareQuery = "Update Users Set Balance = ? Where UserName = ?;";
		return connection.prepareStatement(prepareQuery);
	}
	
	public String setCarInvestment(int totalInvestment ,String carName){
		return "Update Car Set TotalInvestment = " + totalInvestment + " Where Car_Name ='" + carName +"';";
	}
	
	public String setCarsRaceId(String RaceId,String carName){
		return "Update Car Set RaceId = '" + RaceId + "' Where Car_Name ='" + carName +"';";
	}
	
	public String updatePastRaceDetailsTable(String winCarName,int RaceId,int profit,int totalInvestment,String currentRaceName){
		return "Update PastRaceDetails Set WinnerCar = '" + winCarName +"',RaceId = " + RaceId + ", System_Profit = " + profit + ", TotalInvestment = " + totalInvestment 
				+ " Where RaceName ='" + currentRaceName +"';";
	}
	
	
///////////////////Select Queries////////////////////////////////////////////////////////////////////////
	
	/**
	 * Return a mysql statement that check if the system need to set the specific race in ready mode.
	 * @return PreparedStatement 
	 * @throws SQLException 
	 */
	public PreparedStatement isRaceReadyStatement() throws SQLException{
		prepareQuery = "Select Count(Distinct UserBets.CarName) From UserBets Where UserBets.RaceIdNumber= ?";
		return connection.prepareStatement(prepareQuery);
	}

	/**
	 * returns statement that return all money that has invested in the race by all the gamblers.
	 * @return PreparedStatement
	 * @throws SQLException
	 */
	public PreparedStatement raceCashStatemnt() throws SQLException{
		prepareQuery = "Select sum(UserBets.Investment) From UserBets Where UserBets.RaceIdNumber= ?";
		return connection.prepareStatement(prepareQuery);
	}

	/**
	 * Return a mysql statement that Finds out how much money the specific gambler has invest on the car(winning car).
	 * @return PreparedStatement
	 * @throws SQLException
	 */
	public PreparedStatement winnerGamblersStatement() throws SQLException{
		prepareQuery = "Select UserName,Sum(Investment) From UserBets Where CarName =? Group By UserName;";
		return connection.prepareStatement(prepareQuery);
	}
	
	/**
	 * Return a mysql statement that Finds out how much money the specific gambler has lost on the race.
	 * @return PreparedStatement
	 * @throws SQLException
	 */
	public PreparedStatement loserGamblersStatement() throws SQLException{
		prepareQuery = "Select UserName,Sum(Investment) From UserBets Where CarName !=? And RaceIdNumber = ?  Group By UserName;";
		return connection.prepareStatement(prepareQuery);
	}

	public PreparedStatement getUserBalanceStatemnt() throws SQLException{
		prepareQuery = "Select Balance From Users Where UserName = ?;";
		return connection.prepareStatement(prepareQuery);
	}

	/**
	 *Return a String that represents the query that return the total money that invested on the specific car.
	 * @param CarName the name of the invested car.
	 * @return String the query for to pull the data from the database.
	 */
	public String getCarInvestment(String CarName){
		return "Select Car.TotalInvestment From Car Where Car.Car_Name='" + CarName +"';";
	}
	
	public String getRaceResult(int numOfRace){
		return "Select CarName,Place From RaceResult Where RaceNumber = " + numOfRace + ";";
	}
	
	public String getWaitingRace(){
		return "Select IdNumber From WaitingRaces;";
	}
	
	public String getRacesCarsNames(){
		return "Select CarName1,CarName2,CarName3,CarName4,CarName5 From WaitingRaces;";
	}
	
	public String getRacesNames(){
		return "Select RaceName From PastRaceDetails;";
	}
	
	public String getUsersNames(){
		return "Select UserName From Users;";
	}
	
	/**
	 * Return a String that help for to find if the specific user is in the database system already.
	 * @param userName the name of the checked user.
	 * @param password the password of the checked user,
	 * @return String the query for to pull the data from the database.
	 */
	public String checkUserQuery(String userName,String password){
		return "Select Balance From Users Where UserName = '" + userName + "' And Password = '" + password +"';";
	}
	
	public String isUserWon(String userName,String RaceName){
		return "select * From UserProfits Where UserName ='"+ userName +"' And RaceName='"+RaceName +"';";
	}
	
	public String systemProfitQuery(){
		return "Select RaceName,System_Profit From PastRaceDetails";
	}
	
	public String usersBalanceQuery(){
		return "Select UserName,Balance from Users Order by Balance DESC";
	}
	
	public String carsWithoutInvestmentQuery(String RaceId) throws SQLException{
		return "Select Car_Name From Car Where RaceId ='" + RaceId +"' And TotalInvestment = 0;";

	}
	
	/**
	 * Return a String that Helps to get from the database all the specific race bets data.
	 * @param RaceId the race id number of the wanted race.
	 * @return String the query for to pull the data from the database.
	 * @throws SQLException
	 */
	public String getRaceGamblersQuery(String RaceId) throws SQLException{
		RaceId = RaceId.substring(6);//Turn from "RaceId1" to "1"
		return "Select UserName,Investment,CarName From UserBets Where RaceIdNumber ='" + RaceId + "';";
	}
	
	public String carsWithInvestmentQuery(String RaceId) throws SQLException{
		return "Select Car_Name From Car Where RaceId ='" + RaceId +"' And TotalInvestment > 0;";
	}
	
	public String getCarsFromPast(String raceName){
	 return "Select CarName,Place,TotalInvestment From Car,RaceResult Where RaceResult.RaceName ='" +raceName+"' And RaceResult.CarName = Car.Car_Name ;"; 
	}
	
	public String getPastRaceGamblers(String raceName){
		return  "Select UserName,User_Profit From UserProfits Where RaceName ='"+raceName+"';";
	}
	
	public String getAllPastRacesDetails(){
		return  "Select RaceName,WinnerCar,System_Profit,TotalInvestment From PastRaceDetails ORDER BY System_Profit DESC;";
	}
	
	public String getGamblerPastBets(String gamblerName){
		return "Select UserBets.* From UserBets,PastRaceDetails Where PastRaceDetails.RaceId = UserBets.RaceIdNumber And UserName ='" + gamblerName+"';";
	}
	
	public String getGamblerPastRaces(String gamblerName){
		return "Select RaceName,User_Profit From UserProfits Where UserName ='"+ gamblerName+"';";
	}
	
	
	public String getAllBets(){
		return "Select * From UserBets;";
	}
	
	
////////////////Delete from table Queries////////////////////////////////////////////////////////	
	
	public String deletePastRace(int numOfRace){
		return "Delete from WaitingRaces Where IdNumber = " +numOfRace +";";
	}
	
	
		
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
