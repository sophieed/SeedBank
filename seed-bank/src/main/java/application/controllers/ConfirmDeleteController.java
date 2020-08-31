package application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;


/**
 * Controller for the confirmation of deletion pop up screen.
 */
@Controller
@Scope("prototype")
public class ConfirmDeleteController {

	@FXML private Label labelConfirmDelete;
	@FXML private Button confirmButton;
	@FXML private Button cancelButton;

	private TableView<SeedPacket> seedPackets;
	private SeedPacket seedPacket;

	@Autowired private SeedPacketService seedPacketService;


	/**
	 * Initialise the data.
	 * @param seedPackets The table of seed packets being passed in
	 */
	void initData(TableView<SeedPacket> seedPackets) {

		this.seedPackets = seedPackets;
		seedPacket = (SeedPacket) seedPackets.getSelectionModel().getSelectedItem();					

		setLabel();

		confirmButton.defaultButtonProperty().bind(confirmButton.focusedProperty());
		cancelButton.defaultButtonProperty().bind(cancelButton.focusedProperty());
	}


	/*
	 * Method to delete the relevant seed packet and close the window, triggered when the user presses 
	 * the relevant button.
	 */
	@FXML
	private void confirm() {
		seedPacketService.delete(seedPacket);	
		seedPackets.getItems().remove(seedPacket);								
		confirmButton.getScene().getWindow().hide();
	}


	/*
	 * Method to cancel the deletion and close the window, triggered when the user presses the 
	 * relevant button.
	 */
	@FXML
	private void cancel() {
		confirmButton.getScene().getWindow().hide();
	}


	/*
	 * Private helper method to modify the confirmation prompt message to include the name of the 
	 * seed packet to be deleted from the portfolio.
	 */
	private void setLabel() {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				labelConfirmDelete.setText("Are you sure you want to delete " 
						+ seedPacket.getName() + " from your portfolio?");
			}
		});
	}

}
