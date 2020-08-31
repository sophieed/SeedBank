package application.controllers;

import static application.Mode.ADD_NEW;
import static application.Mode.EDIT;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Comparator.comparing;
import static javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import application.Mode;
import application.currentlygrowing.CurrentlyGrowing;
import application.currentlygrowing.CurrentlyGrowingService;
import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;


/**
 * Controller for the sow new pop up screen.
 */
@Controller
@Scope("prototype")
public class SowNewController {

	@FXML private ComboBox<SeedPacket> seedComboBox;
	@FXML private RadioButton indoorsRadio;
	@FXML private RadioButton outdoorsRadio;
	@FXML private DatePicker dateSown;
	@FXML private TextField numberSown;
	@FXML private DatePicker dateGerminated;
	@FXML private TextField numberGerminated;
	@FXML private DatePicker dateEstablished;
	@FXML private TextField numberEstablished;
	@FXML private HBox germinatedData;
	@FXML private HBox establishedData;
	@FXML private Label numberRemaining;
	@FXML private TextField locationStored;
	@FXML private TextArea notes;
	@FXML private Button saveNewButton;
	@FXML private Button cancelButton;

	@Autowired private SeedPacketService seedPacketService;
	@Autowired private CurrentlyGrowingService currentlyGrowingService;
	@Autowired private ApplicationContext applicationContext;

	private CurrentlyGrowing currentlyGrowingRecord = new CurrentlyGrowing();
	private String failureMessage = "";
	private LocalDate today = LocalDate.now();
	private ObservableList<CurrentlyGrowing> currentlyGrowing;
	private TableView<CurrentlyGrowing> table;
	private Mode mode;
	private Integer priorNumberSown;


	/**
	 * Initialise the data.
	 */
	void initData(ObservableList<CurrentlyGrowing> currentlyGrowing, 
			TableView<CurrentlyGrowing> table, Mode mode) {
		this.currentlyGrowing = currentlyGrowing;
		this.table = table;
		this.mode = mode;
		setMode(mode, table);
		setDefaultValues();
		mapModelToFields();	
		initialiseEventHandling();
	}


	/**
	 * Method to map the values displayed in the fields to the model.
	 */
	void mapFieldsToModel() {
		currentlyGrowingRecord.setSeedPacket(seedComboBox.getValue().getId());
		currentlyGrowingRecord.setIndoors(indoorsRadio.isSelected());
		currentlyGrowingRecord.setLocation(locationStored.getText());
		currentlyGrowingRecord.setDateSown(dateSown.getValue());
		currentlyGrowingRecord.setNumberSown(Integer.valueOf(numberSown.getText()));
		currentlyGrowingRecord.setDateGerminated(dateGerminated.getValue());
		currentlyGrowingRecord.setNumberGerminated(numberGerminated.getText() == null || numberGerminated.getText().isBlank() ? null : Integer.valueOf(numberGerminated.getText()));
		currentlyGrowingRecord.setDateEstablished(dateEstablished.getValue());
		currentlyGrowingRecord.setNumberEstablished(numberEstablished.getText() == null || numberEstablished.getText().isBlank() ? null : Integer.valueOf(numberEstablished.getText()));		
		currentlyGrowingRecord.setNotes(notes.getText());
	}


	/*
	 * Private helper method to set the default field values.
	 */
	private void setDefaultValues() {
		setUpSeedPacketComboBox();

		ToggleGroup toggleGroup = new ToggleGroup();
		indoorsRadio.setToggleGroup(toggleGroup);
		outdoorsRadio.setToggleGroup(toggleGroup);
		indoorsRadio.setSelected(true);

		dateSown.setValue(today);

		// Set visibility of the progression data
		germinatedData.managedProperty().bind(germinatedData.visibleProperty());
		establishedData.managedProperty().bind(establishedData.visibleProperty());

		germinatedData.setVisible(currentlyGrowingRecord.getDateGerminated() != null);
		establishedData.setVisible(currentlyGrowingRecord.getDateEstablished() != null);
	}


	/*
	 * Private helper method to map the model values to the fields displayed.
	 */
	private void mapModelToFields() {
		if (currentlyGrowingRecord.getSeedPacket() != null) {
			seedComboBox.setValue(seedPacketService.loadById(currentlyGrowingRecord.getSeedPacket()).get());
			indoorsRadio.setSelected(currentlyGrowingRecord.isIndoors());
			outdoorsRadio.setSelected(!currentlyGrowingRecord.isIndoors());
			locationStored.setText(currentlyGrowingRecord.getLocation());
			dateSown.setValue(currentlyGrowingRecord.getDateSown());
			dateGerminated.setValue(currentlyGrowingRecord.getDateGerminated());
			dateEstablished.setValue(currentlyGrowingRecord.getDateEstablished());

			priorNumberSown = currentlyGrowingRecord.getNumberSown(); // Used in edit mode only
			numberSown.setText(priorNumberSown.toString());
			numberGerminated.setText(currentlyGrowingRecord.getNumberGerminated() != null ? currentlyGrowingRecord.getNumberGerminated().toString() : null);
			numberEstablished.setText(currentlyGrowingRecord.getNumberEstablished() != null ? currentlyGrowingRecord.getNumberEstablished().toString() : null);
			notes.setText(currentlyGrowingRecord.getNotes());
		}
	}


	/*
	 * Method to save the new currently growing record and close the window, triggered when the user presses 
	 * the relevant button.
	 */
	@FXML
	private void save() {
		if (isValid()) {
			try {
				mapFieldsToModel();
				currentlyGrowingService.save(currentlyGrowingRecord);
				deductSownSeedsFromPack();
				refreshTableView();
				saveNewButton.getScene().getWindow().hide();
			} catch (ConstraintViolationException e) {
				populateConstraintViolationMessages(e);
			}
		}

		if (failureMessage.length() > 0) {
			openValidationFailurePopup(failureMessage);
		}
	}


	/*
	 * Method to cancel, and close the window, triggered when the user presses the relevant button.
	 */
	@FXML
	private void cancel() {
		saveNewButton.getScene().getWindow().hide();
	}


	/*
	 * Method to set the number remaining label from the seed packet once selected from the combobox.
	 */
	@FXML
	private void seedSelected() {

		numberRemaining.setText(""); // Default back to blank to prevent value being carried over

		Integer numberRemainingValue = seedComboBox.getValue().getNumberRemaining();
		if (numberRemainingValue > 0) {
			numberRemaining.setText("~ " + numberRemainingValue.toString() + " remaining");
		}
	}


	/*
	 * Private helper method to apply settings dependent on the application mode.
	 */
	private void setMode(Mode mode, TableView<CurrentlyGrowing> currentlyGrowing) {

		boolean editable = false; 
		boolean saveButtonDisabled = true;
		boolean seedFieldEditable = false;

		switch (mode) {

		case ADD_NEW : 
			editable = true;
			seedFieldEditable = true;
			saveButtonDisabled = false;
			break;

		case EDIT : 
			currentlyGrowingRecord = currentlyGrowing.getSelectionModel().getSelectedItem();
			editable = true;
			seedFieldEditable = false;
			saveButtonDisabled = false;
			break;

		case VIEW : 
			currentlyGrowingRecord = currentlyGrowing.getSelectionModel().getSelectedItem();
			editable = false;
			seedFieldEditable = false;
			saveButtonDisabled = true;
			break;

		case DELETE:
			break;
		}

		setEditable(editable, seedFieldEditable, saveButtonDisabled);
	}


	/*
	 * Private helper method to set which fields are editable.
	 */
	private void setEditable(boolean editable, boolean seedFieldEditable, boolean saveButtonDisabled) {
		seedComboBox.setDisable(!seedFieldEditable);
		indoorsRadio.setDisable(!editable);
		outdoorsRadio.setDisable(!editable);
		locationStored.setEditable(editable);
		locationStored.setDisable(!editable);
		dateSown.setDisable(!editable);
		numberSown.setEditable(editable);
		numberSown.setDisable(!editable);
		dateGerminated.setDisable(!editable);
		numberGerminated.setEditable(editable);
		numberGerminated.setDisable(!editable);	
		dateEstablished.setDisable(!editable);
		numberEstablished.setEditable(editable);
		numberEstablished.setDisable(!editable);	
		notes.setEditable(editable);
		notes.setDisable(!editable);

		saveNewButton.setDisable(saveButtonDisabled);
	}


	/*
	 * Private helper method to set up the behaviour of the seed packet combo box.
	 */
	private void setUpSeedPacketComboBox() {
		List<SeedPacket> seedPackets = seedPacketService.loadAll();	

		seedPackets.sort(comparing(SeedPacket::getName, CASE_INSENSITIVE_ORDER));

		seedComboBox.getItems().setAll(seedPackets);

		seedComboBox.setConverter(new StringConverter<SeedPacket>() {

			@Override
			public String toString(SeedPacket object) {
				return (object == null ? null : object.getName());
			}

			@Override
			public SeedPacket fromString(String string) {
				return seedPacketService.loadByName(string);
			}
		});
	}


	/*
	 * Private helper method to apply validation (not related to constraint violations on the CurrentlyGrowing 
	 * domain object) prior to saving it in the database. 
	 */
	private boolean isValid() {

		failureMessage = ""; // Ensure message is wiped from any previous validation attempts

		// Seed Packet must be set
		if (seedComboBox.getValue() == null) {
			failureMessage += "Seed packet must be set\n";
		}

		// Checks that the number sown is a valid number
		String numberSownInput = numberSown.getText();
		if (numberSownInput != null && !isNumeric(numberSownInput)) {
			failureMessage += "Invalid number sown\n";
		}

		// Checks that the number sown is greater than zero
		if (numberSownInput != null && isNumeric(numberSownInput) 
				&& Integer.valueOf(numberSownInput) < 1) {
			failureMessage += "The number sown must be greater than zero\n";
		}

		// Checks that the date sown is not in the future
		LocalDate dateSownInput = dateSown.getValue();
		if (dateSownInput != null && dateSownInput.isAfter(today)) {
			failureMessage += "The date sown cannot be in the future\n";
		}

		// Checks that the number germinated is a valid number
		String numberGerminatedInput = numberGerminated.getText();

		if (numberGerminatedInput != null && !numberGerminatedInput.isBlank() 
				&& !isNumeric(numberGerminatedInput)) {
			failureMessage += "Invalid number germinated\n";
		}

		// Checks that the number germinated is greater than zero
		if (numberGerminatedInput != null && !numberGerminatedInput.isBlank() 
				&& isNumeric(numberGerminatedInput) 
				&& Integer.valueOf(numberGerminatedInput) < 1) {
			failureMessage += "The number germinated must be greater than zero\n";
		}

		// Make sure the number germinated is not greater than the number sown.
		if (numberGerminatedInput != null && !numberGerminatedInput.isBlank() 
				&& isNumeric(numberGerminatedInput) 
				&& Integer.valueOf(numberGerminatedInput) > Integer.valueOf(numberSownInput)) {
			failureMessage += "The number germinated must not be greater than the number sown\n";
		}
		
		// Checks that the date germinated is not in the future
		LocalDate dateGerminatedInput = dateGerminated.getValue();
		if (dateGerminatedInput != null && dateGerminatedInput.isAfter(today)) {
			failureMessage += "The date germinated cannot be in the future\n";
		}
			
		// Checks that the germinated date is not before sown date
		if (dateGerminatedInput != null && dateGerminatedInput.isBefore(dateSownInput)) {
			failureMessage += "The date germinated cannot be before the date sown\n";
		}
	
		// Checks that the number established is a valid number
		String numberEstablishedInput = numberEstablished.getText();

		if (numberEstablishedInput != null && !numberEstablishedInput.isBlank() && !isNumeric(numberEstablishedInput)) {
			failureMessage += "Invalid number established\n";
		}

		// Checks that the number germinated is greater than zero
		if (numberEstablishedInput != null && !numberEstablishedInput.isBlank() && isNumeric(numberEstablishedInput) 
				&& Integer.valueOf(numberEstablishedInput) < 1) {
			failureMessage += "The number established must be greater than zero\n";
		}

		// Make sure the number established is not greater than the number sown.
		if (numberEstablishedInput != null && !numberEstablishedInput.isBlank() && isNumeric(numberEstablishedInput) 
				&& Integer.valueOf(numberEstablishedInput) > Integer.valueOf(numberGerminatedInput)) {
			failureMessage += "The number established must not be greater than the number germinated\n";
		}
		
		// Checks that the date established is not in the future
		LocalDate dateEstablishedInput = dateEstablished.getValue();
		if (dateEstablishedInput != null && dateEstablishedInput.isAfter(today)) {
			failureMessage += "The date established cannot be in the future\n";
		}
		
		// Checks that the established date is not before germinated date
		if (dateEstablishedInput != null && dateEstablishedInput.isBefore(dateGerminatedInput)) {
			failureMessage += "The date established cannot be before the date germinated\n";
		}
	
		if (failureMessage.isBlank()) {
			return true;
		} else {
			return false;
		}
	}


	/*
	 * Private helper method to check whether a string is a positive integer
	 */
	private boolean isNumeric(String string) {
		if (string == null || string.isBlank()) {
			return false;
		}
		return string.matches("^\\d*$");
	}


	/*
	 * Private helper method to populate any messages from the ConstraintViolationException onto 
	 * the failure message to be displayed to the user.
	 */
	private void populateConstraintViolationMessages(ConstraintViolationException e) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		Iterator<ConstraintViolation<?>> iterator = violations.iterator();
		while (iterator.hasNext()) {
			failureMessage += (iterator.next().getMessage() + "\n");
		}
	}


	/*
	 * Open the pop up window detailing the validation failure.
	 */
	private void openValidationFailurePopup(String failureMessage) {

		// Grab the FXML file		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popups/ValidationFailure.fxml"));
		fxmlLoader.setControllerFactory(applicationContext::getBean);

		Stage stage = new Stage();
		try {
			stage.setScene(new Scene((Pane) fxmlLoader.load()));

			ValidationFailureController validationFailureController = fxmlLoader.getController();
			validationFailureController.initData(failureMessage);

			stage.setTitle("Invalid Input!"); 
			stage.show();

		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.ERROR, "Failed to create new Window.", e);		
		}
	}


	/* 
	 * Private helper method to fire an event when this pop up closes.
	 */
	private void fireCloseEvent() {
		Stage stage = (Stage) saveNewButton.getScene().getWindow();
		saveNewButton.setOnAction(event -> stage.fireEvent(new WindowEvent(stage, WINDOW_CLOSE_REQUEST)));
		stage.hide();
	}


	/*
	 * Private helper method to deduct the number of seeds sown from the total remaining for that pack.
	 * 
	 * If in edit mode, add back the previously sown seeds before deducting those set this time round
	 * 
	 * Note that some number remaining values may not be accurate (e.g. if a pack has ~1000 seeds and 
	 * ~1/2 have been used, it's highly likely the exact number remaining is not 500. So default to 0 
	 * if numberRemainingMinusSown is a negative value.
	 */
	private void deductSownSeedsFromPack() {

		SeedPacket seedPacketSown = seedComboBox.getValue();

		int numberSownToSet = Integer.valueOf(numberSown.getText());
		int priorNumberRemaining = seedPacketSown.getNumberRemaining();
		int numberRemainingToSet = priorNumberRemaining; // Default to prior

		if (mode.equals(ADD_NEW)) {
			numberRemainingToSet -= numberSownToSet;
		}

		if (mode.equals(EDIT) && numberSownToSet != priorNumberSown) {
			numberRemainingToSet += priorNumberSown - numberSownToSet; 
		}

		seedPacketSown.setNumberRemaining(numberRemainingToSet > 0 ? numberRemainingToSet : 0);
		seedPacketService.save(seedPacketSown);
	}


	/*
	 * Private helper method to initialise the event handling.
	 */
	private void initialiseEventHandling() {

		// Handles when a user presses Cmd+S to save the CurrentlyGrowing record
		saveNewButton.getScene().getAccelerators().put(new KeyCodeCombination(
				KeyCode.S, KeyCombination.SHORTCUT_DOWN), new Runnable() {
			@Override public void run() {
				save();
			}
		});

		// Handles when a user presses escape to close the window
		saveNewButton.getScene().addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
			if (KeyCode.ESCAPE == event.getCode()) {
				cancel();
			}
		});	
	}


	/*
	 * Private helper method to refresh the TableView to add any new currently growing records, and update 
	 * any existing currently growing records that have changed.
	 */
	private void refreshTableView() {
		if (!currentlyGrowing.contains(currentlyGrowingRecord)) { // If adding a new record
			currentlyGrowing.add(currentlyGrowingRecord); 
			fireCloseEvent();;
		} else {
			table.refresh();
		}
	}
}
