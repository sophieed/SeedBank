package application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import application.manufacturer.Manufacturer;
import application.manufacturer.ManufacturerService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;


/**
 * Controller for the add new manufacturer pop up screen.
 */
@Controller
@Scope("prototype")
public class AddNewManufacturerController {

	@FXML private TextField nameField;
	@FXML private TextField urlField;
	@FXML private Button saveManufacturerButton;
	@FXML private Button cancelButton;

	private Manufacturer manufacturer = new Manufacturer();
	private ComboBox<String> manufacturerCombo;

	@Autowired private ManufacturerService manufacturerService;


	/**
	 * Initialise the data.
	 */
	void initData(ComboBox<String> manufacturerCombo) {
		this.manufacturerCombo = manufacturerCombo;
		saveManufacturerButton.defaultButtonProperty().bind(saveManufacturerButton.focusedProperty());
		cancelButton.defaultButtonProperty().bind(cancelButton.focusedProperty());
		initializeEventHandling();
	}


	/*
	 * Method to save the manufacturer and close the window, triggered when the user presses 
	 * the relevant button.
	 */
	@FXML
	private void save() {
		mapFieldsToModel();
		manufacturerService.save(manufacturer);
		saveManufacturerButton.getScene().getWindow().hide();
		
		int index = manufacturerCombo.getItems().size()-1;
		manufacturerCombo.getItems().add(index, nameField.getText());
		manufacturerCombo.getSelectionModel().select(index);
	}


	/*
	 * Method to cancel, and close the window, triggered when the user presses the relevant button.
	 */
	@FXML
	private void cancel() {
		saveManufacturerButton.getScene().getWindow().hide();
	}
	
	
	/*
	 * Private helper method to map the values displayed in the fields to the model.
	 */
	private void mapFieldsToModel() {
		manufacturer.setName(nameField.getText());
		manufacturer.setUrl(urlField.getText());
	}
	
	
	/*
	 * Private helper method to initialise the event handling.
	 */
	private void initializeEventHandling() {

		// Handles when a user presses Cmd+S to save the Seed Packet
		saveManufacturerButton.getScene().getAccelerators().put(new KeyCodeCombination(
				KeyCode.S, KeyCombination.SHORTCUT_DOWN), new Runnable() {
			@Override public void run() {
				save();
			}
		});
	}

}
