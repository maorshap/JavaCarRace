package Gambler;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javafx.application.Platform;

/**
 * The controller part of the gamber's clients MVC architecture.
 */
public class GamblerController implements Runnable {

	static int currentRace = 0;//The number of the current running race.
	private static final int GAMBLER_SERVER_PORT = 8888;
	private int currentBet;
	private ArrayList<String> carsNames1 = new ArrayList<>();
	private ArrayList<String> carsNames2 = new ArrayList<>();
	private ArrayList<String> carsNames3 = new ArrayList<>();
	private ArrayList<String> racesNames = new ArrayList<>();
	private GamblerView view;
	private GamblerModel model;

	/**
	 * Stores all the neccasery data in the view and define actions per user choice.
	 */
	public void createViewData(){
		view.racesBox.valueProperty().addListener(e->{
			if(view.racesBox.getSelectionModel().getSelectedItem() == racesNames.get(0)){
				view.changeCarsBox(1);
				view.changeView();
			}
			else if (view.racesBox.getSelectionModel().getSelectedItem() == racesNames.get(1)){			
				view.changeCarsBox(2);
				view.changeView();
			}
			else{
				view.changeCarsBox(3);
				view.changeView();
			}
		});

		view.confirmButton.setOnAction(e->{
			int raceId = Integer.parseInt(view.racesBox.getSelectionModel().getSelectedItem());
			if (currentRace == raceId )
				view.warningAlert("The Race with Id: " + raceId +" has start already.");
			else{
				model.drawMoney(currentBet);
				model.completeBet(currentBet,view.carsBox.getSelectionModel().getSelectedItem(),raceId);
				refreshBalance();
				for (int i = 0 ; i < 3 ; i++){
					if(model.getNumOfRaceArr()[i] == raceId)
						raceId = i+1;
				}
			}
			model.getRaceReady(raceId);
			view.changeView();
		});

		view.betButton.setOnAction(e->{
			try{
				this.currentBet = Integer.parseInt(view.betField.getText());
				if(currentBet <= 0)
					view.errorAlert("We are accepts only positive amount of money");
				else
					view.createConfirmView();
			}
			catch (Exception ex){
				view.warningAlert("Bets text field can only contain numeric values");
			}

		});

		view.returnButton.setOnAction(e->{
			Platform.runLater(()->{
				view.ballaneField.setText(String.valueOf(model.getBalance()));
			});
			view.changeView();
		});

		view.exitButton.setOnAction(e->{
			model.exit();
			view.close();
		});

	}

	public GamblerController(GamblerView view){
		this.view = view;
	}

	public void run(){
		try {
			model = new GamblerModel(new Socket("localhost",GAMBLER_SERVER_PORT));
			carsNames1.addAll(model.getRace1Cars());
			carsNames2.addAll(model.getRace2Cars());
			carsNames3.addAll(model.getRace3Cars());
			racesNames.addAll(model.getRacesNames());

			Platform.runLater(()->{
				view.show();
			});
			view.loginButton.setOnAction(e->{
				if(model.checkUser(view.tfUser.getText(),view.passwordField.getText()) == 1){
					view.createComboBoxes(racesNames,carsNames1,carsNames2,carsNames3);
					createViewData();
					refreshBalance();
					view.changeView();
				}
				else
					view.setErrorText("User name/password incorrect");
			});

			view.signInButton.setOnAction(e->{
				if (view.passwordField.getText().trim().isEmpty()){
					view.setErrorText("Please enter password");
					return;
				}
				if((model.newUser(view.tfUser.getText(),view.passwordField.getText())) == 1){
					view.createComboBoxes(racesNames,carsNames1,carsNames2,carsNames3);
					createViewData();
					view.changeView();
				}
				else
					view.setErrorText("User name is already exist");
			});
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public GamblerModel getModel(){
		return model;
	}

	public GamblerView getView(){
		return view;
	}

	/**
	 * Refreash the Races names and cars data.
	 */
	public void refreashRacesData(){
		carsNames1 = new ArrayList<>(model.getRace1Cars());
		carsNames2 = new ArrayList<>(model.getRace2Cars());
		carsNames3 = new ArrayList<>(model.getRace3Cars());
		racesNames = new ArrayList<>(model.getRacesNames());

		if(model.isOnline()){
			Platform.runLater(()->{
				view.createComboBoxes(racesNames,carsNames1,carsNames2,carsNames3);
				view.racesBox.valueProperty().addListener(e->{
					if(view.racesBox.getSelectionModel().getSelectedItem() == racesNames.get(0)){
						view.changeCarsBox(1);
						view.changeView();
					}
					else if (view.racesBox.getSelectionModel().getSelectedItem() == racesNames.get(1)){			
						view.changeCarsBox(2);
						view.changeView();
					}
					else{
						view.changeCarsBox(3);
						view.changeView();
					}
				});
				view.changeView();
			});
		}
	}



	public void refreshBalance(){
		if(model.isOnline()){
		Platform.runLater(()->{
			view.ballaneField.setText(String.valueOf(model.getBalance()));
		});
		}
	}

	/**
	 * Sets the current race number of the running race.
	 * @param numOfRace the identify number of the race.
	 */
	public void setCurrentRace(int numOfRace){
		GamblerController.currentRace = numOfRace;
	}	

}
