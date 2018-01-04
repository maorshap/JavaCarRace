package Gambler;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * class that acts as a deliver of data between the gambler-client and the server.
 */
public class GamblerMessage implements Serializable{

	private static final long serialVersionUID = 7465494026185705801L;
	private String userName,password,carName;
	private int money,raceId,totalInvestment,balance,flag;
	private ArrayList<String> carNames1 = new ArrayList<>();
	private ArrayList<String> carNames2 = new ArrayList<>();
	private ArrayList<String> carNames3 = new ArrayList<>();
	private ArrayList<String> raceName = new ArrayList<>();
	private int[] NumOfRaceArr = new int[3];
	
	public int[] getNumOfRaceArr(){
		return NumOfRaceArr;
	}
	
	public void setNumOfRaceArr(int[] arr){
		for (int i = 0 ; i < 3 ; i++)
		this.NumOfRaceArr[i] = arr[i];
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getCarName(){
		return carName;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public void setCarName(String carName){
		this.carName = carName;
	}
	
	public void setMoney(int money){
		this.money = money;
	}
	
	public void setRaceId(int id){
		this.raceId = id;
	}
	
	public void setTotalInvestment(int investment){
		this.totalInvestment = investment;
	}
	
	public int getRaceId(){
		return raceId;
	}
	
	public void setBalance(int balance){
		this.balance = balance;
	}
	
	public int getTotalInvestment(){
		return totalInvestment;
	}
	
	public int getMoney(){
		return money;
	}
	
	public int getBalance(){
		return balance;
	}
	
	public void setCarName1(ArrayList<String> lst){
		this.carNames1.addAll(lst);
	}
	
	public void setCarName2(ArrayList<String> lst){
		this.carNames2.addAll(lst);
	}
	
	public void setCarName3(ArrayList<String> lst){
		this.carNames3.addAll(lst);
	}
	
	public void setRaceNames(ArrayList<String> lst){
		this.raceName.addAll(lst);
	}
	
	public ArrayList<String> getRaceNameList(){
		return raceName;
	}
	
	public ArrayList<String> getcarName1List(){
		return carNames1;
	}
	
	public ArrayList<String> getcarName2List(){
		return carNames2;
	}
	
	public ArrayList<String> getcarName3List(){
		return carNames3;
	}
	
	public int getFlag(){
		return flag;
	}
	
	public void setFlag(int value){
		this.flag = value;
	}
	
	public void clearCarNameLists(){
		carNames1.clear();
		carNames2.clear();
		carNames3.clear();
	}
	
	public void clearRacesNameList(){
		raceName.clear();
	}
	
}
