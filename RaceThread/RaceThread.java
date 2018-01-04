package RaceThread;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import CarRaceServer.CarRace;
import Gambler.GamblerApp;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class RaceThread implements Runnable{//Controller

	private static final int CAR_RACE_SERVER_PORT = 8000;
	private Stage stg;
	private CarRaceServerView view;
	private int threadNumber;
	private int raceNumber;
	private Socket socket;
	private CarRaceServerModel model;
	private Lock lock = new ReentrantLock();
	private Condition isDone = lock.newCondition();

	/**
	 * @param counter the number of the race. (counts from the beging of the system).
	 * @param model class that holds all the data of the race and connects to the system's database.
	 * @param numOfThread the number of race thread .
	 */
	public RaceThread(int counter,CarRaceServerModel model,int numOfThread){
		this.raceNumber = counter;
		this.threadNumber = numOfThread;
		this.model = model;
	}

	public void run() {
		try {
			model.createRaceData(threadNumber,raceNumber);
			model.insertToCarsRaceId(threadNumber);
			socket = new Socket("localhost",CAR_RACE_SERVER_PORT);
			model.setSocket(socket,threadNumber);
			view = new CarRaceServerView(model.getNumberOfCars());
			view.setCarsPanes(model.getCarsPanes(threadNumber));
			model.setThreadView(view,threadNumber);
			model.setThreadLock(lock,threadNumber);
			model.setThreadCondition(isDone,threadNumber);
			if(raceNumber > model.getNumberOfRaces()){
				Platform.runLater(()->{
					CarRace.refreshQueriesWindow();
					GamblerApp.refreashRacesData();
				});
			}
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

		Platform.runLater(new Runnable(){
			public void run(){
				Scene scene = new Scene(view.getBorderPane(), 750, 500);
				stg = new Stage();
				stg.setScene(scene);
				stg.setTitle("CarRaceView" + raceNumber);
				stg.setAlwaysOnTop(true);
				stg.show();
				stg.setOnCloseRequest(new EventHandler<WindowEvent>(){ @Override
					public void handle(WindowEvent event) {	
					if (model.getSocket(threadNumber) != null)
						model.closeSocket(threadNumber);
					for(int i = 0 ; i < model.getAudios().length ; i++)
						model.getAudios()[i].pause();
				}
				});
				scene.widthProperty().addListener(
						new ChangeListener<Number>(){ @Override
							public void changed(ObservableValue<? extends Number> observable,Number oldValue, Number newValue){
							view.setCarPanesMaxWidth(newValue.doubleValue());
						}
						});
			}
		});

		if(threadNumber == 1){
			model.getThreadsList().set(0,this);
			model.isRace1Ready(raceNumber);
		}
		else if(threadNumber == 2){
			model.getThreadsList().set(1,this);
			model.isRace2Ready(raceNumber);
		}
		else{
			model.getThreadsList().set(2,this);
			model.isRace3Ready(raceNumber);
		}


		lock.lock();
		try {//Moving forward only when the current race thread is finished.
			isDone.await();
			Platform.runLater(()->{//When the race is over,the thread shows the race result for 1 minute.
				Scene scene2 = new Scene(view.getBorderPane(),400, 200);
				stg.setScene(scene2);
				stg.setTitle("Race" + raceNumber + " Results");
			});
			isDone.await();
			Platform.runLater(()->{
				stg.close();
			});

		}
		catch (InterruptedException e) {
		}
		finally{
			lock.unlock();
		}


	}
}

