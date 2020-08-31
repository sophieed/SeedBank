package application.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import java.time.Month;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.api.FxRobot;

import application.TestApplicationBase;
import application.manufacturer.Manufacturer;
import application.manufacturer.ManufacturerService;
import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;
import application.type.Type;
import application.type.TypeService;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


/**
 * Integration style tests to ensure that the seed packet repository functionality works correctly.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSeedPacketRepositoryManagement extends TestApplicationBase {

	private FxRobot robot = new FxRobot();

	@Autowired private SeedPacketService seedPacketService;
	@Autowired private ManufacturerService manufacturerService;
	@Autowired private TypeService typeService;


	/**
	 * Ensure that the appropriate data is present in the database before the tests run
	 */
	@Before
	public void setUpData() {
		// Make sure there are some types in the database
		Type type1 = new Type();
		type1.setName("Type 1");
		type1.setFamily("Family 1");
		type1.setEdible(true);

		Type type2 = new Type();
		type2.setName("Type 2");
		type2.setFamily("Family 2");
		type2.setEdible(true);

		typeService.save(type1);
		typeService.save(type2);

		// Make sure there are some manufacturers in the database
		Manufacturer manufacturer1 = new Manufacturer();
		manufacturer1.setName("Manufacturer 1");
		manufacturer1.setUrl("url1");

		Manufacturer manufacturer2 = new Manufacturer();
		manufacturer2.setName("Manufacturer 2");
		manufacturer2.setUrl("url2");

		manufacturerService.save(manufacturer1);
		manufacturerService.save(manufacturer2);
	}


	/**
	 * Tests the following:
	 * 
	 * * 1. New seed packets can be successfully added to the repository, and are persisted in 
	 *   the database accordingly.
	 *   
	 * * 2. Once a seed packet has been added, if the Add New window is reopened, the fields from the 
	 *   last seed packet are not carried over.
	 *   
	 * * 3. Entries saved to the database can be retrieved and viewed.
	 * 
	 * * 4. Entries saved to the database can be edited, and the changes are persisted in the database.
	 * 
	 * * 5. Seed packets can be deleted, and will be removed from the database.
	 */
	@Test
	public void testSeedPacketRepositoryManagement() {

		String seedPacket1 = "seed";
		String seedPacket2 = "seed2";
		String variety = "var";

		// NEW SEED PACKETS CAN BE ADDED TO THE REPOSITORY AND ARE PERSISTED IN THE DATABASE //

		// Assert that the seed packet to be input doesn't yet exist in the database
		assertFalse(seedPacketService.exists(seedPacket1)); 

		// Add a new seed packet to the database
		robot.clickOn("#repositoryTab")
		.clickOn("#addButton");

		// Ensure we open in the correct mode
		Stage stage = (Stage) Stage.getWindows().get(1);
		assertEquals("Should open in Add New mode", "Add New", stage.getTitle()); 

		robot.clickOn("#name")
		.type(KeyCode.S).type(KeyCode.E).type(KeyCode.E).type(KeyCode.D)
		.clickOn("#typeCombo")
		.type(KeyCode.DOWN).type(KeyCode.DOWN)
		.clickOn("#harvestFromCombo").type(KeyCode.DOWN).type(KeyCode.ENTER)
		.clickOn("#harvestUntilCombo").type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.ENTER)
		.clickOn("#saveButton");

		// Assert that the seed packet NOW exists in the database
		assertTrue(seedPacketService.exists(seedPacket1));

		// SAVING A SECOND NEW SEED PACKET DOES NOT INCLUDE INFORMATION FROM THE PREVIOUS ONE //

		// Assert that the seed packet to be input doesn't yet exist in the database
		assertFalse(seedPacketService.exists(seedPacket2)); 

		// Add a second seed packet
		robot.clickOn("#addButton")
		.clickOn("#name")
		.type(KeyCode.S).type(KeyCode.E).type(KeyCode.E).type(KeyCode.D).type(KeyCode.DIGIT2)
		.clickOn("#typeCombo")
		.type(KeyCode.DOWN).type(KeyCode.DOWN)		      // No harvest values set this time
		.clickOn("#saveButton");

		// Assert that the second seed packet now exists in the database
		assertTrue(seedPacketService.exists(seedPacket2));

		/* Assert that the first seedPacket has a harvestFrom value set, but the second seedPacket 
		does NOT. This shows that the fields set on a previous seedPacket do not hang over when the 
		Add New pop-up is reopened */
		assertEquals("Harvest start month should be January", Month.JANUARY, seedPacketService.loadByName(seedPacket1).getHarvestStartMonth());
		assertNull("Harvest start month should be null", seedPacketService.loadByName(seedPacket2).getHarvestStartMonth());	

		// ENTRIES SAVED TO THE REPOSITORY CAN BE RETRIEVED AND VIEWED //

		// Assert that the two seedPackets are shown in the displayed TableView
		verifyThat("#seedPacketTable", hasNumRows(2));
		verifyThat("#seedPacketTable", hasTableCell(seedPacket1));
		verifyThat("#seedPacketTable", hasTableCell(seedPacket2));

		// Grab the TableView
		@SuppressWarnings("unchecked")
		TableView<SeedPacket> seedPacketTable = 
		(TableView<SeedPacket>) Stage.getWindows().get(0).getScene()
		.lookup("#seedPacketTable");

		// Select the first entry (seedPacket1)
		seedPacketTable.getSelectionModel().select(0);

		// Click to View
		robot.clickOn("#viewButton");

		// Ensure we open in the correct mode
		stage = (Stage) Stage.getWindows().get(1);
		assertEquals("Should open in View mode", "View", stage.getTitle()); 

		verifyThat("#name", hasText(seedPacket1)); // Ensure we are viewing the correct seed packet

		robot.clickOn("#cancelButton");

		// ENTRIES CAN BE EDITED AND THE CHANGES ARE PERSISTED IN THE DATABASE //

		robot.clickOn("#editButton");

		// Ensure we open in the correct mode
		stage = (Stage) Stage.getWindows().get(1);
		assertEquals("Should open in Edit mode", "Edit", stage.getTitle()); 

		verifyThat("#name", hasText(seedPacket1)); // Ensure we are editing the correct seed packet

		robot.clickOn("#variety")
		.type(KeyCode.V).type(KeyCode.A).type(KeyCode.R)
		.clickOn("#saveButton");

		// Assert that the changes are persisted in the database
		assertEquals("Variety should be 'var'", variety, seedPacketService.loadByName(seedPacket1).getVariety());

		// Check the changes are reflected in view mode
		seedPacketTable.getSelectionModel().select(1);
		robot.clickOn("#viewButton");
		verifyThat("#variety", hasText(variety));

		robot.clickOn("#cancelButton");

		// ENTRIES CAN BE DELETED AND WILL BE REMOVED FROM THE DATABASE //

		// Select seedPacket2
		seedPacketTable.getSelectionModel().select(0);

		// Click to delete
		robot.clickOn("#deleteButton")
		.clickOn("#confirmButton");

		// Assert that the seed packet no longer exists in the database
		assertFalse(seedPacketService.exists(seedPacket2)); 

		/* Assert that seedPacket2 is no longer shown in the displayed TableView, but 
		   seedPacket1 has not been deleted */
		verifyThat("#seedPacketTable", hasNumRows(1));
		verifyThat("#seedPacketTable", hasTableCell(seedPacket1));
	}


	/**
	 * Tests the following:
	 * 
	 * * 1. Manufacturers in the database are included in the Manufacturer combo box via repository maintenance.
	 *   
	 * * 2. The user is able to add a new manufacturer via repository maintenance.
	 *   
	 * * 3. The user can save a new seed packet with the new manufacturer added via repository maintenance.
	 * @throws InterruptedException 
	 * 
	 */
	@Test
	public void testAddingManufacturerThroughRepositoryMaintenance() throws InterruptedException {

		robot.clickOn("#repositoryTab")
		.clickOn("#addButton");

		// Verify that the manufacturers in the database are present in the combo box
		ComboBox<String> manufacturerCombo = robot.lookup("#manufacturerCombo").queryComboBox();

		manufacturerCombo.getItems().containsAll(Lists.list("Manufacturer1", "Manufacturer2"));

		// Try to add a new manufacturer
		for (Node child : manufacturerCombo.getChildrenUnmodifiable()) {
			if (child.getStyleClass().contains("arrow-button")) {
				Node arrowRegion = ((Pane) child).getChildren().get(0);
				robot.clickOn(arrowRegion);
				Thread.sleep(100);
				robot.clickOn("Add new...");
			}
		}

		Stage stage = (Stage) Stage.getWindows().get(2);
		assertEquals("Should open in Add New Manufacturer", "Add New Manufacturer", stage.getTitle()); 

		// Verify the new manufacturer is not yet in the database
		assertFalse(manufacturerService.existsByName("new")); 

		robot.clickOn("#nameField")
		.type(KeyCode.N).type(KeyCode.E).type(KeyCode.W)
		.clickOn("#urlField")
		.type(KeyCode.U).type(KeyCode.R).type(KeyCode.L)
		.clickOn("#saveManufacturerButton");

		// Verify the new manufacturer is now in the database
		assertTrue(manufacturerService.existsByName("new")); 

		// Verify that the seed packet can be saved with the new manufacturer
		robot.clickOn("#name")
		.type(KeyCode.S).type(KeyCode.E).type(KeyCode.E).type(KeyCode.D).type(KeyCode.DIGIT3)
		.clickOn("#typeCombo")
		.type(KeyCode.DOWN).type(KeyCode.DOWN)
		.clickOn("#saveButton");

		SeedPacket savedSeedPacket = seedPacketService.loadByName("seed3");

		assertEquals("Manufacturer should be 'new'", "new", savedSeedPacket.getManufacturer());
	}


	/**
	 * Tests the following:
	 * 
	 * * 1. Types in the database are included in the Type combo box via repository maintenance.
	 *   
	 * * 2. The user is able to add a new type via repository maintenance.
	 *   
	 * * 3. The user can save a new seed packet with the new type added via repository maintenance.
	 * @throws InterruptedException 
	 * 
	 */
	@Test
	public void testAddingTypeThroughRepositoryMaintenance() throws InterruptedException {

		robot.clickOn("#repositoryTab")
		.clickOn("#addButton");

		// Verify that the types in the database are present in the combo box
		ComboBox<String> typeCombo = robot.lookup("#typeCombo").queryComboBox();

		typeCombo.getItems().containsAll(Lists.list("Type 1", "Type 2"));

		// Try to add a new type
		for (Node child : typeCombo.getChildrenUnmodifiable()) {
			if (child.getStyleClass().contains("arrow-button")) {
				Node arrowRegion = ((Pane) child).getChildren().get(0);
				robot.clickOn(arrowRegion);
				Thread.sleep(100);
				robot.clickOn("Add new...");
			}
		}

		Stage stage = (Stage) Stage.getWindows().get(2);
		assertEquals("Should open in Add New Type", "Add New Type", stage.getTitle()); 

		// Verify the new type is not yet in the database
		assertFalse(typeService.existsByName("new")); 

		robot.clickOn("#nameField")
		.type(KeyCode.N).type(KeyCode.E).type(KeyCode.W)
		.clickOn("#familyField")
		.type(KeyCode.F).type(KeyCode.A).type(KeyCode.M)
		.clickOn("#edibleCheckbox")
		.clickOn("#saveTypeButton");

		// Verify the new type is now in the database
		assertTrue(typeService.existsByName("new")); 

		// Verify that the seed packet can be saved with the new type
		robot.clickOn("#name")
		.type(KeyCode.S).type(KeyCode.E).type(KeyCode.E).type(KeyCode.D).type(KeyCode.DIGIT4)
		.clickOn("#saveButton");

		SeedPacket savedSeedPacket = seedPacketService.loadByName("seed4");

		assertEquals("Type should be 'new'", "new", savedSeedPacket.getType());
	}
}
