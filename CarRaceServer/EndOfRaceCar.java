package CarRaceServer;

/**
 * Used for to fill the race result table in the end of the race.
 */
public class EndOfRaceCar {
	
	private String carName;
	private String carPlace;
	
	/**
	 * Creats a EndOfRaceCar instance with unique car name and his place in the race.
	 * @param carName the name of the car.
	 * @param carPlace the place that the car has finished in the race.
	 */
	public EndOfRaceCar(String carName,String carPlace){
		this.carName = carName;
		this.carPlace = carPlace;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getCarPlace() {
		return carPlace;
	}

	public void setCarPlace(String carPlace) {
		this.carPlace = carPlace;
	}
	
	

}
