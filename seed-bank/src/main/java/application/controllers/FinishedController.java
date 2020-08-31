package application.controllers;

import static javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import application.currentlygrowing.CurrentlyGrowing;
import application.currentlygrowing.CurrentlyGrowingService;
import application.previouslygrowing.PreviouslyGrowing;
import application.previouslygrowing.PreviouslyGrowingService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * Controller for the Finished currently growing record pop up screen.
 */
@Controller
@Scope("prototype")
public class FinishedController {

	@FXML private TextArea performanceNotes;

	@FXML private Button saveFinishedButton;
	@FXML private Button cancelButton;

	private CurrentlyGrowing currentlyGrowingRecord;
	private PreviouslyGrowing previouslyGrowingRecord = new PreviouslyGrowing();
	private TableView<CurrentlyGrowing> table;

	@Autowired private PreviouslyGrowingService previouslyGrowingService;
	@Autowired private CurrentlyGrowingService currentlyGrowingService;


	/**
	 * Initialise the data.
	 */
	void initData(TableView<CurrentlyGrowing> table) {
		this.table = table;
		currentlyGrowingRecord = table.getSelectionModel().getSelectedItem();

		saveFinishedButton.defaultButtonProperty().bind(saveFinishedButton.focusedProperty());
		cancelButton.defaultButtonProperty().bind(cancelButton.focusedProperty());
		initializeEventHandling();
	}


	/*
	 * Method to save the previously growing record and close the window, triggered when the user presses 
	 * the relevant button.
	 */
	@FXML
	private void save() {
		mapFieldsToModel();
		previouslyGrowingService.save(previouslyGrowingRecord);
		currentlyGrowingService.delete(currentlyGrowingRecord);
		table.refresh();
		fireCloseEvent();
		saveFinishedButton.getScene().getWindow().hide();
	}


	/*
	 * Method to cancel, and close the window, triggered when the user presses the relevant button.
	 */
	@FXML
	private void cancel() {
		saveFinishedButton.getScene().getWindow().hide();
	}


	/*
	 * Private helper method to map the values displayed in the fields to the model.
	 */
	private void mapFieldsToModel() {

		previouslyGrowingRecord.setDateClosed(LocalDate.now());
		previouslyGrowingRecord.setSuccessful(true);
		previouslyGrowingRecord.setStarRating(null); // TODO
		previouslyGrowingRecord.setPerformanceNotes(performanceNotes.getText());

		previouslyGrowingRecord.setId(currentlyGrowingRecord.getId());
		previouslyGrowingRecord.setSeedPacket(currentlyGrowingRecord.getSeedPacket());
		previouslyGrowingRecord.setIndoors(currentlyGrowingRecord.isIndoors());
		previouslyGrowingRecord.setLocation(currentlyGrowingRecord.getLocation());
		previouslyGrowingRecord.setNotes(currentlyGrowingRecord.getNotes());
		previouslyGrowingRecord.setDateSown(currentlyGrowingRecord.getDateSown());
		previouslyGrowingRecord.setDateGerminated(currentlyGrowingRecord.getDateGerminated());
		previouslyGrowingRecord.setDateEstablished(currentlyGrowingRecord.getDateEstablished());	
		previouslyGrowingRecord.setNumberSown(currentlyGrowingRecord.getNumberSown());
		previouslyGrowingRecord.setNumberGerminated(currentlyGrowingRecord.getNumberGerminated());
		previouslyGrowingRecord.setNumberEstablished(currentlyGrowingRecord.getNumberEstablished());	
	}


	/*
	 * Private helper method to initialise the event handling.
	 */
	private void initializeEventHandling() {

		// Handles when a user presses Cmd+S to save the Seed Packet
		saveFinishedButton.getScene().getAccelerators().put(new KeyCodeCombination(
				KeyCode.S, KeyCombination.SHORTCUT_DOWN), new Runnable() {
			@Override public void run() {
				save();
			}
		});
	}


	/* 
	 * Private helper method to fire an event when this pop up closes.
	 */
	private void fireCloseEvent() {
		Stage stage = (Stage) saveFinishedButton.getScene().getWindow();
		saveFinishedButton.setOnAction(event -> stage.fireEvent(new WindowEvent(stage, WINDOW_CLOSE_REQUEST)));
		stage.hide();
	}

}
