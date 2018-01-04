package RaceThread;


import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

public class CarRaceServerView{

	private BorderPane border_pane;
	private GridPane  cars_grid;
	private CarPane[] carsPanes;
	private int numOfCars;
	
	
	public CarRaceServerView(int num_of_cars){
		this.numOfCars = num_of_cars;
		carsPanes = new CarPane[numOfCars];
		border_pane = new BorderPane();
	}

	public void setCarsPanes(CarPane[] panes){
		for(int i = 0 ; i < numOfCars; i++)
			this.carsPanes[i] = panes[i];
		createCarsGrid();
		border_pane.setCenter(cars_grid);
	}

	/**
	 * Creats the "roads"
	 */
	public void createCarsGrid(){	
		cars_grid = new GridPane();
		for(int i = 0 ; i < numOfCars ; i++)
			cars_grid.add(carsPanes[i],0,i);
		cars_grid.setStyle("-fx-background-color: beige");
		cars_grid.setGridLinesVisible(false);

		ColumnConstraints column = new ColumnConstraints();
		column.setPercentWidth(100);
		cars_grid.getColumnConstraints().add(column);

		RowConstraints row = new RowConstraints();
		row.setPercentHeight(50);
		for(int i = 0 ; i < numOfCars ; i++)
			cars_grid.getRowConstraints().add(row);
	}

	public void createAllTimelines(){
		for(CarPane cp : carsPanes)
			cp.createTimeline();
		
	}

	public BorderPane getBorderPane(){
		return border_pane;
	}
	
	public void setBorderPane(BorderPane bp){
		this.border_pane = bp;
	}

	public GridPane getCarsGrid() {
		return cars_grid;
	}

	public void setCarPanesMaxWidth(double newWidth) {	
		for(CarPane cp : carsPanes)
			cp.setMaxWidth(newWidth);
	}

	public Pane getCarPane1() {	
		return carsPanes[0];
	}
	public Pane getCarPane2() {	
		return  carsPanes[1];
	}
	public Pane getCarPane3() {	
		return  carsPanes[2];
	}
	public Pane getCarPane4() {	
		return  carsPanes[3];
	}
	public Pane getCarPane5() {	
		return  carsPanes[4];
	}
}



