package RaceThread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;

/**
 * holds 1 of 2 parts of the data and actions of a single car (CarPane holding the rest).
 */
public class Car implements CarEvents{ 

	private final String speedMsg = "SPEED_EVENT";
	private final String winnerMsg = "WINNER_EVENT";
	
	private int id;
	private int model_id;
	private int race_num;
	private double speed;
	private Color color;
	private int wheelRadius;
	private Map<eventType, ArrayList<EventHandler<Event>>> carHashMap;//According to the event, number of event handlers are getting implemented.
	private ObjectOutputStream streamToServer;
	private String carName;

	public Car(int id, int model_id,int race_num) {	
		this.id = id;
		this.model_id = model_id;
		this.race_num = race_num;
		this.speed = 1;
		this.color = Color.RED;
		this.wheelRadius = 5;
		carName = "Car#"+race_num+(id+1);
		carHashMap = new HashMap<eventType, ArrayList<EventHandler<Event>>>();
		for (eventType et : eventType.values())
			carHashMap.put(et, new ArrayList<EventHandler<Event>>());
	}

	public String getCarName(){
		return carName;
	}
	
	public void setCarName(String carName){
		this.carName += carName;
	}
	
	public void setStreamToServer(ObjectOutputStream stream){
		this.streamToServer = stream;
	}

	public void closeStreamToServer(){
		try {
			this.streamToServer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Writing to the server that the current car is the current race winner.
	 * <br> This functions is called only by the winner car
	 */
	public void WinnerDeclaration(){
		try {
			streamToServer.writeUTF(winnerMsg);
			streamToServer.flush();
			streamToServer.writeInt(id);
			streamToServer.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public int getModelId() {	
		return model_id;
	}

	public Color getColor(){	
		return color;
	}

	public int getRadius()  {	
		return wheelRadius;
	}

	public double getSpeed() {	
		return speed;
	}

	public void setColor(Color color){	
		this.color = color;
		processEvent(eventType.COLOR, new ActionEvent());
	}

	/**
	 * Sets the new speed of the car.
	 * @param speed the new speed of the current car.
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
		processEvent(eventType.SPEED, new ActionEvent());
	}

	public void setRadius(int wheelRadius) {
		this.wheelRadius = wheelRadius;
		processEvent(eventType.RADIUS, new ActionEvent());
	}

	/**
	 * Adding a eventHandler (eventToADD) to the ArrayList in the carHashMap with the matching key et.
	 * @param eventToAdd 
	 * @param et
	 */
	public synchronized void addEventHandler(EventHandler<Event> eventToAdd, eventType et) {	
		ArrayList<EventHandler<Event>> al;
		al = carHashMap.get(et);
		if (al == null)
			al = new ArrayList<EventHandler<Event>>();
		al.add(eventToAdd);
		carHashMap.put(et, al);//The old value (the ArrayList<>) for the key et is replaced by the new 1.
	}

	/**
	 * Remove the wanted eventHandler l from the ArrayList inside of the carHashMap that match with the key et.
	 * <br> The old value (the ArrayList<>) for the key et is replaced by the new one (without the EventHandler<Event> eventToRemove).
	 * @param eventToRemove 
	 * @param et
	 */
	public synchronized void removeEventHandler(EventHandler<Event> eventToRemove, eventType et) {	
		ArrayList<EventHandler<Event>> al;
		al = carHashMap.get(et);
		if (al != null && al.contains(eventToRemove))
			al.remove(eventToRemove);
		carHashMap.put(et, al);
	}

	/**
	 * Creating a message ,which will be print to the system's server log, according to the event.
	 * @param et type of event.
	 * @param e the event.
	 */
	private void processEvent(eventType et, Event e)  {	
		String msg,newValueString;
		ArrayList<EventHandler<Event>> al;
		synchronized (this){ 
			al = carHashMap.get(et);
			if (al == null)
				return;
		}
		switch(et.toString()){
		case "SPEED" : 
			newValueString = " | new speed value: " + speed;
			break;
		case "RADIUS": 
			newValueString =  " | new wheel radus: " + wheelRadius;
			break;
		case "COLOR":
			newValueString =  " | color : " + color;
		default:
			newValueString = " | array size is: " + al.size();
		}
		msg = "CarRaceView" + getModelId() + " | car number: " + (getId() + 1) + " | actionCommand: " + et.toString()
		+ newValueString;

		try {
			streamToServer.writeUTF(speedMsg);
			streamToServer.flush();
			streamToServer.writeUTF(msg);
			streamToServer.flush();
		}
		catch (IOException e1) {		
		}
		for (int i = 0; i < al.size(); i++){
			EventHandler<Event> handler = (EventHandler<Event>) al.get(i);
			handler.handle(e);
		}
	}

	public int getRace_num() {
		return race_num;
	}

	/**
	 * Writing to the system's server log a update about the current situataion of the current car.
	 * @param mesg  - the message that will be print in the server's log.
	 */
	public void writeToServer(String mesg){
		try {
			streamToServer.writeUTF(speedMsg);
			streamToServer.flush();
			streamToServer.writeUTF(mesg);
			streamToServer.flush();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}

