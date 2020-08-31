package application.controllers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


/**
 * Controller for the validation failure pop up screen.
 */
@Controller
@Scope("prototype")
public class ValidationFailureController {

	@FXML private Label causeOfFailure;
	@FXML private Button okButton;


	/**
	 * Initialise the data.
	 * @param seedPackets The table of seed packets being passed in
	 */
	void initData(String failureMessage) {
		causeOfFailure.setText(failureMessage);
		okButton.defaultButtonProperty().bind(okButton.focusedProperty());
	}


	/*
	 * Method to close the window when the user presses the OK button.
	 */
	@FXML
	private void ok() {							
		okButton.getScene().getWindow().hide();
	}
}
