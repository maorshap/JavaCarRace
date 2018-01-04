package CarRaceServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import javafx.application.Platform;


	/**
	 * This class is charge on handle the new race thread.<br>This class is uses inside the CarRaceServerWindow.
	 */
	public class CarRaceHandler implements Runnable{

	private static final String SPEED_EVENT = "SPEED_EVENT";
	private static final String WINNER_EVENT = "WINNER_EVENT";
	private static final String UPDATE_EVENT = "UPDATE_SERVER";
	private Socket socketHandler;
	private int clientNum;
	private ObjectInputStream streamFromClient;
	private String typeOfMessage;

	/**
	 * CarRaceHandler gets the server's sockets and the race number.
	 * @param socket the socket that eastablish the connection.
	 * @param cm the current client number.
	 */
	public CarRaceHandler(Socket socket,int cm){
		this.socketHandler = socket;
		this.clientNum = cm;
	}

	public void run(){
		try {
			streamFromClient = new ObjectInputStream(socketHandler.getInputStream());
			while(true){
				typeOfMessage = streamFromClient.readUTF();

				switch (typeOfMessage) {

				case SPEED_EVENT:
					String mesg1 = streamFromClient.readUTF();
					Platform.runLater(()->{
						CarRaceServerWindow.printMsg("Client " + clientNum + "'s : " + mesg1 );
					});
					break;

				case WINNER_EVENT:
					CarRaceServerWindow.setWinnerCarId(streamFromClient.readInt());
					break;
					
				case UPDATE_EVENT:
					String mesg2 = streamFromClient.readUTF();
					Platform.runLater(()->{
						CarRaceServerWindow.printMsg(mesg2);
					});
					break;
				}
			}
		}
		catch (IOException e) {
			try {
				socketHandler.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
}
