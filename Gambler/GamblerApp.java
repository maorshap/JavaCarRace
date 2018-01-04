package Gambler;

import java.io.IOException;
import java.util.ArrayList;



/**
 * The "main" class that initialize the gambler client application in MVC architecture.
 */
public class GamblerApp {

	static ArrayList<GamblerController> gamblerContList = new ArrayList<>();
	ArrayList<GamblerView> gamblerViewList = new ArrayList<>();

	/**
	 * Creates a new model,view,controller for a Client.
	 */
	public void createGamblerWindow(){
		GamblerView view = new GamblerView();
		GamblerController controller = new GamblerController(view);
		new Thread(controller).start();
		gamblerContList.add(controller);
		gamblerViewList.add(view);
		view.setOnCloseRequest(e->{
			try {
				controller.getModel().exit();
				controller.getModel().getSocket().close();
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	/**
	 * Updates that data at all the gamblers windows.
	 * <br>The data is the names of the waiting races and their cars names.
	 */
	public static void refreashRacesData(){
		for (GamblerController gc : gamblerContList){
			gc.getModel().getCarsNames();
			gc.refreashRacesData();
		}
	}

	/**
	 * Updates the balance textfield inside the view.
	 */
	public static void refreashBalance(){
		for (GamblerController gc : gamblerContList)
			gc.refreshBalance();
	}

	/**
	 * Constraints the gambelr's clients from to bet on a running race.
	 * @param numOfRace the number of the wanted race. identify number.
	 */
	public static void limitBetOnRace (int numOfRace){
		for (GamblerController gc : gamblerContList)
			gc.setCurrentRace(numOfRace);
	}

}
