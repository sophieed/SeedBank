package application.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.api.FxRobot;

import application.TestApplicationBase;
import application.currentlygrowing.CurrentlyGrowing;
import application.currentlygrowing.CurrentlyGrowingService;
import application.integration.support.SeedPacketBuilder;
import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;
import application.type.Type;
import application.type.TypeService;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;


/**
 * Integration style tests to ensure that the currently growing progression functionality works correctly.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCurrentlyGrowingProgressionManagement extends TestApplicationBase {

	private FxRobot robot = new FxRobot();
	private LocalDate today = LocalDate.now();

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
				.build();

		seedPacketService.save(seedPacket);	

		// Open the Sow New screen and save a valid currently growing record
		robot.clickOn("#currentlyGrowingTab")
		.clickOn("#sowNewButton")
		.clickOn("#seedComboBox")
		.type(KeyCode.DOWN)
		.clickOn("#numberSown")
		.type(KeyCode.DIGIT5);

		DatePicker dateSown = lookup("#dateSown").query(); 
		dateSown.setValue(today.minusDays(5));

		robot.clickOn("#saveNewButton");
	}


	/**
	 * Tests the following:
	 * 
	 * * 1. 'Sown' currently growing records can be progressed to germinated, and the data are persisted 
	 * 		to the database accordingly.
	 *   
	 * * 2. 'Germinated' currently growing records can be progressed to established, and the data are 
	 * 		persisted accordingly.
	 *   
	 * * 3. The 'germinated' and 'established' phase-specific data is only visible in view & edit mode 
	 * 		when the record has already been progressed to that phase.
	 * 
	 * * 4. When progressing a record, the labels denoting number and date are correctly populated with 
	 * 		text appropriate to the phase the record is in.
	 * 
	 * * 5. When progressing an indoors germinated record to established, the 'plant out' checkbox is 
	 * 		visible (but is not visible at any other point). Clicking this will transfer the record from
	 * 		indoors to outdoors on saving.
	 */
	@Test
	public void testCurrentlyGrowingProgression() {

		// Ensure there is only one record to make sure we are following the correct one
		assertEquals("There should be one currently growing record", 1,  currentlyGrowingService.loadAll().size());

		// View the record and ensure that the germinated and established data isn't visible
		Node column = lookup("#sownIndoorsSeedColumn").nth(1).query(); 
		robot.clickOn(column)
		.clickOn("#viewSowingButton");

		HBox germinatedData = lookup("#germinatedData").query(); 
		HBox establishedData = lookup("#establishedData").query(); 

		assertFalse("Germinated data should not be visible", germinatedData.isVisible());
		assertFalse("Established data should not be visible", establishedData.isVisible());

		// Progress the currently growing "sown" record to "germinated'
		robot.clickOn("#cancelButton")
		.clickOn("#progressIndoorsPhase1")
		.clickOn("#progressNumberField")
		.type(KeyCode.BACK_SPACE).type(KeyCode.DIGIT4);

		DatePicker progressDatePicker = lookup("#progressDatePicker").query(); 
		progressDatePicker.setValue(today.minusDays(3));

		// Ensure the labels are correctly populated and the plant out option is NOT visible
		Label progressDateLabel = (Label) lookup("#progressDateLabel").query();
		Label progressNumberLabel = (Label) lookup("#progressNumberLabel").query();
		CheckBox plantOutCheckbox = (CheckBox) lookup("#plantOutCheckBox").query();

		assertEquals("Date label should be 'Date Germinated:'", "Date Germinated:", progressDateLabel.getText());
		assertEquals("Number label should be 'Number Germinated:'", "Number Germinated:", progressNumberLabel.getText());
		assertFalse("The plant out checkbox should be visible", plantOutCheckbox.isVisible());

		robot.clickOn("#saveProgressButton");
	
		// Ensure the progress data was saved to the database
		assertEquals("There should still be one currently growing record", 1,  currentlyGrowingService.loadAll().size());
		CurrentlyGrowing currentlyGrowingRecord = currentlyGrowingService.loadAll().get(0);
		assertEquals("Germinated number should be 4", (Integer) 4,  currentlyGrowingRecord.getNumberGerminated());
		assertEquals("Germinated date should be 3 days ago", today.minusDays(3),  currentlyGrowingRecord.getDateGerminated());

		// View the record and ensure the germinated data are visible, but not the established data
		column = lookup("#germinatedIndoorsSeedColumn").nth(1).query(); 
		robot.clickOn(column)
		.clickOn("#viewSowingButton");
		
		germinatedData = lookup("#germinatedData").query(); 
		establishedData = lookup("#establishedData").query(); 
		assertTrue("Germinated data should be visible", germinatedData.isVisible());
		assertFalse("Established data should not be visible", establishedData.isVisible());

		// Progress the currently growing "germinated" record to "established' and plant out
		robot.clickOn("#cancelButton");

		column = lookup("#germinatedIndoorsSeedColumn").nth(1).query(); 
		robot.clickOn(column)
		.clickOn("#progressIndoorsPhase2")
		.clickOn("#progressNumberField")
		.type(KeyCode.BACK_SPACE).type(KeyCode.DIGIT3);

		progressDatePicker = lookup("#progressDatePicker").query(); 
		progressDatePicker.setValue(today.minusDays(2));
		plantOutCheckbox = (CheckBox) lookup("#plantOutCheckBox").query();
		plantOutCheckbox.setSelected(true);

		// Ensure the labels are correctly populated and the plant out option is visible
		progressDateLabel = (Label) lookup("#progressDateLabel").query();
		progressNumberLabel = (Label) lookup("#progressNumberLabel").query();

		assertEquals("Date label should be 'Date Established:'", "Date Established:", progressDateLabel.getText());
		assertEquals("Number label should be 'Number Established:'", "Number Established:", progressNumberLabel.getText());
		assertTrue("The plant out checkbox should be visible", plantOutCheckbox.isVisible());

		robot.clickOn("#saveProgressButton");

		// Ensure the progress data was saved to the database
		assertEquals("There should still be one currently growing record", 1,  currentlyGrowingService.loadAll().size());
		currentlyGrowingRecord = currentlyGrowingService.loadAll().get(0);
		assertEquals("Established number should be 3", (Integer) 3,  currentlyGrowingRecord.getNumberEstablished());
		assertEquals("Established date should be 2 days ago", today.minusDays(2),  currentlyGrowingRecord.getDateEstablished());
		assertFalse("The record should have been planted out", currentlyGrowingRecord.isIndoors());

		// View the record and ensure the established data are now visible
		column = lookup("#establishedOutdoorsSeedColumn").nth(1).query(); 
		robot.clickOn(column)
		.clickOn("#viewSowingButton");

		establishedData = lookup("#establishedData").query(); 
		assertTrue("Established data should be visible", establishedData.isVisible());
	}
	
	
	/**
	 * Tests the following:
	 * 
	 * * 1. 'Sown' currently growing records can be 'quick progressed' to germinated, with the number 
	 * 		of seeds germinated defaulted to the number sown, and date germinated defaulted to today's 
	 * 		date.
	 *   
	 * * 2. 'Germinated' currently growing records can be 'quick progressed' to established, with the 
	 * 		number of seeds established defaulted to the number germinated, and date established 
	 * 		defaulted to today's date.
	 */
	@Test
	public void testCurrentlyGrowingQuickProgression() {
		
		// BASELINE CHECKS
		
		// Ensure there is only one record to make sure we are following the correct one
		List<CurrentlyGrowing> currentlyGrowingRecords = currentlyGrowingService.loadAll();
		assertEquals("There should be one currently growing record", 1,  currentlyGrowingRecords.size());

		CurrentlyGrowing currentlyGrowingRecordToProgress = currentlyGrowingRecords.get(0);
		assertEquals("Number sown should be 5", (Integer) 5, currentlyGrowingRecordToProgress.getNumberSown());
		assertNull("Number germinated should be null", currentlyGrowingRecordToProgress.getNumberGerminated());
		assertNull("Date germinated should be null", currentlyGrowingRecordToProgress.getDateGerminated());
		
		// Ensure that the record is in the sown indoors table, not the germinated indoors table
		verifyThat("#sownIndoorsTable", hasNumRows(1));
		verifyThat("#sownIndoorsTable", hasTableCell("seed packet"));
		verifyThat("#germinatedIndoorsTable", hasNumRows(0));
		
		// TEST Cmd+P QUICK-PROGRESSES THE RECORD
		Node column = lookup("#sownIndoorsSeedColumn").nth(1).query(); 
		robot.clickOn(column)
		.push(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
		
		// Ensure that the record is NOW in the germinated indoors table, not the sown indoors table
		verifyThat("#sownIndoorsTable", hasNumRows(0));
		verifyThat("#germinatedIndoorsTable", hasNumRows(1));
		verifyThat("#germinatedIndoorsTable", hasTableCell("seed packet"));
	
		// Ensure there is still only one record in the currently_growing table
		currentlyGrowingRecords = currentlyGrowingService.loadAll();
		assertEquals("There should still be one currently growing record", 1,  currentlyGrowingRecords.size());

		// Ensure the germination date and number have been persisted to the database correctly
		currentlyGrowingRecordToProgress = currentlyGrowingRecords.get(0);
		assertEquals("Number germinated should be 5", (Integer) 5, currentlyGrowingRecordToProgress.getNumberGerminated());
		assertEquals("Date germinated should be today", LocalDate.now(), currentlyGrowingRecordToProgress.getDateGerminated());
		
		// Ensure the established date and number are not set and the table is empty
		assertNull("Number established should be null", currentlyGrowingRecordToProgress.getNumberEstablished());
		assertNull("Date established should be null", currentlyGrowingRecordToProgress.getDateEstablished());

		verifyThat("#germinatedIndoorsTable", hasNumRows(1));
		verifyThat("#germinatedIndoorsTable", hasTableCell("seed packet"));
		verifyThat("#establishedIndoorsTable", hasNumRows(0));

		// QUICK-PROGRESS AGAIN TO ESTABLISHED STAGE
		column = lookup("#germinatedIndoorsSeedColumn").nth(1).query(); 
		robot.clickOn(column)
		.push(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
		
		verifyThat("#germinatedIndoorsTable", hasNumRows(0));
		verifyThat("#establishedIndoorsTable", hasNumRows(1));
		verifyThat("#establishedIndoorsTable", hasTableCell("seed packet"));

		// Ensure there is still only one record in the currently_growing table
		currentlyGrowingRecords = currentlyGrowingService.loadAll();
		assertEquals("There should still be one currently growing record", 1,  currentlyGrowingRecords.size());

		// Ensure the established date and number have been persisted to the database correctly
		currentlyGrowingRecordToProgress = currentlyGrowingRecords.get(0);
		assertEquals("Number established should be 5", (Integer) 5, currentlyGrowingRecordToProgress.getNumberEstablished());
		assertEquals("Date established should be today", LocalDate.now(), currentlyGrowingRecordToProgress.getDateEstablished());
	}

}
