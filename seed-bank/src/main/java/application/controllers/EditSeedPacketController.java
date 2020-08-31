package application.controllers;

import static java.lang.Integer.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static java.util.regex.Pattern.compile;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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

import application.Mode;
import application.manufacturer.Manufacturer;
import application.manufacturer.ManufacturerService;
import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;
import application.type.Type;
import application.type.TypeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;


/**
 * Controller for the seed packet editing screen.
 */
@Controller
@Scope("prototype")
public class EditSeedPacketController {

	@FXML private TextField name;
	@FXML private TextField latinName;
	@FXML private ComboBox<String> typeCombo;
	@FXML private TextField variety;
	@FXML private ComboBox<String> manufacturerCombo;
	@FXML private TextField manufacturerCode;
	@FXML private TextField packSize;
	@FXML private TextField numberRemaining;
	@FXML private TextField expirationDate;
	@FXML private ComboBox<Month> sowingIndoorFromCombo;
	@FXML private ComboBox<Month> sowingIndoorUntilCombo;
	@FXML private ComboBox<Month> sowingOutdoorFromCombo;
	@FXML private ComboBox<Month> sowingOutdoorUntilCombo;
	@FXML private ComboBox<Month> harvestFromCombo;
	@FXML private ComboBox<Month> harvestUntilCombo;
	@FXML private ComboBox<Month> floweringFromCombo;
	@FXML private ComboBox<Month> floweringUntilCombo;
	@FXML private TextArea description;
	@FXML private TextArea keywords;

	@FXML private Button saveButton;
	@FXML private Button cancelButton;

	private SeedPacket seedPacket = new SeedPacket();
	private TableView<SeedPacket> seedPackets = new TableView<SeedPacket>();
	private String failureMessage = "";
	private Mode mode;

	@Autowired private SeedPacketService seedPacketService;
	@Autowired private ManufacturerService manufacturerService;
	@Autowired private TypeService typeService;
	@Autowired private ApplicationContext applicationContext;


	/**
	 * Initialise the data when entering from a table view
	 * @param seedPackets The {@link TableView} of {@link SeedPacket}s
	 * @param mode The mode the application is to be run in
	 */
	public void initData(TableView<SeedPacket> seedPackets, Mode mode) {
		this.seedPackets = seedPackets;
		setMode(mode, seedPackets);
		mapModelToFields();			
		initializeEventHandling();
	}


	/**
	 * Initialise the data when entering from a list view.
	 * @param seedPacketList The {@link ListView} of {@link SeedPacket}s.
	 */
	public void initData(ListView<SeedPacket> seedPacketList) {
		Long seedPacketId = seedPacketList.getSelectionModel().getSelectedItem().getId(); /* Ensure we get the
																							 most up to date 
																							 version. */
		seedPacket = seedPacketService.loadById(seedPacketId).get();
		setEditable(false, false, true);
		mapModelToFields();			
		initializeEventHandling();
	}


	/**
	 * Initialise the data when entering from a specified seed packet.
	 * @param seedPacket The {@link SeedPacket}.
	 */
	public void initData(SeedPacket seedPacket) {
		this.seedPacket = seedPacket;
		setEditable(false, false, true);
		mapModelToFields();			
		initializeEventHandling();
	}


	/**
	 * Method to map the values displayed in the fields to the model.
	 */
	void mapFieldsToModel() {
		seedPacket.setName(name.getText());
		seedPacket.setLatinName(latinName.getText());
		seedPacket.setType(typeCombo.getValue());
		seedPacket.setVariety(variety.getText());
		seedPacket.setManufacturer(manufacturerCombo.getValue());
		seedPacket.setManufacturerCode(manufacturerCode.getText());

		seedPacket.setPackSize(packSize.getText() != null ? Integer.valueOf(packSize.getText()) : 0);
		seedPacket.setNumberRemaining(numberRemaining.getText() != null ? Integer.valueOf(numberRemaining.getText()) : 0);
		seedPacket.setExpirationDate(expirationDate.getText() != null ? Integer.valueOf(expirationDate.getText()) : 0);

		seedPacket.setSowingIndoorsStartMonth(sowingIndoorFromCombo.getValue());
		seedPacket.setSowingIndoorsEndMonth(sowingIndoorUntilCombo.getValue());
		seedPacket.setSowingOutdoorsStartMonth(sowingOutdoorFromCombo.getValue());
		seedPacket.setSowingOutdoorsEndMonth(sowingOutdoorUntilCombo.getValue());
		seedPacket.setHarvestStartMonth(harvestFromCombo.getValue());
		seedPacket.setHarvestEndMonth(harvestUntilCombo.getValue());
		seedPacket.setFloweringStartMonth(floweringFromCombo.getValue());
		seedPacket.setFloweringEndMonth(floweringUntilCombo.getValue());

		seedPacket.setKeywords(keywords.getText());
		seedPacket.setDescription(description.getText());
	}


	/*
	 * Private helper method which saves the seed packet to the database and closes the window. 
	 * Triggered when the user presses the relevant button.
	 */
	@FXML
	private void save() {
		if (isValid()) {
			try {
				mapFieldsToModel();
				seedPacketService.save(seedPacket);
				refreshTableView();	
				saveButton.getScene().getWindow().hide();
			} catch (ConstraintViolationException e) {
				populateConstraintViolationMessages(e);
			}
		}

		if (failureMessage.length() > 0) {
			openValidationFailurePopup(failureMessage);
		}
	}


	/*
	 * Private helper method which does not save the seed packet to the database, but closes 
	 * the window. Triggered when the user presses the relevant button.
	 */
	@FXML
	private void cancel() {
		saveButton.getScene().getWindow().hide();
	}


	/*
	 * Private helper method to map the model values to the fields displayed.
	 */
	private void mapModelToFields() {
		name.setText(seedPacket.getName());
		latinName.setText(seedPacket.getLatinName());
		latinName.setFont(Font.font("Verdana", FontPosture.ITALIC, 12));
		variety.setText(seedPacket.getVariety());
		manufacturerCode.setText(seedPacket.getManufacturerCode());
		packSize.setText(seedPacket.getPackSize() != null ? seedPacket.getPackSize().toString() : null);
		numberRemaining.setText(seedPacket.getNumberRemaining() != null ? seedPacket.getNumberRemaining().toString() : null);
		expirationDate.setText(seedPacket.getExpirationDate() != null ? seedPacket.getExpirationDate().toString() : null);
		description.setText(seedPacket.getDescription());
		keywords.setText(seedPacket.getKeywords());

		setUpComboBoxes();
	}


	/*
	 * Private helper method to set up the combo boxes and map the model values to the fields displayed.
	 */
	private void setUpComboBoxes() {
		setUpManufacturerComboBox();
		setUpTypeComboBox();
		setUpMonthComboBoxes();
	}


	/*
	 * Private helper method to set up the behaviour of the manufacturer combo box.
	 */
	private void setUpManufacturerComboBox() {
		List<Manufacturer> manufacturers = manufacturerService.loadAll();
		List<String> manufacturerNames = new ArrayList<String>(asList("")); // Base case for adding new ones

		for (Manufacturer manufacturer : manufacturers) {
			manufacturerNames.add(manufacturer.getName());
		}

		sort(manufacturerNames); 
		manufacturerCombo.getItems().setAll(manufacturerNames);

		// Allow a new manufacturer to be added from within the combo box
		manufacturerCombo.setCellFactory(factory -> {
			ListCell<String> addNewCell = new ListCell<String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						if (item.isEmpty()) {
							setText("Add new...");
						} else {
							setText(item);
						}
					}
				}
			};

			// If the user presses the Add New cell...
			addNewCell.addEventFilter(MOUSE_PRESSED, event -> {
				if (addNewCell.getItem().isEmpty() && ! addNewCell.isEmpty()) {
					openAddNewManufacturerPopup(manufacturerCombo);
					event.consume();
				}
			});
			return addNewCell ;
		});

		manufacturerCombo.setValue(seedPacket.getManufacturer());
	}


	/*
	 * Private helper method to set up the behaviour of the type combo box.
	 */
	private void setUpTypeComboBox() {

		List<Type> types = typeService.loadAll();
		List<String> typeNames = new ArrayList<String>(asList("")); // Base case for adding new ones

		for (Type type : types) {
			typeNames.add(type.getName());
		}

		sort(typeNames);
		typeCombo.getItems().setAll(typeNames);

		// Allow a new type to be added from within the combo box
		typeCombo.setCellFactory(factory -> {
			ListCell<String> addNewCell = new ListCell<String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						if (item.isEmpty()) {
							setText("Add new...");
						} else {
							setText(item);
						}
					}
				}
			};

			// If the user presses the Add New cell...
			addNewCell.addEventFilter(MOUSE_PRESSED, event -> {
				if (addNewCell.getItem().isEmpty() && ! addNewCell.isEmpty()) {
					openAddNewTypePopup(typeCombo);
					event.consume();
				}
			});
			return addNewCell ;
		});

		typeCombo.setValue(seedPacket.getType());	
	}


	/*
	 * Private helper method to open the Add New Manufacturer pop-up
	 */
	private void openAddNewManufacturerPopup(ComboBox<String> manufacturerCombo){

		// Grab the FXML file		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popups/AddNewManufacturer.fxml"));
		fxmlLoader.setControllerFactory(applicationContext::getBean);

		Stage stage = new Stage();
		try {
			stage.setScene(new Scene((Pane) fxmlLoader.load()));

			AddNewManufacturerController addNewManufacturerController = fxmlLoader.getController();
			addNewManufacturerController.initData(manufacturerCombo);

			stage.setTitle("Add New Manufacturer"); 
			stage.show();

		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.ERROR, "Failed to create new Window.", e);		
		}
	}


	/*
	 * Private helper method to open the Add New Type pop-up
	 */
	private void openAddNewTypePopup(ComboBox<String> typeCombo){

		// Grab the FXML file		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popups/AddNewType.fxml"));
		fxmlLoader.setControllerFactory(applicationContext::getBean);

		Stage stage = new Stage();
		try {
			stage.setScene(new Scene((Pane) fxmlLoader.load()));

			AddNewTypeController addNewTypeController = fxmlLoader.getController();
			addNewTypeController.initData(typeCombo);

			stage.setTitle("Add New Type"); 
			stage.show();

		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.ERROR, "Failed to create new Window.", e);		
		}
	}


	/*
	 * Private helper method to set up the combo boxes containing months, and map any values from the model.
	 */
	private void setUpMonthComboBoxes() {

		List<ComboBox<Month>> comboBoxes = new ArrayList<>(Arrays.asList(
				sowingIndoorFromCombo, sowingIndoorUntilCombo, sowingOutdoorFromCombo, sowingOutdoorUntilCombo, 
				harvestFromCombo, harvestUntilCombo, floweringFromCombo, floweringUntilCombo));

		for (ComboBox<Month> comboBox : comboBoxes) {
			comboBox.getItems().setAll(Month.values());
		}

		sowingIndoorFromCombo.setValue(seedPacket.getSowingIndoorsStartMonth());
		sowingIndoorUntilCombo.setValue(seedPacket.getSowingIndoorsEndMonth());
		sowingOutdoorFromCombo.setValue(seedPacket.getSowingOutdoorsStartMonth());
		sowingOutdoorUntilCombo.setValue(seedPacket.getSowingOutdoorsEndMonth());	
		harvestFromCombo.setValue(seedPacket.getHarvestStartMonth());
		harvestUntilCombo.setValue(seedPacket.getHarvestEndMonth());
		floweringFromCombo.setValue(seedPacket.getFloweringStartMonth());
		floweringUntilCombo.setValue(seedPacket.getFloweringEndMonth());
	}


	/*
	 * Private helper method to apply settings dependent on the application mode.
	 */
	private void setMode(Mode mode, TableView<SeedPacket> seedPackets) {

		boolean editable = false; 
		boolean nameFieldEditable = false;
		boolean saveButtonDisabled = true;

		this.mode = mode;

		switch (mode) {

		case ADD_NEW : 
			editable = true;
			nameFieldEditable = true;
			saveButtonDisabled = false;
			break;

		case EDIT : 
			// Ensure we get the most up to date version, not just what's in the table on startup
			Long seedPacketId = seedPackets.getSelectionModel().getSelectedItem().getId();
			seedPacket = seedPacketService.loadById(seedPacketId).get();
			editable = true;
			nameFieldEditable = false;
			saveButtonDisabled = false;
			break;

		case VIEW : 
			seedPacketId = seedPackets.getSelectionModel().getSelectedItem().getId();
			seedPacket = seedPacketService.loadById(seedPacketId).get();
			editable = false;
			nameFieldEditable = false;
			saveButtonDisabled = true;
			break;

		case DELETE:
			break;
		}

		setEditable(editable, nameFieldEditable, saveButtonDisabled);
	}


	/*
	 * Private helper method to set which fields are editable.
	 */
	private void setEditable(boolean editable, boolean nameFieldEditable, boolean saveButtonDisabled) {
		name.setEditable(nameFieldEditable);
		name.setDisable(!nameFieldEditable);
		latinName.setEditable(editable);
		latinName.setDisable(!editable);
		typeCombo.setDisable(!editable);
		variety.setEditable(editable);
		variety.setDisable(!editable);
		manufacturerCombo.setDisable(!editable);
		manufacturerCode.setEditable(editable);
		manufacturerCode.setDisable(!editable);	
		packSize.setEditable(editable);
		packSize.setDisable(!editable);
		numberRemaining.setEditable(editable);
		numberRemaining.setDisable(!editable);
		expirationDate.setEditable(editable);
		expirationDate.setDisable(!editable);

		sowingIndoorFromCombo.setDisable(!editable);
		sowingIndoorUntilCombo.setDisable(!editable);
		sowingOutdoorFromCombo.setDisable(!editable);
		sowingOutdoorUntilCombo.setDisable(!editable);
		harvestFromCombo.setDisable(!editable);
		harvestUntilCombo.setDisable(!editable);
		floweringFromCombo.setDisable(!editable);
		floweringUntilCombo.setDisable(!editable);

		keywords.setEditable(editable);
		keywords.setDisable(!editable);
		description.setEditable(editable);
		description.setDisable(!editable);

		saveButton.setDisable(saveButtonDisabled);
	}


	/*
	 * Private helper method to initialise the event handling.
	 */
	private void initializeEventHandling() {

		// Handles when a user presses Cmd+S to save the Seed Packet
		saveButton.getScene().getAccelerators().put(new KeyCodeCombination(
				KeyCode.S, KeyCombination.SHORTCUT_DOWN), new Runnable() {
			@Override public void run() {
				save();
			}
		});


		// Handles when a user presses escape to close the window
		saveButton.getScene().addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
			if (KeyCode.ESCAPE == event.getCode()) {
				cancel();
			}
		});
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
	 * Private helper method to apply validation (not related to constraint violations on the SeedPacket 
	 * domain object) prior to saving it in the database. 
	 */
	private boolean isValid() {

		failureMessage = ""; // Ensure message is wiped from any previous validation attempts

		// Checks that the seed packet does not already exist in the database
		if (mode == Mode.ADD_NEW && seedPacketService.exists(name.getText())) {
			failureMessage += (name.getText() + " already exists in the database.\n");
		}

		// Checks that the pack size (if set) is a valid numeric value
		String packSizeInput = packSize.getText();
		if (packSizeInput != null && !isNumeric(packSizeInput)) {
			failureMessage += packSizeInput + " is not a valid pack size.\n";
		}

		// Checks that the number of seeds remaining (if set) is a valid numeric value
		String numberRemainingInput = numberRemaining.getText();
		if (numberRemainingInput != null && !isNumeric(numberRemainingInput)) {
			failureMessage += numberRemainingInput + " is not a valid number remaining.\n";
		}

		// Checks that the number of seeds remaining is not greater than the initial pack size
		if (isNumeric(packSizeInput) && isNumeric(numberRemainingInput)
				&& valueOf(numberRemainingInput) > valueOf(packSizeInput)) {
			failureMessage += "There are more seeds remaining than the pack should contain!\n";
		}

		// Checks that the expiration year is valid (YYYY, 2000 or greater)
		String expirationDateInput = expirationDate.getText();
		if (expirationDateInput != null && !isYear(expirationDateInput)) {
			failureMessage += expirationDateInput + " is not a valid year.\n";
		}

		/* Checks that if a 'from' combobox is selected, the corresponding 'until' box 
		 * is also selected, and vice versa */
		if(sowingIndoorFromCombo.getSelectionModel().isEmpty() != sowingIndoorUntilCombo.getSelectionModel().isEmpty()) {
			failureMessage += "Sowing indoor times are incomplete.\n";
		}

		if(sowingOutdoorFromCombo.getSelectionModel().isEmpty() != sowingOutdoorUntilCombo.getSelectionModel().isEmpty()) {
			failureMessage += "Sowing outdoor times are incomplete.\n";
		}

		if(harvestFromCombo.getSelectionModel().isEmpty() != harvestUntilCombo.getSelectionModel().isEmpty()) {
			failureMessage += "Harvesting times are incomplete.\n";
		}

		if(floweringFromCombo.getSelectionModel().isEmpty() != floweringUntilCombo.getSelectionModel().isEmpty()) {
			failureMessage += "Flowering times are incomplete.\n";
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
	 * Private helper method to check whether a string is a valid year (YYYY) or zero
	 */
	private boolean isYear(String string) {
		Pattern pattern = compile("(20[0-9]{2}$)|0");
		return pattern.matcher(string).matches();
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
	 * Private helper method to refresh the TableView to add any new seed packets, and update 
	 * any existing seed packets that have changed.
	 */
	private void refreshTableView() {
		/*  As we re-load on entering to make sure we have the most up-to-date copy from the DB, we 
		 *  need to be sure to delete the version already in the backing list and replace it with the 
		 *  new copy if running in edit mode. */
		if (mode == Mode.EDIT) { 
			seedPackets.getItems().removeIf(p -> p.getId() == seedPacket.getId());
		}
		seedPackets.getItems().add(seedPacket);
	}

}
