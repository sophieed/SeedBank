package application.controllers;

import static java.util.Arrays.asList;
import static javafx.collections.FXCollections.observableArrayList;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import application.Mode;
import application.currentlygrowing.CurrentlyGrowing;
import application.currentlygrowing.CurrentlyGrowingService;
import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * Controller for the currently growing tab.
 */
@Component
public class CurrentlyGrowingTabController {

	@FXML private VBox currentlyGrowingBox;
	@FXML private TableView<CurrentlyGrowing> sownIndoorsTable;
	@FXML private TableView<CurrentlyGrowing> germinatedIndoorsTable;
	@FXML private TableView<CurrentlyGrowing> establishedIndoorsTable;
	@FXML private TableView<CurrentlyGrowing> sownOutdoorsTable;
	@FXML private TableView<CurrentlyGrowing> germinatedOutdoorsTable;
	@FXML private TableView<CurrentlyGrowing> establishedOutdoorsTable;

	@FXML private TableColumn<CurrentlyGrowing, String> sownIndoorsSeedColumn;
	@FXML private TableColumn<CurrentlyGrowing, String> sownOutdoorsSeedColumn;
	@FXML private TableColumn<CurrentlyGrowing, String> germinatedIndoorsSeedColumn;
	@FXML private TableColumn<CurrentlyGrowing, String> germinatedOutdoorsSeedColumn;
	@FXML private TableColumn<CurrentlyGrowing, String> establishedIndoorsSeedColumn;
	@FXML private TableColumn<CurrentlyGrowing, String> establishedOutdoorsSeedColumn;

	@FXML private Button sowNewButton;
	@FXML private Button viewSowingButton;
	@FXML private Button viewSeedInfoButton;
	@FXML private Button editCurrentlyGrowingRecordButton;
	@FXML private Button finishButton;
	@FXML private Button failButton;

	@FXML private Button progressIndoorsPhase1;
	@FXML private Button progressIndoorsPhase2;
	@FXML private Button progressOutdoorsPhase1;
	@FXML private Button progressOutdoorsPhase2;

	@Autowired private ApplicationContext applicationContext;
	@Autowired private SeedPacketService seedPacketService;
	@Autowired private CurrentlyGrowingService currentlyGrowingService;

	private TableView<CurrentlyGrowing> mostRecentlySelectedTable;
	private List<TableView<CurrentlyGrowing>> tables;
	private Button callingButton;
	private ObservableList<CurrentlyGrowing> currentlyGrowing;


	/**
	 * Initialise the controller.
	 * @param currentlyGrowing The currently growing record to be used
	 */
	void initialise(ObservableList<CurrentlyGrowing> currentlyGrowing) {
		this.currentlyGrowing = currentlyGrowing;

		initialiseCellValueFactories();
		initialiseData(currentlyGrowing);

		tables = 
				asList(sownIndoorsTable, germinatedIndoorsTable, establishedIndoorsTable, 
						sownOutdoorsTable, germinatedOutdoorsTable, establishedOutdoorsTable);


		initialiseTableListener(tables);
		initialiseEventHandling(tables);
	}


	/*
	 * Private helper method to initialise the cell value factories for each table
	 */
	private void initialiseCellValueFactories() {
		setCellValueFactories(sownIndoorsTable, "dateSown");
		setCellValueFactories(sownOutdoorsTable, "dateSown");
		setCellValueFactories(germinatedIndoorsTable, "dateGerminated");
		setCellValueFactories(germinatedOutdoorsTable, "dateGerminated");
		setCellValueFactories(establishedIndoorsTable, "dateEstablished");
		setCellValueFactories(establishedOutdoorsTable, "dateEstablished");
	}


	/*
	 * Private helper method to initialise the data.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialiseData(List<CurrentlyGrowing> currentlyGrowingRecords) {

		SortedList<CurrentlyGrowing> sownIndoors = new SortedList<>(new FilteredList(observableArrayList(currentlyGrowingRecords), getSownIndoors()));
		sownIndoorsTable.setItems(sownIndoors);
		sownIndoors.comparatorProperty().bind(sownIndoorsTable.comparatorProperty());

		SortedList<CurrentlyGrowing> germinatedIndoors = new SortedList<>(new FilteredList(observableArrayList(currentlyGrowingRecords), getGerminatedIndoors()));
		germinatedIndoorsTable.setItems(germinatedIndoors);
		germinatedIndoors.comparatorProperty().bind(germinatedIndoorsTable.comparatorProperty());

		SortedList<CurrentlyGrowing> establishedIndoors = new SortedList<>(new FilteredList(observableArrayList(currentlyGrowingRecords), getEstablishedIndoors()));
		establishedIndoorsTable.setItems(establishedIndoors);
		establishedIndoors.comparatorProperty().bind(establishedIndoorsTable.comparatorProperty());

		SortedList<CurrentlyGrowing> sownOutdoors = new SortedList<>(new FilteredList(observableArrayList(currentlyGrowingRecords), getSownOutdoors()));	
		sownOutdoorsTable.setItems(sownOutdoors);
		sownOutdoors.comparatorProperty().bind(sownOutdoorsTable.comparatorProperty());

		SortedList<CurrentlyGrowing> germinatedOutdoors = new SortedList<>(new FilteredList(observableArrayList(currentlyGrowingRecords), getGerminatedOutdoors()));
		germinatedOutdoorsTable.setItems(germinatedOutdoors);
		germinatedOutdoors.comparatorProperty().bind(germinatedOutdoorsTable.comparatorProperty());

		SortedList<CurrentlyGrowing> establishedOutdoors = new SortedList<>(new FilteredList(observableArrayList(currentlyGrowingRecords), getEstablishedOutdoors()));
		establishedOutdoorsTable.setItems(establishedOutdoors);	
		establishedOutdoors.comparatorProperty().bind(establishedOutdoorsTable.comparatorProperty());
	}


	/*
	 * Private helper method to add a listener to keep track of the most recently selected table
	 */
	private void initialiseTableListener(List<TableView<CurrentlyGrowing>> tables) {
		for (TableView<CurrentlyGrowing> table : tables) {
			table.focusedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					mostRecentlySelectedTable = table;
				}
			});
		}
	}


	/*
	 * Private helper method to initialise the cell value factories for the tables.
	 */
	@SuppressWarnings("unchecked")
	private void setCellValueFactories(TableView<CurrentlyGrowing> table, String dateField) {

		ObservableList<TableColumn<CurrentlyGrowing, ?>> columns = table.getColumns();

		TableColumn<CurrentlyGrowing, String> nameColumn = (TableColumn<CurrentlyGrowing, String>) columns.get(0);

		nameColumn.setCellValueFactory(cellData 
				-> new SimpleStringProperty(seedPacketService.loadById(
						cellData.getValue().getSeedPacket()).get().getName()));

		TableColumn<CurrentlyGrowing, LocalDate> dateColumn = (TableColumn<CurrentlyGrowing, LocalDate>) columns.get(1);

		dateColumn.setCellValueFactory(new PropertyValueFactory<>(dateField));	
	}


	/*
	 * Private helper method to initialise the event handling.
	 */
	private void initialiseEventHandling(List<TableView<CurrentlyGrowing>> tables) {

		// Handle when a user double clicks on an entry
		for (TableView<CurrentlyGrowing> table : tables) {
			table.setOnMouseClicked((MouseEvent event) -> {
				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
					openSowNewPopup(Mode.VIEW);
				}
			});
		}

		initialiseShortcutEventHandling();
		initializeButtonEventHandling();
	}


	/*
	 * Private helper method to initialise event handling when shortcuts are used
	 */
	private void initialiseShortcutEventHandling() {

		// Handles when a user types Cmd+# to launch the edit window in the appropriate mode
		setShortcut(KeyCode.N, Mode.ADD_NEW); // Cmd+N
		setShortcut(KeyCode.V, Mode.VIEW); // Cmd+V
		setShortcut(KeyCode.E, Mode.EDIT); // Cmd+E

		// Handles when a user types Cmd+B to view the seed packet info (next to Cmd+V to view sowing info)
		currentlyGrowingBox.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN).match(event)) {
					viewSeedInfo();
				} 
			}
		});

		// Handles when a user types Cmd+P to 'Quick Progress' the currently growing record
		currentlyGrowingBox.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN).match(event)) {
					quickProgress();
				}
			}
		});
	}


	/*
	 * Private helper method to initialise the event handling specifically where buttons are involved
	 */
	private void initializeButtonEventHandling() {

		List<Button> buttons = new ArrayList<>(
				Arrays.asList(sowNewButton, viewSowingButton, viewSeedInfoButton, 
						editCurrentlyGrowingRecordButton, finishButton, failButton,
						progressIndoorsPhase1, progressIndoorsPhase2,             
						progressOutdoorsPhase1, progressOutdoorsPhase2));

		for (Button button : buttons) {
			button.defaultButtonProperty().bind(button.focusedProperty());
		}

		// Add a listener to keep track of the calling button
		buttons = new ArrayList<>(
				Arrays.asList(progressIndoorsPhase1, progressIndoorsPhase2,             
						progressOutdoorsPhase1, progressOutdoorsPhase2));

		for (Button button : buttons) {	
			button.focusedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					callingButton = button;
				}
			});
		}
	}


	/*
	 * Private helper method to set shortcuts for launching the relevant pop-up in the appropriate mode
	 */
	private void setShortcut(KeyCode keyCode, Mode mode) {
		currentlyGrowingBox.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (new KeyCodeCombination(keyCode, KeyCombination.SHORTCUT_DOWN).match(event)) {
					openSowNewPopup(mode);
				} 
			}
		});
	}


	/*
	 * Open the sow new pop-up in ADD_NEW mode when opened via the button.
	 */
	@FXML
	private void sowNew() {
		openSowNewPopup(Mode.ADD_NEW);
	}


	/*
	 * Open the sow new pop-up in VIEW mode when opened via the button.
	 */
	@FXML
	private void viewSowingDetails() {
		openSowNewPopup(Mode.VIEW);
	}


	/*
	 * Open the sow new pop-up in EDIT mode when opened via the button.
	 */
	@FXML
	private void edit() {
		openSowNewPopup(Mode.EDIT);
	}


	/*
	 * Open the seed packet info in view mode when opened via the button.
	 */
	@FXML 
	private void viewSeedInfo() {
		openViewSeedPacketPopup();
	}


	/*
	 * Open the progress pop-up to handle progressing a currently growing record to the next phase.
	 */
	@FXML
	private void progress() {
		openProgressPopup(grabAppropriateTable());
	}


	/*
	 * Quick progress the currently growing record without opening the dialogue.
	 */
	private void quickProgress() {

		TableView<CurrentlyGrowing> table = mostRecentlySelectedTable;

		CurrentlyGrowing recordToBeProgressed = table.getSelectionModel().getSelectedItem();

		if (table.equals(sownIndoorsTable) || table.equals(sownOutdoorsTable)) {
			recordToBeProgressed.setDateGerminated(LocalDate.now());
			recordToBeProgressed.setNumberGerminated(recordToBeProgressed.getNumberSown());
		} else {
			recordToBeProgressed.setDateEstablished(LocalDate.now());
			recordToBeProgressed.setNumberEstablished(recordToBeProgressed.getNumberGerminated());
		}
		currentlyGrowingService.save(recordToBeProgressed);	
		initialiseData(currentlyGrowing);
	}


	/*
	 * Open the finish dialogue.
	 */
	@FXML 
	private void finish() {
		openFinishPopup();
	}


	/*
	 * Open the fail dialogue.
	 */
	@FXML 
	private void fail() {
		//	openFailPopup();
	}


	/*
	 * Open the pop up window in the relevant mode.
	 */
	private void openSowNewPopup(Mode mode) {

		// Grab the FXML file		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popups/SowNew.fxml"));
		fxmlLoader.setControllerFactory(applicationContext::getBean);

		Stage stage = new Stage();
		try {
			stage.setScene(new Scene((Pane) fxmlLoader.load()));

			SowNewController sowNewController = fxmlLoader.getController();

			sowNewController.initData(currentlyGrowing, mostRecentlySelectedTable, mode);
			refreshStage(stage);

			stage.setTitle(mode == Mode.ADD_NEW ? "Sow New Seeds" : mode.toString()); 
			stage.show();

		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.ERROR, "Failed to create new Window.", e);		
		}
	}


	/*
	 * Open the pop up window to display the seed packet info
	 */
	private void openViewSeedPacketPopup() {
		// Grab the FXML file		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popups/EditSeedPacket.fxml"));
		fxmlLoader.setControllerFactory(applicationContext::getBean);

		Stage stage = new Stage();
		try {
			stage.setScene(new Scene((Pane) fxmlLoader.load()));

			EditSeedPacketController editSeedPacketController = fxmlLoader.getController();

			CurrentlyGrowing currentlyGrowingRecord = mostRecentlySelectedTable.getSelectionModel().getSelectedItem();
			SeedPacket seedPacket = seedPacketService.loadById(currentlyGrowingRecord.getSeedPacket()).get();

			editSeedPacketController.initData(seedPacket);
			stage.setTitle("View Seed Packet"); 
			stage.show();

		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.ERROR, "Failed to create new Window.", e);		
		}
	}


	/*
	 * Open the progress pop up window to move the currently growing record to the next phase
	 */
	private void openProgressPopup(TableView<CurrentlyGrowing> table) {
		// Grab the FXML file		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popups/Progress.fxml"));
		fxmlLoader.setControllerFactory(applicationContext::getBean);

		Stage stage = new Stage();
		try {
			stage.setScene(new Scene((Pane) fxmlLoader.load()));

			ProgressController progressController = fxmlLoader.getController();		

			progressController.initData(table);
			refreshStage(stage);

			stage.setTitle("Progress"); 
			stage.show();

		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.ERROR, "Failed to create new Window.", e);		
		}
	}
	
	
	/*
	 * Open the Finish pop up window to progress the record to finished
	 */
	private void openFinishPopup() {
		// Grab the FXML file		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popups/Finished.fxml"));
		fxmlLoader.setControllerFactory(applicationContext::getBean);

		Stage stage = new Stage();
		try {
			stage.setScene(new Scene((Pane) fxmlLoader.load()));

			FinishedController finishedController = fxmlLoader.getController();		

			finishedController.initData(mostRecentlySelectedTable);
			refreshStage(stage);

			stage.setTitle("Finish"); 
			stage.show();

		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.ERROR, "Failed to create new Window.", e);		
		}
	}


	/*
	 * Private helper method to grab the appropriate table containing the currently growing record 
	 * to progress. Uses the calling button to identify the relevant table.
	 */
	private TableView<CurrentlyGrowing> grabAppropriateTable() {

		// Use the calling button to select the appropriate table for progressing the selected record
		TableView<CurrentlyGrowing> tableForProgression = new TableView<>();

		if (callingButton.equals(progressIndoorsPhase1)) {
			tableForProgression = sownIndoorsTable;
		} else if (callingButton.equals(progressIndoorsPhase2)) {
			tableForProgression = germinatedIndoorsTable;
		} else if (callingButton.equals(progressOutdoorsPhase1)) {
			tableForProgression = sownOutdoorsTable;
		} else if (callingButton.equals(progressOutdoorsPhase2)){
			tableForProgression = germinatedOutdoorsTable;
		}

		return tableForProgression;
	}


	/*
	 * Private helper method to set up stage refresh when a pop-up is closed.
	 */
	private void refreshStage(Stage stage) {
		stage.setOnHidden(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				initialiseData(currentlyGrowing);
			}
		});
	}


	/*
	 * Predicates for list filtering
	 */
	private Predicate<CurrentlyGrowing> getSownIndoors()  {
		return p -> p.getDateSown() != null && p.getDateGerminated() == null && p.isIndoors();
	}

	private Predicate<CurrentlyGrowing> getSownOutdoors()  {
		return p -> p.getDateSown() != null && p.getDateGerminated() == null && !p.isIndoors();
	}

	private Predicate<CurrentlyGrowing> getGerminatedIndoors()  {
		return p -> p.getDateGerminated() != null && p.getDateEstablished() == null && p.isIndoors();
	}

	private Predicate<CurrentlyGrowing> getGerminatedOutdoors()  {
		return p -> p.getDateGerminated() != null && p.getDateEstablished() == null && !p.isIndoors();
	}

	private Predicate<CurrentlyGrowing> getEstablishedIndoors()  {
		return p -> p.getDateEstablished() != null && p.isIndoors();
	}

	private Predicate<CurrentlyGrowing> getEstablishedOutdoors()  {
		return p -> p.getDateEstablished() != null && !p.isIndoors();
	}

}
