package application.controllers;

import static java.util.regex.Pattern.compile;
import static javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import application.currentlygrowing.CurrentlyGrowing;
import application.currentlygrowing.CurrentlyGrowingService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * Controller for the progress pop up screen for manually progressing currently growing records.
 */
@Controller
@Scope("prototype")
public class ProgressController {

	@FXML private Label progressDateLabel;
	@FXML private Label progressNumberLabel;
	@FXML private DatePicker progressDatePicker;
	@FXML private TextField progressNumberField;
	@FXML private CheckBox plantOutCheckBox;

	@FXML private Button saveProgressButton;
	@FXML private Button cancelProgressButton;

	@Autowired private CurrentlyGrowingService currentlyGrowingService;
	@Autowired private ApplicationContext applicationContext;

	private TableView<CurrentlyGrowing> table;
	private CurrentlyGrowing currentlyGrowingRecord;
	private String failureMessage = "";
	private boolean isPhase1;


	/**
	 * Initialise the data.
	 */
	void initData(TableView<CurrentlyGrowing> table) {
		this.table = table;
		currentlyGrowingRecord = table.getSelectionModel().getSelectedItem();
		setPhase();
		setDisplay();
		initializeEventHandling();
	}


	/*
	 * Method to save the progression and close the window, triggered when the user presses 
	 * the relevant button.
	 */
	@FXML
	private void save() {
		if (isValid()) {
			try {
				mapFieldsToModel();
				currentlyGrowingService.save(currentlyGrowingRecord);
				table.refresh();;
				fireCloseEvent();
				saveProgressButton.getScene().getWindow().hide();
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
		saveProgressButton.getScene().getWindow().hide();
	}


	/*
	 * Private helper method to map the values displayed in the fields to the model.
	 */
	private void mapFieldsToModel() {

		if (isPhase1) {
			currentlyGrowingRecord.setDateGerminated(progressDatePicker.getValue());
			currentlyGrowingRecord.setNumberGerminated(Integer.valueOf(progressNumberField.getText()));
		} else {
			currentlyGrowingRecord.setDateEstablished(progressDatePicker.getValue());
			currentlyGrowingRecord.setNumberEstablished(Integer.valueOf(progressNumberField.getText()));
		}

		if (plantOutCheckBox.isSelected()) { // Plant out if appropriate
			currentlyGrowingRecord.setIndoors(false);		
		}
	}


	/*
	 * Private helper method to set the phase. I.e. if we are transitioning from sown to germinated 
	 * (phase 1) or germinated to established (phase 2).
	 */
	private void setPhase() {
		if (currentlyGrowingRecord.getDateGerminated() == null) {
			isPhase1 = true;
		} else {
			isPhase1 = false;
		}
	}


	/*
	 * Private helper method to set the appropriate labels and determine whether or not the plant out 
	 * option should be displayed.
	 */
	private void setDisplay() {

		// Decide whether or not to display the Plant Out option
		plantOutCheckBox.setVisible(
				currentlyGrowingRecord.isIndoors() && 
				currentlyGrowingRecord.getDateGerminated() != null);

		// Set the labels
		progressDateLabel.setText(isPhase1 ? "Date Germinated:" : "Date Established:");
		progressNumberLabel.setText(isPhase1 ? "Number Germinated:" : "Number Established:");

		// Set the defaults
		progressDatePicker.setValue(LocalDate.now());
		progressNumberField.setText(isPhase1 ? 
				currentlyGrowingRecord.getNumberSown().toString() : 
					currentlyGrowingRecord.getNumberGerminated().toString());
		plantOutCheckBox.setSelected(false);
	}


	/*
	 * Private helper method to initialise the event handling.
	 */
	private void initializeEventHandling() {

		// Handles when a user presses Cmd+S to save the progression
		saveProgressButton.getScene().getAccelerators().put(new KeyCodeCombination(
				KeyCode.S, KeyCombination.SHORTCUT_DOWN), new Runnable() {
			@Override public void run() {
				save();
			}
		});
		
		// Handles when a user presses escape to close the window
		saveProgressButton.getScene().addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
			if (KeyCode.ESCAPE == event.getCode()) {
				cancel();
			}
		});
	}


	/*
	 * Private helper method to apply validation (not related to constraint violations on the CurrentlyGrowing 
	 * domain object) prior to saving it in the database. 
	 */
	private boolean isValid() {

		failureMessage = ""; // Ensure message is wiped from any previous validation attempts

		// Checks that the progress number is a valid number
		String progressNumberInput = progressNumberField.getText();
		if (progressNumberInput != null && !isNumeric(progressNumberInput)) {
			failureMessage += "Invalid number\n";
		}

		// Checks that the progress number is greater than zero
		if (progressNumberInput != null && isNumeric(progressNumberInput) 
				&& Integer.valueOf(progressNumberInput) < 1) {
			failureMessage += "Number must be greater than zero\n";
		}

		// If phase 1, make sure the progress number is not greater than the sown number.
		if (isPhase1 && progressNumberInput != null && isNumeric(progressNumberInput) 
				&& Integer.valueOf(progressNumberInput) > currentlyGrowingRecord.getNumberSown()) {
			failureMessage += "The number germinated must not be greater than the number sown\n";
		}

		// If phase 2, make sure the progress number is not greater than the germinated number.
		if (!isPhase1 && progressNumberInput != null && isNumeric(progressNumberInput) 
				&& Integer.valueOf(progressNumberInput) > currentlyGrowingRecord.getNumberGerminated()) {
			failureMessage += "The number established must not be greater than the number germinated\n";
		}

		// Checks that the date used is not in the future
		LocalDate dateInput = progressDatePicker.getValue();
		if (dateInput != null && dateInput.isAfter(LocalDate.now())) {
			failureMessage += "The date cannot be in the future\n";
		}
		
		// If phase 1, make sure progress date is not before sown date
		if (isPhase1 && dateInput != null && dateInput.isBefore(currentlyGrowingRecord.getDateSown())) {
			failureMessage += "The date germinated cannot be before the date sown\n";
		}
		
		// If phase 2, make sure progress date is not before germinated date
		if (!isPhase1 && dateInput != null && dateInput.isBefore(currentlyGrowingRecord.getDateGerminated())) {
			failureMessage += "The date established cannot be before the date germinated\n";
		}
		

		if (failureMessage.isBlank()) {
			return true;
		} else {
			return false;
		}
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
	 * Private helper method to check whether a string is numeric
	 */
	private boolean isNumeric(String string) {
		if (string == null || string.isBlank()) {
			return false;
		}

		Pattern pattern = compile("-?\\d+(\\.\\d+)?");
		return pattern.matcher(string).matches();
	}


	/* 
	 * Private helper method to fire an event when this pop up closes.
	 */
	private void fireCloseEvent() {
		Stage stage = (Stage) saveProgressButton.getScene().getWindow();
		saveProgressButton.setOnAction(event -> stage.fireEvent(new WindowEvent(stage, WINDOW_CLOSE_REQUEST)));
		stage.hide();
	}

}
