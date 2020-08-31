package application.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.isEmpty;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.api.FxRobot;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.ListViewMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import application.TestApplicationBase;
import application.currentlygrowing.CurrentlyGrowing;
import application.currentlygrowing.CurrentlyGrowingService;
import application.integration.support.SeedPacketBuilder;
import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;
import application.type.Type;
import application.type.TypeService;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;


/**
 * Integration style tests to ensure that the currently growing functionality works correctly.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCurrentlyGrowingManagement extends TestApplicationBase {

	private FxRobot robot = new FxRobot();

	@Autowired private CurrentlyGrowingService currentlyGrowingService;
	@Autowired private SeedPacketService seedPacketService;
	@Autowired private TypeService typeService;


	/**
	 * Ensure that the appropriate data is present in the database before the tests run
	 */
	@Before
	public void setUpData() {

		// Make sure there is a valid type in the database
		Type type = new Type();
		type.setName("Type");
		typeService.save(type);

		// Make sure there is a valid seed packet in the database
		SeedPacket seedPacket = new SeedPacketBuilder()
				.withName("seed packet")
				.withType("type")
				.withPackSize(50)
				.withNumberRemaining(40)
				.build();

		seedPacketService.save(seedPacket);	
	}


	/**
	 * Tests the following:
	 * 
	 * * 1. New currently growing records can be successfully added to the repository, and are persisted in 
	 *   	the database accordingly. The number remaining for the associated seed packet is adjusted 
	 *   	accordingly.
	 *   
	 * * 2. Once a currently growing record has been added, if the Sow New window is reopened, the fields 
	 * 		from the previous record are not carried over.
	 *   
	 * * 3. Entries saved to the database can be retrieved and viewed by double clicking on the item in the 
	 * 		table view.
	 * 
	 * * 4. Newly added currently growing records are correctly shown in the today tab.
	 * 
	 * * 5. Currently growing records can be edited, and the changes are persisted accordingly. If 
	 * 		the number sown has changed, the number remaining for the seed packet is adjusted accordingly.
	 * 
	 */
	@Test
	public void testCurrentlyGrowingManagement() {

		// Check currently growing is empty in Today Tab
		robot.clickOn("#todayTab");
		verifyThat("#numberCurrentlyGrowing", LabeledMatchers.hasText("0 active sowings"));
		verifyThat("#currentlyGrowingListView", isEmpty());

		// Assert that there are not yet any currently growing records in the database
		assertEquals("There should be no entries in the database", 0, currentlyGrowingService.loadAll().size()); 

		// Click the 'Sow New' button
		robot.clickOn("#currentlyGrowingTab")
		.clickOn("#sowNewButton");

		// Ensure we open in the correct mode
		Stage stage = (Stage) Stage.getWindows().get(1);
		assertEquals("Should open in Sow New mode", "Sow New Seeds", stage.getTitle()); 

		// Fill in the required information
		robot.clickOn("#seedComboBox").type(KeyCode.DOWN)
		.clickOn("#indoorsRadio")
		.clickOn("#numberSown").type(KeyCode.DIGIT5)
		.clickOn("#locationStored").type(KeyCode.F).type(KeyCode.O).type(KeyCode.O)
		.clickOn("#notes").type(KeyCode.B).type(KeyCode.A).type(KeyCode.R)
		.clickOn("#saveNewButton");

		// Assert that the record NOW exists in the database
		List<CurrentlyGrowing> savedRecords = currentlyGrowingService.loadAll();
		assertEquals("There should now be one entry in the database", 1, savedRecords.size());

		// Verify the fields were correctly stored
		CurrentlyGrowing savedRecord = savedRecords.get(0);
		assertEquals("Seed Packet ID should be 1", (Long) 1L, savedRecord.getSeedPacket());
		assertTrue("Should be indoors", savedRecord.isIndoors());
		assertEquals("Should have been sown today", LocalDate.now() , savedRecord.getDateSown());
		assertEquals("Number sown should be 5", (Integer) 5, savedRecord.getNumberSown());
		assertEquals("Location should be foo", "foo", savedRecord.getLocation());
		assertEquals("Notes should be bar", "bar", savedRecord.getNotes());

		// Verify the number remaining was reduced accordingly
		SeedPacket seedPacketUsed = seedPacketService.loadByName("seed packet");
		assertEquals("Number remaining should have decreased by 5", (Integer) 35, seedPacketUsed.getNumberRemaining());

		// SAVING A SECOND RECORD DOES NOT INCLUDE INFORMATION FROM THE PREVIOUS ONE //

		// Add a second currently growing record with some different values (note that 'Notes' is blank)
		robot.clickOn("#sowNewButton")
		.clickOn("#seedComboBox").type(KeyCode.DOWN)
		.clickOn("#indoorsRadio")
		.clickOn("#numberSown").type(KeyCode.DIGIT2)
		.clickOn("#locationStored").type(KeyCode.F).type(KeyCode.O).type(KeyCode.O).type(KeyCode.DIGIT2)
		.clickOn("#saveNewButton");

		// Assert that the second seed packet now exists in the database
		savedRecords = currentlyGrowingService.loadAll();
		assertEquals("There should now be two entries in the database", 2, savedRecords.size());

		// Verify the fields were correctly stored
		savedRecord = savedRecords.get(1);
		assertEquals("Seed Packet ID should be 1", (Long) 1L, savedRecord.getSeedPacket());
		assertTrue("Should be indoors", savedRecord.isIndoors());
		assertEquals("Should have been sown today", LocalDate.now() , savedRecord.getDateSown());
		assertEquals("Number sown should be 2", (Integer) 2, savedRecord.getNumberSown());
		assertEquals("Location should be foo2", "foo2", savedRecord.getLocation());
		assertEquals("Notes should be null", "", savedRecord.getNotes()); /* Shows the fields set on the previous 
																	   record do not hang over when a new 
																	   record is added. */

		// ENTRIES SAVED TO THE REPOSITORY CAN BE RETRIEVED AND VIEWED //

		// Assert that the two currently growing records are shown in the displayed TableView
		verifyThat("#sownIndoorsTable", hasNumRows(2));
		verifyThat("#sownIndoorsTable", hasTableCell("seed packet"));

		// Verify that double-clicking an entry in the table opens it in view mode
		Node seedColumn = lookup("#sownIndoorsSeedColumn").nth(1).query(); 
		robot.doubleClickOn(seedColumn);

		stage = (Stage) Stage.getWindows().get(1);
		assertEquals("Should open in View mode", "View", stage.getTitle()); 

		verifyThat("#notes", TextInputControlMatchers.hasText("bar")); // Ensure we are viewing the correct currently growing record

		// Verify that clicking the 'View Sowing Info' button brings up the relevant pop-up
		robot.clickOn("#cancelButton")
		.clickOn("#viewSowingButton");

		stage = (Stage) Stage.getWindows().get(1);
		assertEquals("Should open in View mode", "View", stage.getTitle()); 

		// Verify that clicking the 'View Seed Info' button brings up the relevant pop-up
		robot.clickOn("#cancelButton")
		.clickOn("#viewSeedInfoButton");

		stage = (Stage) Stage.getWindows().get(1);
		assertEquals("Should open the View Seed Packet pop-up", "View Seed Packet", stage.getTitle()); 

		// NEW RECORDS ARE SHOWN IN THE TODAY TAB
		robot.clickOn("#cancelButton")
		.clickOn("#todayTab");
		verifyThat("#numberCurrentlyGrowing", LabeledMatchers.hasText("2 active sowings"));
		verifyThat("#currentlyGrowingListView", ListViewMatchers.hasItems(1)); /* Should only have one entry
																				  in the table as both 
																				  sowings use the same
																				  seed packet.
																				*/

		// ENTRIES CAN BE EDITED
		robot.clickOn("#currentlyGrowingTab")
		.clickOn("#editCurrentlyGrowingRecordButton");

		stage = (Stage) Stage.getWindows().get(1);
		assertEquals("Should open in Edit mode", "Edit", stage.getTitle()); 

		verifyThat("#notes", TextInputControlMatchers.hasText("bar")); // Ensure we are viewing the correct currently growing record

		robot.clickOn("#locationStored")
		.type(KeyCode.DIGIT2) // Location was 'foo', change to 'foo2'
		.clickOn("#numberSown")
		.type(KeyCode.BACK_SPACE) // Number sown was 5, delete this
		.type(KeyCode.DIGIT8) // Change it to 8
		.clickOn("#saveNewButton");

		// Ensure we haven't added a new record. There should still only be 2
		savedRecords = currentlyGrowingService.loadAll();
		assertEquals("There should still be two entries in the database", 2, savedRecords.size());

		// Assert that the changes are persisted in the database
		savedRecord = savedRecords.get(0);
		assertEquals("Location should now be 'foo2'", "foo2", savedRecord.getLocation());
		assertEquals("Number sown should now be 8", (Integer) 8, savedRecord.getNumberSown());

		/* Verify that the number remaining was altered accordingly, taking into account the prior number
		 * sown. I.e...
		 * Number sown for this record before editing was 5.
		 * Number remaining prior to editing was 33 (as initially had 40, sowed 5, then sowed a further 2).
		 * Now changing number sown to 8 (as though user initially made an error recording the value).
		 * So add back the 5 initially recorded for this sowing (33 + 5 = 38)
		 * And deduct the corrected value of 8 (38 - 8 = 30)
		 */
		seedPacketUsed = seedPacketService.loadByName("seed packet");
		assertEquals("Number remaining should now be 30", (Integer) 30, seedPacketUsed.getNumberRemaining());

		// Check the changes are reflected in view mode
		robot.clickOn("#viewSowingButton");
		verifyThat("#locationStored", hasText("foo2"));
		verifyThat("#numberSown", hasText("8"));
		robot.clickOn("#cancelButton");
	}

}