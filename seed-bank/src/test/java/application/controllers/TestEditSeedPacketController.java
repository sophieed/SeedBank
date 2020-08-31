package application.controllers;

import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Month;
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
import application.manufacturer.Manufacturer;
import application.manufacturer.ManufacturerService;
import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;
import application.type.Type;
import application.type.TypeService;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.input.KeyCombination;


/**
 * Tests for the {@link EditSeedPacketController}.
 */
public class TestEditSeedPacketController extends ApplicationTest {

	@Spy private TextField name;
	@Spy private TextField latinName;
	@Spy private ComboBox<String> typeCombo;
	@Spy private TextField variety;
	@Spy private ComboBox<String> manufacturerCombo;
	@Spy private TextField manufacturerCode;
	@Spy private TextField packSize;
	@Spy private TextField numberRemaining;
	@Spy private TextField expirationDate;
	@Spy private ComboBox<Month> sowingIndoorFromCombo;
	@Spy private ComboBox<Month> sowingIndoorUntilCombo;
	@Spy private ComboBox<Month> sowingOutdoorFromCombo;
	@Spy private ComboBox<Month> sowingOutdoorUntilCombo;
	@Spy private ComboBox<Month> harvestFromCombo;
	@Spy private ComboBox<Month> harvestUntilCombo;
	@Spy private ComboBox<Month> floweringFromCombo;
	@Spy private ComboBox<Month> floweringUntilCombo;
	@Spy private SeedPacket seedPacket;
	@Spy private TextArea description;
	@Spy private TextArea keywords;

	@Mock private Button saveButton;
	@Mock private Button cancelButton;
	@Mock private TableView<SeedPacket> seedPacketTable;
	@Mock private ObservableList<SeedPacket> listOfSeedPackets;
	@Mock private TableViewSelectionModel<SeedPacket> selectionModel;
	@Mock private ObservableMap<KeyCombination, Runnable> accelerators;
	@Mock private Scene scene;
	@Mock private ManufacturerService manufacturerService;
	@Mock private SeedPacketService seedPacketService;
	@Mock private TypeService typeService;

	private static final String NAME_FOR_MAPPING = "Name";
	private static final String LATIN_NAME_FOR_MAPPING = "LATIN NAME";
	private static final String TYPE_FOR_MAPPING = "Type";
	private static final String VARIETY_FOR_MAPPING = "Variety";
	private static final String MANUFACTURER_FOR_MAPPING = "Manufacturer";
	private static final String MANUFACTURER_CODE_FOR_MAPPING = "Code";
	private static final String KEYWORDS_FOR_MAPPING = "Keywords";
	private static final String DESCRIPTION_FOR_MAPPING = "Description";
	private static final Integer PACK_SIZE_FOR_MAPPING = 10;
	private static final Integer NUMBER_REMAINING_FOR_MAPPING = 6;
	private static final Integer EXPIRATION_DATE_FOR_MAPPING = 2024;
	private static final Long SEED_PACKET_ID = 123L;
	private static final Month SOWING_INDOORS_FROM_FOR_MAPPING = JANUARY;
	private static final Month SOWING_INDOORS_UNTIL_FOR_MAPPING = FEBRUARY;
	private static final Month SOWING_OUTDOORS_FROM_FOR_MAPPING = MARCH;
	private static final Month SOWING_OUTDOORS_UNTIL_FOR_MAPPING = APRIL;
	private static final Month HARVEST_FROM_FOR_MAPPING = MAY;
	private static final Month HARVEST_UNTIL_FOR_MAPPING = JUNE;
	private static final Month FLOWERING_FROM_FOR_MAPPING = JULY;
	private static final Month FLOWERING_UNTIL_FOR_MAPPING = AUGUST;
	
	private List<Manufacturer> manufacturers = new ArrayList<Manufacturer>();
	private List<Type> types = new ArrayList<Type>();

	
	@InjectMocks
	private EditSeedPacketController editSeedPacketController;


	/**
	 * Initialises the mock objects, and mocks any core behaviour.
	 */
	@Before 
	public void init() {
		initMocks(this);

		when(manufacturerService.loadAll()).thenReturn(manufacturers);
		when(typeService.loadAll()).thenReturn(types);
		when(seedPacketTable.getSelectionModel()).thenReturn(selectionModel);
		when(selectionModel.getSelectedItem()).thenReturn(seedPacket);
		when(seedPacket.getId()).thenReturn(SEED_PACKET_ID);
		when(seedPacketService.loadById(SEED_PACKET_ID)).thenReturn(Optional.of(seedPacket));
		when(saveButton.getScene()).thenReturn(scene);
		when(scene.getAccelerators()).thenReturn(accelerators);
	}
	
	
	/**
	 * Tests that the controller correctly handles Add New mode, with a blank {@link SeedPacket} 
	 * and the fields set accordingly.
	 */
	@Test
	public void testAddNewMode() {

		// When
		editSeedPacketController.initData(seedPacketTable, Mode.ADD_NEW);

		// Then
		verify(selectionModel, never()).getSelectedItem(); // The seed packet should NOT be loaded

		assertTrue("Name should be editable", name.isEditable());
		assertTrue("Name should not be disabled", !name.isDisabled());
		assertTrue("Latin Name should be editable", latinName.isEditable());
		assertTrue("Latin Name should not be disabled", !latinName.isDisabled());
		assertTrue("Type should not be disabled", !typeCombo.isDisabled());
		assertTrue("Variety should be editable", variety.isEditable());
		assertTrue("Variety should not be disabled", !variety.isDisabled());
		assertTrue("Manufacturer should not be disabled", !manufacturerCombo.isDisabled());
		assertTrue("Manufacturer Code should be editable", manufacturerCode.isEditable());
		assertTrue("Manufacturer Code should not be disabled", !manufacturerCode.isDisabled());
		assertTrue("Pack Size should be editable", packSize.isEditable());
		assertTrue("Pack Size should not be disabled", !packSize.isDisabled());
		assertTrue("Number Remaining should be editable", numberRemaining.isEditable());
		assertTrue("Number Remaining should not be disabled", !numberRemaining.isDisabled());
		assertTrue("Expiration Date should be editable", expirationDate.isEditable());
		assertTrue("Expiration Date should not be disabled", !expirationDate.isDisabled());

		assertTrue("Sowing Indoor From should not be disabled", !sowingIndoorFromCombo.isDisabled());
		assertTrue("Sowing Indoor Until should not be disabled", !sowingIndoorUntilCombo.isDisabled());
		assertTrue("Sowing Outdoor From should not be disabled", !sowingOutdoorFromCombo.isDisabled());
		assertTrue("Sowing Outdoor Until should not be disabled", !sowingOutdoorUntilCombo.isDisabled());
		assertTrue("Harvest From should not be disabled", !harvestFromCombo.isDisabled());
		assertTrue("Harvest Until should not be disabled", !harvestUntilCombo.isDisabled());
		assertTrue("Flowering From should not be disabled", !floweringFromCombo.isDisabled());
		assertTrue("Flowering Until should not be disabled", !floweringUntilCombo.isDisabled());

		assertTrue("Keywords should be editable", keywords.isEditable());
		assertTrue("Keywords should not be disabled", !keywords.isDisabled());
		assertTrue("Description should be editable", description.isEditable());
		assertTrue("Description should not be disabled", !description.isDisabled());

		verify(saveButton).setDisable(false);
	}


	/**
	 * Tests that the controller correctly handles View mode, with a loaded {@link SeedPacket} 
	 * and the fields set accordingly.
	 */
	@Test
	public void testViewMode() {

		// When
		editSeedPacketController.initData(seedPacketTable, Mode.VIEW);

		// Then	
		verify(selectionModel).getSelectedItem(); // The seed packet should be loaded

		assertTrue("Name should not be editable", !name.isEditable());
		assertTrue("Name should be disabled", name.isDisabled());
		assertTrue("Latin Name should not be editable", !latinName.isEditable());
		assertTrue("Latin Name should be disabled", latinName.isDisabled());
		assertTrue("Type should be disabled", typeCombo.isDisabled());
		assertTrue("Variety should not be editable", !variety.isEditable());
		assertTrue("Variety should be disabled", variety.isDisabled());
		assertTrue("Manufacturer should be disabled", manufacturerCombo.isDisabled());
		assertTrue("Manufacturer Code should not be editable", !manufacturerCode.isEditable());
		assertTrue("Manufacturer Code should be disabled", manufacturerCode.isDisabled());
		assertTrue("Pack Size should not be editable", !packSize.isEditable());
		assertTrue("Pack Size should be disabled", packSize.isDisabled());
		assertTrue("Number Remaining should not be editable", !numberRemaining.isEditable());
		assertTrue("Number Remaining should be disabled", numberRemaining.isDisabled());
		assertTrue("Expiration Date should not be editable", !expirationDate.isEditable());
		assertTrue("Expiration Date should be disabled", expirationDate.isDisabled());

		assertTrue("Sowing Indoor From should be disabled", sowingIndoorFromCombo.isDisabled());
		assertTrue("Sowing Indoor Until should be disabled", sowingIndoorUntilCombo.isDisabled());
		assertTrue("Sowing Outdoor From should be disabled", sowingOutdoorFromCombo.isDisabled());
		assertTrue("Sowing Outdoor Until should be disabled", sowingOutdoorUntilCombo.isDisabled());
		assertTrue("Harvest From should be disabled", harvestFromCombo.isDisabled());
		assertTrue("Harvest Until should be disabled", harvestUntilCombo.isDisabled());
		assertTrue("Flowering From should be disabled", floweringFromCombo.isDisabled());
		assertTrue("Flowering Until should be disabled", floweringUntilCombo.isDisabled());

		assertTrue("Keywords should not be editable", !keywords.isEditable());
		assertTrue("Keywords should be disabled", keywords.isDisabled());
		assertTrue("Description should not be editable", !description.isEditable());
		assertTrue("Description should be disabled", description.isDisabled());

		verify(saveButton).setDisable(true);
	}


	/**
	 * Tests that the controller correctly handles Edit mode, with a loaded {@link SeedPacket} 
	 * and the fields set accordingly.
	 */
	@Test
	public void testEditMode() {
		// When
		editSeedPacketController.initData(seedPacketTable, Mode.EDIT);

		// Then
		verify(selectionModel).getSelectedItem(); // The seed packet should be loaded

		assertTrue("Name should not be editable", !name.isEditable());
		assertTrue("Name should be disabled", name.isDisabled());
		assertTrue("Latin Name should be editable", latinName.isEditable());
		assertTrue("Latin Name should not be disabled", !latinName.isDisabled());
		assertTrue("Type should not be disabled", !typeCombo.isDisabled());
		assertTrue("Variety should be editable", variety.isEditable());
		assertTrue("Variety should not be disabled", !variety.isDisabled());
		assertTrue("Manufacturer should not be disabled", !manufacturerCombo.isDisabled());
		assertTrue("Manufacturer Code should be editable", manufacturerCode.isEditable());
		assertTrue("Manufacturer Code should not be disabled", !manufacturerCode.isDisabled());
		assertTrue("Pack Size should be editable", packSize.isEditable());
		assertTrue("Pack Size should not be disabled", !packSize.isDisabled());
		assertTrue("Number Remaining should be editable", numberRemaining.isEditable());
		assertTrue("Number Remaining should not be disabled", !numberRemaining.isDisabled());
		assertTrue("Expiration Date should be editable", expirationDate.isEditable());
		assertTrue("Expiration Date should not be disabled", !expirationDate.isDisabled());

		assertTrue("Sowing Indoor From should not be disabled", !sowingIndoorFromCombo.isDisabled());
		assertTrue("Sowing Indoor Until should not be disabled", !sowingIndoorUntilCombo.isDisabled());
		assertTrue("Sowing Outdoor From should not be disabled", !sowingOutdoorFromCombo.isDisabled());
		assertTrue("Sowing Outdoor Until should not be disabled", !sowingOutdoorUntilCombo.isDisabled());
		assertTrue("Harvest From should not be disabled", !harvestFromCombo.isDisabled());
		assertTrue("Harvest Until should not be disabled", !harvestUntilCombo.isDisabled());
		assertTrue("Flowering From should not be disabled", !floweringFromCombo.isDisabled());
		assertTrue("Flowering Until should not be disabled", !floweringUntilCombo.isDisabled());

		assertTrue("Keywords should be editable", keywords.isEditable());
		assertTrue("Keywords should not be disabled", !keywords.isDisabled());
		assertTrue("Description should be editable", description.isEditable());
		assertTrue("Description should not be disabled", !description.isDisabled());

		verify(saveButton).setDisable(false);
	}


	/**
	 * Tests that the model is correctly mapped to the fields
	 */
	@Test
	public void testModelMappedCorrectlyToFields() {

		// Given
		setUpSeedPacketModel();

		// Check a few of the fields to assert that they are not yet set to the model
		assertEquals("Name field should currently be empty", "", name.getText());
		assertEquals("Pack size should currently be empty", "", packSize.getText());
		assertNull("Harvest from should currently be empty", harvestFromCombo.getValue());
		assertEquals("Keywords should currently be empty", "", keywords.getText());

		// When
		editSeedPacketController.initData(seedPacketTable, Mode.VIEW);

		// Then assert that the fields NOW reflect the model
		assertEquals("Name is incorrect", NAME_FOR_MAPPING, name.getText());
		assertEquals("Latin Name is incorrect", LATIN_NAME_FOR_MAPPING, latinName.getText());
		assertEquals("Type is incorrect", TYPE_FOR_MAPPING, typeCombo.getValue());
		assertEquals("Variety is incorrect", VARIETY_FOR_MAPPING, variety.getText());
		assertEquals("Manufacturer is incorrect", MANUFACTURER_FOR_MAPPING, manufacturerCombo.getValue());
		assertEquals("Manufacturer Code is incorrect", MANUFACTURER_CODE_FOR_MAPPING, manufacturerCode.getText());
		assertEquals("Pack Size is incorrect", PACK_SIZE_FOR_MAPPING.toString(), packSize.getText());
		assertEquals("Number Remaining is incorrect", NUMBER_REMAINING_FOR_MAPPING.toString(), numberRemaining.getText());
		assertEquals("Expiry Date is incorrect", EXPIRATION_DATE_FOR_MAPPING.toString(), expirationDate.getText());

		assertEquals("Sow Indoors From Date is incorrect", SOWING_INDOORS_FROM_FOR_MAPPING, sowingIndoorFromCombo.getValue());
		assertEquals("Sow Indoors Until Date is incorrect", SOWING_INDOORS_UNTIL_FOR_MAPPING, sowingIndoorUntilCombo.getValue());
		assertEquals("Sow Outdoors From Date is incorrect", SOWING_OUTDOORS_FROM_FOR_MAPPING, sowingOutdoorFromCombo.getValue());
		assertEquals("Sow Outdoors Until Date is incorrect", SOWING_OUTDOORS_UNTIL_FOR_MAPPING, sowingOutdoorUntilCombo.getValue());
		assertEquals("Harvest From Date is incorrect", HARVEST_FROM_FOR_MAPPING, harvestFromCombo.getValue());
		assertEquals("Harvest From Date is incorrect", HARVEST_UNTIL_FOR_MAPPING, harvestUntilCombo.getValue());
		assertEquals("Flowering From Date is incorrect", FLOWERING_FROM_FOR_MAPPING, floweringFromCombo.getValue());
		assertEquals("Flowering From Date is incorrect", FLOWERING_UNTIL_FOR_MAPPING, floweringUntilCombo.getValue());

		assertEquals("Keywords is incorrect", KEYWORDS_FOR_MAPPING, keywords.getText());
		assertEquals("Description is incorrect", DESCRIPTION_FOR_MAPPING, description.getText());
	}


	/**
	 * Tests that the input values from the fields are appropriately mapped to the model 
	 * for saving.
	 */
	@Test
	public void testFieldsCorrectlyMappedToModel() {

		// Given
		when(seedPacketTable.getItems()).thenReturn(listOfSeedPackets);
		setValidNumericalFieldValues();

		setUpInputFieldValues();

		// Check a few of the model fields to assert that they have not yet been set from the input values
		assertNull("Name should currently be empty", seedPacket.getName());
		assertNull("Pack size should currently be empty", seedPacket.getPackSize());
		assertNull("Harvest from should currently be empty", seedPacket.getHarvestStartMonth());
		assertNull("Keywords should currently be empty", seedPacket.getKeywords());

		// When
		editSeedPacketController.mapFieldsToModel();

		// Then assert that the model values have NOW been updated with the values input into the fields
		assertEquals("Name is incorrect", NAME_FOR_MAPPING, seedPacket.getName());
		assertEquals("Latin Name is incorrect", LATIN_NAME_FOR_MAPPING, seedPacket.getLatinName());
		assertEquals("Type is incorrect", TYPE_FOR_MAPPING, seedPacket.getType());
		assertEquals("Variety is incorrect", VARIETY_FOR_MAPPING, seedPacket.getVariety());
		assertEquals("Manufacturer is incorrect", MANUFACTURER_FOR_MAPPING, seedPacket.getManufacturer());
		assertEquals("Manufacturer Code is incorrect", MANUFACTURER_CODE_FOR_MAPPING, seedPacket.getManufacturerCode());
		assertEquals("Pack Size is incorrect", PACK_SIZE_FOR_MAPPING, seedPacket.getPackSize());
		assertEquals("Number Remaining is incorrect", NUMBER_REMAINING_FOR_MAPPING, seedPacket.getNumberRemaining());
		assertEquals("Expiry Date is incorrect", EXPIRATION_DATE_FOR_MAPPING, seedPacket.getExpirationDate());

		assertEquals("Sow Indoors From Date is incorrect", SOWING_INDOORS_FROM_FOR_MAPPING, seedPacket.getSowingIndoorsStartMonth());
		assertEquals("Sow Indoors Until Date is incorrect", SOWING_INDOORS_UNTIL_FOR_MAPPING, seedPacket.getSowingIndoorsEndMonth());
		assertEquals("Sow Outdoors From Date is incorrect", SOWING_OUTDOORS_FROM_FOR_MAPPING, seedPacket.getSowingOutdoorsStartMonth());
		assertEquals("Sow Outdoors Until Date is incorrect", SOWING_OUTDOORS_UNTIL_FOR_MAPPING, seedPacket.getSowingOutdoorsEndMonth());
		assertEquals("Harvest From Date is incorrect", HARVEST_FROM_FOR_MAPPING, seedPacket.getHarvestStartMonth());
		assertEquals("Harvest From Date is incorrect", HARVEST_UNTIL_FOR_MAPPING, seedPacket.getHarvestEndMonth());
		assertEquals("Flowering From Date is incorrect", FLOWERING_FROM_FOR_MAPPING, seedPacket.getFloweringStartMonth());
		assertEquals("Flowering From Date is incorrect", FLOWERING_UNTIL_FOR_MAPPING, seedPacket.getFloweringEndMonth());

		assertEquals("Keywords is incorrect", KEYWORDS_FOR_MAPPING, seedPacket.getKeywords());
		assertEquals("Description is incorrect", DESCRIPTION_FOR_MAPPING, seedPacket.getDescription());
	}

	
	/*
	 * Private helper method to set the numerical fields to valid values 
	 * for testing.
	 */
	private void setValidNumericalFieldValues() {
		packSize.setText("0");
		numberRemaining.setText("0");
		expirationDate.setText("0");
	}


	/*
	 * Private helper method to input some data to the fields, to be mapped to the 
	 * model for testing.
	 */
	private void setUpInputFieldValues() {
		name.setText(NAME_FOR_MAPPING);
		latinName.setText(LATIN_NAME_FOR_MAPPING);
		typeCombo.setValue(TYPE_FOR_MAPPING);
		variety.setText(VARIETY_FOR_MAPPING);
		manufacturerCombo.setValue(MANUFACTURER_FOR_MAPPING);
		manufacturerCode.setText(MANUFACTURER_CODE_FOR_MAPPING);
		packSize.setText(PACK_SIZE_FOR_MAPPING.toString());
		numberRemaining.setText(NUMBER_REMAINING_FOR_MAPPING.toString());
		expirationDate.setText(EXPIRATION_DATE_FOR_MAPPING.toString());
		sowingIndoorFromCombo.setValue(SOWING_INDOORS_FROM_FOR_MAPPING);
		sowingIndoorUntilCombo.setValue(SOWING_INDOORS_UNTIL_FOR_MAPPING);
		sowingOutdoorFromCombo.setValue(SOWING_OUTDOORS_FROM_FOR_MAPPING);
		sowingOutdoorUntilCombo.setValue(SOWING_OUTDOORS_UNTIL_FOR_MAPPING);
		harvestFromCombo.setValue(HARVEST_FROM_FOR_MAPPING);
		harvestUntilCombo.setValue(HARVEST_UNTIL_FOR_MAPPING);
		floweringFromCombo.setValue(FLOWERING_FROM_FOR_MAPPING);
		floweringUntilCombo.setValue(FLOWERING_UNTIL_FOR_MAPPING);
		keywords.setText(KEYWORDS_FOR_MAPPING);
		description.setText(DESCRIPTION_FOR_MAPPING);
	}


	/*
	 * Private helper method to set up a SeedPacket with values for mapping to the 
	 * fields for display.
	 */
	private void setUpSeedPacketModel() {
		seedPacket.setName(NAME_FOR_MAPPING);
		seedPacket.setLatinName(LATIN_NAME_FOR_MAPPING);
		seedPacket.setType(TYPE_FOR_MAPPING);
		seedPacket.setVariety(VARIETY_FOR_MAPPING);
		seedPacket.setManufacturer(MANUFACTURER_FOR_MAPPING);
		seedPacket.setManufacturerCode(MANUFACTURER_CODE_FOR_MAPPING);
		seedPacket.setPackSize(PACK_SIZE_FOR_MAPPING);
		seedPacket.setNumberRemaining(NUMBER_REMAINING_FOR_MAPPING);
		seedPacket.setExpirationDate(EXPIRATION_DATE_FOR_MAPPING);
		seedPacket.setSowingIndoorsStartMonth(SOWING_INDOORS_FROM_FOR_MAPPING);
		seedPacket.setSowingIndoorsEndMonth(SOWING_INDOORS_UNTIL_FOR_MAPPING);
		seedPacket.setSowingOutdoorsStartMonth(SOWING_OUTDOORS_FROM_FOR_MAPPING);
		seedPacket.setSowingOutdoorsEndMonth(SOWING_OUTDOORS_UNTIL_FOR_MAPPING);
		seedPacket.setHarvestStartMonth(HARVEST_FROM_FOR_MAPPING);
		seedPacket.setHarvestEndMonth(HARVEST_UNTIL_FOR_MAPPING);
		seedPacket.setFloweringStartMonth(FLOWERING_FROM_FOR_MAPPING);
		seedPacket.setFloweringEndMonth(FLOWERING_UNTIL_FOR_MAPPING);
		seedPacket.setKeywords(KEYWORDS_FOR_MAPPING);
		seedPacket.setDescription(DESCRIPTION_FOR_MAPPING);
	}

}
