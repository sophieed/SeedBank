package application.controllers;

import static java.time.LocalDate.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testfx.framework.junit.ApplicationTest;

import application.Mode;
import application.currentlygrowing.CurrentlyGrowing;
import application.currentlygrowing.CurrentlyGrowingService;
import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;

/**
 * Tests for the {@link SowNewController}.
 */
public class TestSowNewController extends ApplicationTest {

	@Spy private ComboBox<SeedPacket> seedComboBox;
	@Spy private RadioButton indoorsRadio;
	@Spy private RadioButton outdoorsRadio;
	@Spy private DatePicker dateSown;
	@Spy private TextField numberSown;
	@Spy private DatePicker dateGerminated;
	@Spy private TextField numberGerminated;
	@Spy private DatePicker dateEstablished;
	@Spy private TextField numberEstablished;
	@Spy private HBox germinatedData;
	@Spy private HBox establishedData;
	@Spy private Label numberRemaining;
	@Spy private TextField locationStored;
	@Spy private TextArea notes;
	@Spy private CurrentlyGrowing currentlyGrowingRecord;
	@Spy private CurrentlyGrowingService currentlyGrowingService;

	@Mock private Button saveNewButton;
	@Mock private Button cancelButton;
	@Mock private ObservableList<CurrentlyGrowing> currentlyGrowing;
	@Mock private TableView<CurrentlyGrowing> table;
	@Mock private TableViewSelectionModel<CurrentlyGrowing> selectionModel;
	@Mock private ObservableMap<KeyCombination, Runnable> accelerators;
	@Mock private Scene scene;
	@Mock private SeedPacketService seedPacketService;
	@Mock private SeedPacket seedPacket;

	private List<SeedPacket> seedPackets = new ArrayList<SeedPacket>();

	@InjectMocks
	private SowNewController sowNewController;

	private static final long SEED_PACKET_ID = 123L; 
	private static final boolean IS_INDOORS = true;        
	private static final String LOCATION_STORED = "location";  
	private static final LocalDate DATE_SOWN = now();   
	private static final Integer NUMBER_SOWN = 4;    
	private static final String NOTES = "notes";               


	/**
	 * Initialises the mock objects, and mocks any core behaviour.
	 */
	@Before 
	public void init() {
		initMocks(this);

		seedPackets.add(seedPacket);

		when(seedPacketService.loadAll()).thenReturn(seedPackets);
		when(seedPacketService.loadById(SEED_PACKET_ID)).thenReturn(Optional.of(seedPacket));
		when(seedPacket.getName()).thenReturn("name");
		when(seedPacket.getId()).thenReturn(SEED_PACKET_ID);
		when(table.getSelectionModel()).thenReturn(selectionModel);
		when(selectionModel.getSelectedItem()).thenReturn(currentlyGrowingRecord);
		when(saveNewButton.getScene()).thenReturn(scene);
		when(scene.getAccelerators()).thenReturn(accelerators);
	}


	/**
	 * Tests that the controller correctly handles Add New mode, with a blank {@link CurrentlyGrowing} 
	 * record and the fields set accordingly.
	 */
	@Test
	public void testAddNewMode() {

		// When
		sowNewController.initData(currentlyGrowing, table, Mode.ADD_NEW);

		// Then
		verify(selectionModel, never()).getSelectedItem(); // Currently growing record should NOT be loaded

		assertTrue("Seed Packet should not be disabled", !seedComboBox.isDisabled());
		assertTrue("Indoors radio box not be disabled", !indoorsRadio.isDisabled());
		assertTrue("Outdoors radio should not be disabled", !outdoorsRadio.isDisabled());
		assertTrue("Date sown should not be disabled", !dateSown.isDisabled());
		assertTrue("Number sown should not be disabled", !numberSown.isDisabled());
		assertTrue("Location stored should be editable", locationStored.isEditable());
		assertTrue("Location stored should not be disabled", !locationStored.isDisabled());
		assertTrue("Notes should be editable", notes.isEditable());
		assertTrue("Notes should not be disabled", !notes.isDisabled());

		verify(saveNewButton).setDisable(false);
	}


	/**
	 * Tests that the controller correctly handles View mode, with a loaded {@link CurrentlyGrowing} 
	 * record and the fields set accordingly.
	 */
	@Test
	public void testViewMode() {
		
		// When
		sowNewController.initData(currentlyGrowing, table, Mode.VIEW);

		// Then
		verify(selectionModel).getSelectedItem(); // The currently growing record should be loaded

		assertTrue("Seed Packet should be disabled", seedComboBox.isDisabled());
		assertTrue("Indoors radio box should be disabled", indoorsRadio.isDisabled());
		assertTrue("Outdoors radio should be disabled", outdoorsRadio.isDisabled());
		assertTrue("Date sown should be disabled", dateSown.isDisabled());
		assertTrue("Number sown should be disabled", numberSown.isDisabled());
		assertTrue("Date germinated should be disabled", dateGerminated.isDisabled());
		assertTrue("Number germinated should be disabled", numberGerminated.isDisabled());
		assertTrue("Date established should be disabled", dateEstablished.isDisabled());
		assertTrue("Number established should be disabled", numberEstablished.isDisabled());
		assertTrue("Location stored should not be editable", !locationStored.isEditable());
		assertTrue("Location stored should be disabled", locationStored.isDisabled());
		assertTrue("Notes should not be editable", !notes.isEditable());
		assertTrue("Notes should be disabled", notes.isDisabled());

		verify(saveNewButton).setDisable(true);
	}


	/**
	 * Tests that the controller correctly handles Edit mode, with a loaded {@link CurrentlyGrowing} 
	 * record and the fields set accordingly.
	 */
	@Test
	public void testEditMode() {

		// When
		sowNewController.initData(currentlyGrowing, table, Mode.EDIT);

		// Then
		verify(selectionModel).getSelectedItem(); // The currently growing record should be loaded

		assertTrue("Seed Packet should be disabled", seedComboBox.isDisabled());
		assertTrue("Indoors radio box should not be disabled", !indoorsRadio.isDisabled());
		assertTrue("Outdoors radio should not be disabled", !outdoorsRadio.isDisabled());
		assertTrue("Date sown should not be disabled", !dateSown.isDisabled());
		assertTrue("Number sown should not be disabled", !numberSown.isDisabled());
		assertTrue("Date germinated should not be disabled", !dateGerminated.isDisabled());
		assertTrue("Number germinated should not be disabled", !numberGerminated.isDisabled());
		assertTrue("Date established should not be disabled", !dateEstablished.isDisabled());
		assertTrue("Number established should not be disabled", !numberEstablished.isDisabled());
		assertTrue("Location stored should be editable", locationStored.isEditable());
		assertTrue("Location stored should not be disabled", !locationStored.isDisabled());
		assertTrue("Notes should be editable", notes.isEditable());
		assertTrue("Notes should not be disabled", !notes.isDisabled());

		verify(saveNewButton).setDisable(false);
	}


	/**
	 * Tests that the model is correctly mapped to the fields
	 */
	@Test
	public void testModelMappedCorrectlyToFields() {

		// Given
		setUpCurrentlyGrowingModel();

		// Check a few of the fields to assert that they are not yet set to the model
		assertNull("Seed Packet field should currently be empty", seedComboBox.getValue());
		assertEquals("Number sown should currently be empty", "", numberSown.getText());
		assertEquals("Notes should currently be empty", "", notes.getText());

		// When
		sowNewController.initData(currentlyGrowing, table, Mode.VIEW);

		// Then assert that the fields NOW reflect the model
		assertEquals("Seed Packet is incorrect", (Long) SEED_PACKET_ID, seedComboBox.getValue().getId());
		assertEquals("Setting is incorrect", IS_INDOORS, indoorsRadio.isSelected());
		assertEquals("Location is incorrect", LOCATION_STORED, locationStored.getText());
		assertEquals("Number sown is incorrect", NUMBER_SOWN.toString(), numberSown.getText());
		assertEquals("Date sown is incorrect", DATE_SOWN, dateSown.getValue());
		assertEquals("Number germinated is incorrect", String.valueOf(NUMBER_SOWN - 1) , numberGerminated.getText());
		assertEquals("Date germinated is incorrect", DATE_SOWN.plusDays(1), dateGerminated.getValue());	
		assertEquals("Number established is incorrect", String.valueOf(NUMBER_SOWN - 2), numberEstablished.getText());
		assertEquals("Date established is incorrect", DATE_SOWN.plusDays(2), dateEstablished.getValue());	
		assertEquals("Notes is incorrect", NOTES, notes.getText());
	}


	/**
	 * Tests that the input values from the fields are appropriately mapped to the model 
	 * for saving.
	 */
	@Test
	public void testFieldsCorrectlyMappedToModel() {

		// Given
		setUpInputFieldValues();

		// Check a few of the model fields to assert that they have not yet been set from the input values
		assertNull("Seed Packet should currently be empty", currentlyGrowingRecord.getSeedPacket());
		assertNull("Number sown should currently be empty", currentlyGrowingRecord.getNumberSown());

		// When
		sowNewController.mapFieldsToModel();

		// Then assert that the model values have NOW been updated with the values input into the fields
		assertTrue("Setting is incorrect", currentlyGrowingRecord.isIndoors());
		assertEquals("Location is incorrect", LOCATION_STORED, currentlyGrowingRecord.getLocation());
		assertEquals("Number sown is incorrect", NUMBER_SOWN, currentlyGrowingRecord.getNumberSown());
		assertEquals("Date sown is incorrect", DATE_SOWN, currentlyGrowingRecord.getDateSown());
		assertEquals("Number germinated is incorrect", (Integer) (NUMBER_SOWN-1), currentlyGrowingRecord.getNumberGerminated());
		assertEquals("Date germinated is incorrect", DATE_SOWN.plusDays(1), currentlyGrowingRecord.getDateGerminated());
		assertEquals("Number established is incorrect", (Integer) (NUMBER_SOWN-2), currentlyGrowingRecord.getNumberEstablished());
		assertEquals("Date established is incorrect", DATE_SOWN.plusDays(2), currentlyGrowingRecord.getDateEstablished());
		assertEquals("Notes is incorrect", NOTES, currentlyGrowingRecord.getNotes());
	}


	/*
	 * Private helper method to set up a CurrentlyGrowing record with values for mapping to the 
	 * fields for display.
	 */
	private void setUpCurrentlyGrowingModel() {
		currentlyGrowingRecord.setSeedPacket(SEED_PACKET_ID);
		currentlyGrowingRecord.setIndoors(IS_INDOORS);
		currentlyGrowingRecord.setLocation(LOCATION_STORED);
		currentlyGrowingRecord.setDateSown(DATE_SOWN);
		currentlyGrowingRecord.setNumberSown(NUMBER_SOWN);
		currentlyGrowingRecord.setDateGerminated(DATE_SOWN.plusDays(1));
		currentlyGrowingRecord.setNumberGerminated(NUMBER_SOWN-1);
		currentlyGrowingRecord.setDateEstablished(DATE_SOWN.plusDays(2));
		currentlyGrowingRecord.setNumberEstablished(NUMBER_SOWN-2);
		currentlyGrowingRecord.setNotes(NOTES);
	}


	/* 
	 * Private helper method to input some data to the fields, to be mapped to the 
	 * model for testing.
	 */
	private void setUpInputFieldValues() {
		seedComboBox.setValue(seedPacket);
		indoorsRadio.setSelected(true);
		locationStored.setText(LOCATION_STORED);
		dateSown.setValue(DATE_SOWN);
		numberSown.setText(NUMBER_SOWN.toString());	
		dateGerminated.setValue(DATE_SOWN.plusDays(1));
		numberGerminated.setText(String.valueOf(NUMBER_SOWN-1));
		dateEstablished.setValue(DATE_SOWN.plusDays(2));
		numberEstablished.setText(String.valueOf(NUMBER_SOWN-2));
		notes.setText(NOTES);
	}

}