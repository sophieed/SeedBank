package application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import application.type.Type;
import application.type.TypeService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;


/**
 * Controller for the add new type pop up screen.
 */
@Controller
@Scope("prototype")
public class AddNewTypeController {

	@FXML private TextField nameField;
	@FXML private TextField familyField;
	@FXML private CheckBox edibleCheckbox;
	@FXML private CheckBox ornamentalCheckbox;
	@FXML private Button saveTypeButton;
	@FXML private Button cancelButton;

	private Type type = new Type();
	private ComboBox<String> typeCombo;

	@Autowired private TypeService typeService;


	/**
	 * Initialise the data.
	 */
	void initData(ComboBox<String> typeCombo) {
		this.typeCombo = typeCombo;
		familyField.setFont(Font.font("Verdana", FontPosture.ITALIC, 12));
		saveTypeButton.defaultButtonProperty().bind(saveTypeButton.focusedProperty());
		cancelButton.defaultButtonProperty().bind(cancelButton.focusedProperty());
		initializeEventHandling();
	}


	/*
	 * Method to save the type and close the window, triggered when the user presses 
	 * the relevant button.
	 */
	@FXML
	private void save() {
		mapFieldsToModel();
		typeService.save(type);
		saveTypeButton.getScene().getWindow().hide();
		
		int index = typeCombo.getItems().size()-1;
		typeCombo.getItems().add(index, nameField.getText());
		typeCombo.getSelectionModel().select(index);
	}


	/*
	 * Method to cancel, and close the window, triggered when the user presses the relevant button.
	 */
	@FXML
	private void cancel() {
		saveTypeButton.getScene().getWindow().hide();
	}
	
	
	/*
	 * Private helper method to map the values displayed in the fields to the model.
	 */
	private void mapFieldsToModel() {
		type.setName(nameField.getText());
		type.setFamily(familyField.getText());
		type.setEdible(edibleCheckbox.isSelected());
		type.setOrnamental(ornamentalCheckbox.isSelected());
	}
	
	
	/*
	 * Private helper method to initialise the event handling.
	 */
	private void initializeEventHandling() {

		// Handles when a user presses Cmd+S to save the Seed Packet
		saveTypeButton.getScene().getAccelerators().put(new KeyCodeCombination(
				KeyCode.S, KeyCombination.SHORTCUT_DOWN), new Runnable() {
			@Override public void run() {
				save();
			}
		});
	}

}
