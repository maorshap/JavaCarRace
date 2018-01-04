package RaceThread;

import CarRaceServer.CarRace;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 *Charges of the grahpics of a individual car and his place in the race.
 */
public class CarPane extends Pane implements CarEvents{

	class SpeedEvent implements EventHandler<Event>{ @Override
		public void handle(Event event){ 
		setSpeed(car.getSpeed());
	}
	}

	class ColorEvent implements  EventHandler<Event>{ @Override
		public void handle(Event event){	
		setColor(car.getColor());
	}
	}

	class RadiusEvent implements EventHandler<Event> { @Override
		public void handle(Event event){
		setRadius(car.getRadius());
	}
	}

	private static int carPlace = 0;
	private boolean flag = false;
	private int place;
	private MediaPlayer endRaceAudio = new MediaPlayer(new Media("file://" + CarRace.class.getClassLoader().getResource("RaceThread/Cheering.mp3").getPath()));
	final int MOVE=1;
	final int STOP=0;
	final int SMALL_SIZE = 2;
	final int MEDIUM_SIZE = 15;
	final int LARGE_SIZE = 35;
	private double xCoor;
	private double yCoor;
	private Timeline tl0,tl1,tl2,tl3; // speed=setRate()
	private Color color;
	private int r;// radius
	private Car car;
	private int numOfCars;
	private int maxSpeed;
	private int changeSpeedRate;
	CarProperties.Type type;


	/**
	 * CarPane instance gets a random Car's type,size and color and build a 3d car accordingly.
	 * @param c the car.
	 * @param numOfCars the number of the cars in the race.
	 * @param speed the max speed that a car can drive in.
	 * @param rate the cycle of time that in the end a car will change his speed in the race.
	 * @param type the type of the car.
	 * @param color the color of the car.
	 * @param size the size of the car.
	 */
	public CarPane(Car c,int numOfCars,int speed,int rate,CarProperties.Type type,CarProperties.Color color,CarProperties.Size size){
		this.car = c;
		this.numOfCars = numOfCars;
		this.maxSpeed = speed;
		this.changeSpeedRate = rate;
		this.type = type;
		setCarColor(color);
		setCarSize(size);
		car.setCarName("-" + type.toString() + "-" + color.toString());
		xCoor = 0;
		if (car != null){ 
			car.addEventHandler(new SpeedEvent(), eventType.SPEED);
			car.addEventHandler(new ColorEvent(), eventType.COLOR);
			car.addEventHandler(new RadiusEvent(), eventType.RADIUS);
		}
	}

	public void setColor(Color color){	
		this.color = color;
	}

	public void setFlag(boolean bol){
		this.flag = true;
	}

	public Car getCarModel() {	
		return car;
	}

	public int getPlace(){
		return place;
	}

	/**
	 * Moving the car in the race process and in the end, initailize the current car place in the race.
	 * <br> This funcation is being called in reliance of the current car's speed.
	 * @param n the speed of the car.
	 */
	public void moveCar(int n) {
		yCoor = getHeight();
		setMinSize(10 * r, 6 * r);
		if (xCoor > getWidth()){
			if(flag){
				place = (++carPlace);
				if(place == numOfCars){
					carPlace = 0;
					endRaceAudio.play();
					endRaceAudio.setOnEndOfMedia(()->{
						endRaceAudio.getOnReady();
						endRaceAudio.seek(Duration.ZERO);
						endRaceAudio.stop();
					});
				}
				if(place == 1){
					System.out.println((car.getId()+1) + " is the winner!");
					car.WinnerDeclaration();
				}
				stopTimelines();
				System.out.println((car.getCarName()) +" is at the "+place+"'st place");
				car.writeToServer((car.getCarName()) +" is at the "+place+"'st place");
				return;
			}
			xCoor = -10 * r;
		} 
		else{ 
			xCoor += n;
		}

		// Draw the car
		drawCar(this.type);
	}

	/**
	 * Draws a 3D car dependence on the car's type.
	 * @param type the type of the car
	 */
	public void drawCar(CarProperties.Type type)
	{
		// Draw the wheels
		Cylinder wheel = new Cylinder(15+r/2,20);
		wheel.setTranslateX(xCoor+170);
		wheel.setTranslateY(125);
		wheel.setTranslateZ(365);
		wheel.setRotate(90);
		wheel.setRotationAxis(Rotate.X_AXIS);

		Cylinder wheel2 = new Cylinder(15+r/2,20);
		wheel2.setTranslateX(xCoor+15);
		wheel2.setTranslateY(125);
		wheel2.setTranslateZ(365);
		wheel2.setRotate(90);
		wheel2.setRotationAxis(Rotate.X_AXIS);

		Cylinder wheel3 = new Cylinder(15+r/2,20);
		wheel3.setTranslateX(xCoor+190);
		wheel3.setTranslateY(122);
		wheel3.setTranslateZ(365);
		wheel3.setRotate(90);
		wheel3.setRotationAxis(Rotate.X_AXIS);

		Cylinder wheel4 = new Cylinder(15+r/2,20);
		wheel4.setTranslateX(xCoor+35);
		wheel4.setTranslateY(122);
		wheel4.setTranslateZ(365);
		wheel4.setRotate(90);
		wheel4.setRotationAxis(Rotate.X_AXIS);

		Box carBody = new Box(220+r, 65+r , 80+r);
		carBody.setTranslateX(xCoor+103);
		carBody.setTranslateY(85);
		carBody.setTranslateZ(400);
		carBody.setRotate(-7);
		carBody.setRotationAxis(Rotate.X_AXIS);

		//				 Create a Light
		PointLight light = new PointLight();
		light.setTranslateX(250);
		light.setTranslateY(150);
		light.setTranslateZ(250);

		PointLight secondLight = new PointLight();
		secondLight.setTranslateX(450);
		secondLight.setTranslateY(0);
		secondLight.setTranslateZ(250);	

		PhongMaterial wheelMaterial = new PhongMaterial(); 

		Image wheelImg = new Image("file://"+CarRace.class.getClassLoader().getResource("RaceThread/wheel.jpg").getPath());
		wheelMaterial.setDiffuseMap(wheelImg);

		Box road = new Box(getWidth()*3, 10, 10);
		road.setTranslateX(50);
		road.setTranslateY(145);
		road.setTranslateZ(365);

		PhongMaterial partsColorMaterial = new PhongMaterial();
		if(color == null){
			color = Color.WHITE;
		}
		partsColorMaterial.setDiffuseColor(color);
		carBody.setMaterial(partsColorMaterial);
		wheel.setMaterial(wheelMaterial);
		wheel2.setMaterial(wheelMaterial);
		wheel3.setMaterial(wheelMaterial);
		wheel4.setMaterial(wheelMaterial);

		double roofHeight  = Math.sqrt(2* Math.pow(70+r, 2));

		Group g = new Group();

		PerspectiveCamera camera = new PerspectiveCamera(false);
		camera.setFieldOfView(15);
		getScene().setCamera(camera);
		switch(type)
		{
			case Ferrari:

				Box leftSide = new Box(70+r, 70+r, 70+r);
				leftSide.setTranslateX(xCoor+60);
				leftSide.setTranslateY(65);
				leftSide.setTranslateZ(400);
				leftSide.setRotate(45);				

				Box carRoof = new Box(80+r, 35+r,80+r);
				carRoof.setTranslateX(leftSide.getTranslateX()+(roofHeight/2)-11);
				carRoof.setTranslateY(leftSide.getTranslateY()-35);
				carRoof.setTranslateZ(400);

				Box rightSide = new Box(70+r, 70+r, 70+r);
				rightSide.setTranslateX(xCoor+94+(roofHeight/2));
				rightSide.setTranslateY(65);
				rightSide.setTranslateZ(400);
				rightSide.setRotate(135);

				PhongMaterial audiMaterial = new PhongMaterial();
				//Image audiImg =	new Image("file://"+CarRace.class.getClassLoader().getResource("RaceThread/audi.png").getPath());
				//audiMaterial.setBumpMap(audiImg);
				audiMaterial.setDiffuseColor(color);

				leftSide.setMaterial(partsColorMaterial);
				carBody.setMaterial(partsColorMaterial);
				carRoof.setMaterial(audiMaterial);
				rightSide.setMaterial(partsColorMaterial);

				getChildren().clear();
				g.getChildren().addAll(wheel3,wheel4,rightSide,leftSide,carRoof,carBody,wheel,wheel2,light,road);
				g.setTranslateZ(-200);
				g.setTranslateY(30);
				getChildren().addAll(g);
				break;

			case Audi:

				Cylinder circleRoof = new Cylinder(55+r/2, 15);
				circleRoof.setTranslateX(xCoor+50+(roofHeight/2));
				circleRoof.setTranslateY(60);
				circleRoof.setTranslateZ(400);
				circleRoof.setRotate(90);
				circleRoof.setRotationAxis(Rotate.X_AXIS);

				PhongMaterial lamboMaterial = new PhongMaterial();
				//Image lamboImg = new Image("file://"+CarRace.class.getClassLoader().getResource("RaceThread/lambo.jpg").getPath());
				//lamboMaterial.setBumpMap(lamboImg);
				lamboMaterial.setDiffuseColor(color);

				circleRoof.setMaterial(lamboMaterial);

				getChildren().clear();
				g.getChildren().addAll(wheel3,wheel4,circleRoof,carBody,wheel,wheel2,light,road);
				g.setTranslateZ(-200);
				g.setTranslateY(30);
				getChildren().addAll(g);
				break;

			case Lamborghini:

				Box newCarRoof = new Box(80+r, 35+r,80+r);
				newCarRoof.setTranslateX(xCoor+49+(roofHeight/2));
				newCarRoof.setTranslateY(30);
				newCarRoof.setTranslateZ(400);

				PhongMaterial ferraiMaterial = new PhongMaterial(); 
				//Image ferrariImg = new Image("file://"+CarRace.class.getClassLoader().getResource("RaceThread/logofer.jpg").getPath());	
				//ferraiMaterial.setBumpMap(ferrariImg);
				ferraiMaterial.setDiffuseColor(color);

				newCarRoof.setMaterial(ferraiMaterial);

				getChildren().clear();
				g.getChildren().addAll(wheel3,wheel4,newCarRoof,carBody,wheel,wheel2,light,road);
				g.setTranslateZ(-200);
				g.setTranslateY(30);
				getChildren().addAll(g);
				break;
		}

	}

	/**
	 * Creates timelines that activate when the race start.
	 */
	public void createTimeline(){

		EventHandler<ActionEvent> motionHandler = e ->{ 
			moveCar(MOVE); // move car pane according to limits
		};

		EventHandler<ActionEvent> speendHandler = e->{
			car.setSpeed((Math.floor(100*Math.random()*(maxSpeed))/100)+10);
		};

		tl1 = new Timeline();
		tl1.setCycleCount(Timeline.INDEFINITE);
		KeyFrame moveFrame = new KeyFrame(Duration.millis(50), motionHandler);
		tl1.getKeyFrames().add(moveFrame);

		tl2 = new Timeline();
		tl2.setCycleCount(Timeline.INDEFINITE);
		KeyFrame speedFrame = new KeyFrame(Duration.millis(1000*changeSpeedRate),speendHandler);
		tl2.getKeyFrames().add(speedFrame);

		EventHandler<ActionEvent> getReadyHandler = e->{
			moveCar(0);
		};

		EventHandler<ActionEvent> startRaceHandler = e->{
			car.setSpeed((Math.floor(100*Math.random()*(maxSpeed))/100)+5);
			tl1.play();//Move car
			tl2.playFrom(Duration.millis(3000));//Random speed
		};

		tl3 = new Timeline(new KeyFrame(Duration.millis(50),getReadyHandler));
		tl3.setCycleCount(1);
		tl3.play();

		tl0 = new Timeline(new KeyFrame(Duration.millis(6250),startRaceHandler));
		tl0.setCycleCount(1);
		tl0.play();
	}

	public void setSpeed(double speed) {
		if (speed == STOP){
			tl1.stop();
		}
		else{ 
			tl1.setRate(speed);
			tl1.play();
		}
	}

	public double getX(){
		return xCoor;
	}

	public double getY(){	
		return yCoor;
	}

	public void stopTimelines(){
		tl1.pause();
		tl2.pause();
	}

	/**
	 * Sets the current color of the car according to the given random color that the car gets in the construct of the object instance.
	 * @param color the color of the car
	 */
	public void setCarColor(CarProperties.Color color){
		switch(color){
			case RED:
				setColor(Color.RED);
				break;
			case AQUA:
				setColor(Color.AQUA);
				break;
			case BLUE:
				setColor(Color.BLUE);
				break;
			case GREEN:
				setColor(Color.GREEN);
				break;
			case YELLOW:
				setColor(Color.YELLOW);
				break;
			case ORANGE:
				setColor(Color.ORANGE);
				break;
			case PINK:
				setColor(Color.PINK);
				break;
			case WHITE:
				setColor(Color.WHITE);
				break;
		}
	}

	/**
	 * Sets the size of the car according to the given random size that the car gets in the construct of the object instance.
	 * @param r the car's radius size.
	 */
	public void setRadius(int carRadiusSize) {	
		this.r = carRadiusSize;
	}

	public void setCarSize(CarProperties.Size size){
		switch(size){
			case Small:setRadius(SMALL_SIZE);
			break;
			case Medium:setRadius(MEDIUM_SIZE);
			break;
			case Large:setRadius(LARGE_SIZE);
			break;
		}
	}

}



