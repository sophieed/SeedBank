package application.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import application.Mode;
import application.seeds.SeedPacket;


/**
 * Controller for the repository tab.
 */
@Controller
public class RepositoryTabController {

	@FXML private TableView<SeedPacket> seedPacketTable;
	@FXML private TableColumn<SeedPacket, String> nameColumn;
	@FXML private TableColumn<SeedPacket, String> typeColumn;
	@FXML private TableColumn<SeedPacket, String> sowIndoorsFromColumn;
	@FXML private TableColumn<SeedPacket, String> sowIndoorsUntilColumn;
	@FXML private TableColumn<SeedPacket, String> sowOutdoorsFromColumn;
	@FXML private TableColumn<SeedPacket, String> sowOutdoorsUntilColumn;
	@FXML private TableColumn<SeedPacket, String> harvestFromColumn;
	@FXML private TableColumn<SeedPacket, String> harvestUntilColumn;
	@FXML private TableColumn<SeedPacket, String> floweringFromColumn;
	@FXML private TableColumn<SeedPacket, String> floweringUntilColumn;
	@FXML private TableColumn<SeedPacket, String> sowOutdoorsColumn;
	@FXML private TableColumn<SeedPacket, String> sowIndoorsColumn;
	@FXML private TableColumn<SeedPacket, String> harvestColumn;
	@FXML private TableColumn<SeedPacket, String> floweringColumn;

	@FXML private Button deleteButton;
	@FXML private Button viewButton;
	@FXML private Button addButton;
	@FXML private Button editButton;
	@FXML private Label labelConfirmDelete;

	@Autowired private ApplicationContext applicationContext;
	
	private ObservableList<SeedPacket> seedPackets;
	
	/**
	 * Gets the table view of seed packets
	 * @return A {@link TableView} of {@link SeedPacket}s
	 */
	TableView<SeedPacket> getTable() {
		return seedPacketTable;
	}
	
	
	/**
	 * Initialise the controller.
	 * @param seedPackets The seed packets to be used
	 */
	void initialise(ObservableList<SeedPacket> seedPackets) {
		this.seedPackets = seedPackets;
		initializeRepositoryTable();
		initializeEventHandling();
	}


	/*
	 * Private helper method to initialise the seed bank repository table.
	 */
	private void initializeRepositoryTable() {

		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));	
		sowIndoorsFromColumn.setCellValueFactory(new PropertyValueFactory<>("sowingIndoorsStartMonth"));
		sowIndoorsUntilColumn.setCellValueFactory(new PropertyValueFactory<>("sowingIndoorsEndMonth"));
		sowOutdoorsFromColumn.setCellValueFactory(new PropertyValueFactory<>("sowingOutdoorsStartMonth"));
		sowOutdoorsUntilColumn.setCellValueFactory(new PropertyValueFactory<>("sowingOutdoorsEndMonth"));
		harvestFromColumn.setCellValueFactory(new PropertyValueFactory<>("harvestStartMonth"));
		harvestUntilColumn.setCellValueFactory(new PropertyValueFactory<>("harvestEndMonth"));
		floweringFromColumn.setCellValueFactory(new PropertyValueFactory<>("floweringStartMonth"));
		floweringUntilColumn.setCellValueFactory(new PropertyValueFactory<>("floweringEndMonth"));

		seedPacketTable.setItems(seedPackets);
	}


	/*
	 * Private helper method to initialise the event handling.
	 */
	private void initializeEventHandling() {
		initializeButtonEventHandling();
		initializeShortcutEventHandling();
	}


	/*
	 * Private helper method to initialise the event handling specifically where buttons are involved
	 */
	private void initializeButtonEventHandling() {

		List<Button> buttons = new ArrayList<>(
				Arrays.asList(deleteButton, viewButton, addButton, editButton));

		for (Button button : buttons) {
			button.defaultButtonProperty().bind(button.focusedProperty());
		}

		// Handle when the add button is pressed
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openEditSeedPacketPopup(Mode.ADD_NEW);
			}
		});


		// Handle when the edit button is pressed
		editButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openEditSeedPacketPopup(Mode.EDIT);
			}
		});


		// Handle when the view button is pressed
		viewButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openEditSeedPacketPopup(Mode.VIEW);
			}
		});


		// Handle when the delete button is pressed
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				openConfirmDeletionPopup();
			}
		});
	}


	/*
	 * Private helper method to initialise event handling when shortcuts are used
	 */
	private void initializeShortcutEventHandling() {

		// Handle when a user double clicks on an entry in the repository
		seedPacketTable.setOnMouseClicked((MouseEvent event) -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
				openEditSeedPacketPopup(Mode.VIEW);
			}
		});

		// Handles when a user types Cmd+# to launch the edit window in the appropriate mode
		setShortcut(KeyCode.N, Mode.ADD_NEW);
		setShortcut(KeyCode.E, Mode.EDIT);
		setShortcut(KeyCode.V, Mode.VIEW);

		// Handles when a user presses Cmd+D to bring up the Delete window
		seedPacketTable.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (new KeyCodeCombination(
						KeyCode.D, KeyCombination.SHORTCUT_DOWN).match(event)) {
					openConfirmDeletionPopup();
				} 
			}
		});
	}


	/*
	 * Private helper method to set shortcuts for launching the edit window in the appropriate mode
	 */
	private void setShortcut(KeyCode keyCode, Mode mode) {
		seedPacketTable.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (new KeyCodeCombination(keyCode, KeyCombination.SHORTCUT_DOWN).match(event)) {
					openEditSeedPacketPopup(mode);
				} 
			}
		});
	}


	/*
	 * Open the pop up window in the relevant mode.
	 */
	private void openEditSeedPacketPopup(Mode mode) {
		// Grab the FXML file		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popups/EditSeedPacket.fxml"));
		fxmlLoader.setControllerFactory(applicationContext::getBean);
		
		Stage stage = new Stage();
		try {
			stage.setScene(new Scene((Pane) fxmlLoader.load()));
	
			EditSeedPacketController editSeedPacketController = fxmlLoader.getController();

			editSeedPacketController.initData(seedPacketTable, mode);
			stage.setTitle(mode.toString()); 
			stage.show();

		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.ERROR, "Failed to create new Window.", e);		
		}
	}


	/*
	 * Open the pop up prompting the user to confirm deletion.
	 */
	private void openConfirmDeletionPopup() {
		if (seedPacketTable.getSelectionModel().getSelectedItem() != null) {

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popups/ConfirmDelete.fxml"));			
			fxmlLoader.setControllerFactory(applicationContext::getBean);
			
			Stage stage = new Stage();
			try {
				stage.setScene(new Scene((Pane) fxmlLoader.load()));

				ConfirmDeleteController confirmDeleteController = fxmlLoader.getController();
				confirmDeleteController.initData(seedPacketTable);

				stage.setTitle("Delete"); 
				stage.show();

			} catch (IOException e) {
				Logger logger = Logger.getLogger(getClass().getName());
				logger.log(Level.ERROR, "Failed to create new Window.", e);		
			}
		}
	}

}