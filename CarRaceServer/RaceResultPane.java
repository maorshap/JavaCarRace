package CarRaceServer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

/** 
 *This class is used as the pane for the car result table that appear in the end of each race.
 */
public class RaceResultPane extends BorderPane{
	
	private final int WIDTH =340;
	private final int HEIGHT = 400;
	private String nameProperty = "carName";
	private String placeProperty = "carPlace";
	private String columnNameTitle = "Car_Name";
	private String columnPlaceTitle = "Car_Place";
	private TableColumn<EndOfRaceCar,String> nameColumn,placeColumn;
	private TableView<EndOfRaceCar> resultTable;
	public ObservableList<EndOfRaceCar> data;
	
	@SuppressWarnings("unchecked")
	public RaceResultPane(){
		data = FXCollections.observableArrayList();
		resultTable = new TableView<>();
		resultTable.setItems(data);
		resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		nameColumn = createColumn(columnNameTitle, nameProperty);
		nameColumn.setStyle( "-fx-alignment: CENTER;");
		placeColumn = createColumn(columnPlaceTitle, placeProperty);
		placeColumn.setStyle( "-fx-alignment: CENTER;");
		resultTable.getColumns().addAll(nameColumn,placeColumn);
		this.setCenter(resultTable);
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
	}
	
	public TableColumn<EndOfRaceCar,String> createColumn(String columnName,String strProperty){
		TableColumn<EndOfRaceCar,String> column = new TableColumn<>(columnName);
		column.setCellValueFactory(new PropertyValueFactory<>(strProperty));
		column.setMinWidth(200);
		return column;
	}
	
}
