package application.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isShowing;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;


/**
 * Integration style tests to ensure that invalid currently growing records cannot be saved 
 * to the database.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCurrentlyGrowingValidation extends TestApplicationBase {

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
				.build();

		seedPacketService.save(seedPacket);	
	}

	/**
	 * Tests the following:
	 * 
	 * * 1. If a currently growing record is missing required information (e.g. seed packet), 
	 * a pop-up with an appropriate message explains the problem to the user. The currently 
	 * growing record will NOT be saved.
	 * 
	 * * 2. Once all the validation issues have been resolved, the pop up no longer appears and the  
	 * currently growing record can be saved as expected.
	 */

	@Test
	public void testInvalidInputHandling() {

		Label causeOfFailure;

		// Open the Sow New screen and try to save a blank currently growing record
		robot.clickOn("#currentlyGrowingTab")
		.clickOn("#sowNewButton")
		.clickOn("#saveNewButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("Seed packet must be set\n") && 
				causeOfFailure.getText().contains("Invalid number sown\n"));

		// Try saving a currently growing record with no seed packet set
		robot.clickOn("#okButton")
		.clickOn("#numberSown")
		.type(KeyCode.DIGIT5)
		.clickOn("#saveNewButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("Seed packet must be set\n"));

		// Ensure it did not save to the database
		assertEquals("Record should not have been saved", 0, currentlyGrowingService.loadAll().size()); 

		// Now make it valid and confirm we can save
		robot.clickOn("#okButton")
		.clickOn("#seedComboBox")
		.type(KeyCode.DOWN)
		.clickOn("#saveNewButton");

		List<CurrentlyGrowing> savedRecords = currentlyGrowingService.loadAll();
		assertEquals("Record should have been saved", 1, savedRecords.size()); 
		assertEquals("Should have saved the correct record", (Integer) 5, savedRecords.get(0).getNumberSown());
	}


	/**
	 * Tests the following specific validation rules:
	 * 
	 * * 1. The seed packet must be set.
	 *   
	 * * 2. The number sown must be numeric.
	 * 
	 * * 3. The number sown must be greater than zero.
	 * 
	 * * 4. The date sown must not be in the future.
	 */
	@Test
	public void testSpecificValidationRules() {

		Label causeOfFailure;

		/* Try to save a currently growing record where the seed packet has not been set, the number 
		sown is not numeric, and the date sown is in the future. */
		robot.clickOn("#currentlyGrowingTab")
		.clickOn("#sowNewButton")
		.clickOn("#numberSown")
		.type(KeyCode.K);

		DatePicker dateSownPicker = lookup("#dateSown").query(); 
		dateSownPicker.setValue(LocalDate.now().plusDays(1));

		robot.clickOn("#saveNewButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("Seed packet must be set\n") &&
				causeOfFailure.getText().contains("Invalid number sown\n") && 
				causeOfFailure.getText().contains("The date sown cannot be in the future\n"));

		// Try saving a currently growing record where the number sown is zero.
		robot.clickOn("#okButton")
		.clickOn("#numberSown")
		.type(KeyCode.BACK_SPACE)
		.type(KeyCode.DIGIT0)
		.clickOn("#saveNewButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("The number sown must be greater than zero\n"));	
	}


	/**
	 * Tests the following specific validation rules:
	 * 
	 * * 1. The number germinated must be numeric.
	 * 
	 * * 2. The number germinated must be greater than zero.
	 * 
	 * * 3. The date germinated must not be in the future.
	 * 
	 * * 4. The date germinated must not be before the date sown
	 * 
	 * * 5. The number germinated must not be greater than the number sown.
	 */
	@Test
	public void testSpecificValidationRulesForGerminatedRecords() {

		// Open the Sow New screen and save a valid currently growing record
		robot.clickOn("#currentlyGrowingTab")
		.clickOn("#sowNewButton")
		.clickOn("#seedComboBox")
		.type(KeyCode.DOWN)
		.clickOn("#numberSown")
		.type(KeyCode.DIGIT1).type(KeyCode.DIGIT0)
		.clickOn("#saveNewButton");

		// Progress the record to germinated
		Node column = lookup("#sownIndoorsSeedColumn").nth(1).query(); 

		robot.clickOn(column)
		.clickOn("#progressIndoorsPhase1")
		.clickOn("#saveProgressButton");

		// Now try to edit it with a non-integer number germinated and a germinated date in the future
		column = lookup("#germinatedIndoorsSeedColumn").nth(1).query(); 

		robot.clickOn(column)
		.clickOn("#editCurrentlyGrowingRecordButton")
		.clickOn("#numberGerminated")
		.type(KeyCode.DIGIT2).type(KeyCode.PERIOD).type(KeyCode.DIGIT3);

		DatePicker dateGerminatedPicker = lookup("#dateGerminated").query(); 
		dateGerminatedPicker.setValue(LocalDate.now().plusDays(1));

		robot.clickOn("#saveNewButton");

		Label causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("Invalid number germinated\n") && 
				causeOfFailure.getText().contains("The date germinated cannot be in the future\n"));
		
		// Now try to make the germinated date before the sown date
		robot.clickOn("#okButton");
		
		dateGerminatedPicker.setValue(LocalDate.now().minusDays(10));
		robot.clickOn("#saveNewButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("The date germinated cannot be before the date sown\n"));
		
		// Now make the number germinated zero
		robot.clickOn("#okButton")
		.clickOn("#numberGerminated")
		.type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE)
		.type(KeyCode.DIGIT0)
		.clickOn("#saveNewButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("The number germinated must be greater than zero\n"));

		// Now try to make the number germinated greater than the number sown
		robot.clickOn("#okButton")
		.clickOn("#numberGerminated")
		.type(KeyCode.BACK_SPACE)
		.type(KeyCode.DIGIT5).type(KeyCode.DIGIT0)
		.clickOn("#saveNewButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("The number germinated must not be greater than the number sown\n"));
	}


	/**
	 * Tests the following specific validation rules:
	 * 
	 * * 1. The number established must be numeric.
	 * 
	 * * 2. The number established must be greater than zero.
	 * 
	 * * 3. The date established must not be in the future.
	 * 
	 * * 4. The date established must not be before the date germinated
	 * 
	 * * 5. The number established must not be greater than the number germinated.
	 */
	@Test
	public void testSpecificValidationRulesForEstablishedRecords() {
		// Open the Sow New screen and save a valid currently growing record
		robot.clickOn("#currentlyGrowingTab")
		.clickOn("#sowNewButton")
		.clickOn("#seedComboBox")
		.type(KeyCode.DOWN)
		.clickOn("#numberSown")
		.type(KeyCode.DIGIT1).type(KeyCode.DIGIT0)
		.clickOn("#saveNewButton");

		// Progress the record to germinated
		Node column = lookup("#sownIndoorsSeedColumn").nth(1).query(); 

		robot.clickOn(column)
		.clickOn("#progressIndoorsPhase1")
		.clickOn("#saveProgressButton");
		
		// Progress the record to established
		column = lookup("#germinatedIndoorsSeedColumn").nth(1).query(); 

		robot.clickOn(column)
		.clickOn("#progressIndoorsPhase2")
		.clickOn("#saveProgressButton");	

		// Now try to edit it with a non-numeric number established and an established date in the future
		column = lookup("#establishedIndoorsSeedColumn").nth(1).query(); 

		robot.clickOn(column)
		.clickOn("#editCurrentlyGrowingRecordButton")
		.clickOn("#numberEstablished")
		.type(KeyCode.H);

		DatePicker dateEstablishedPicker = lookup("#dateEstablished").query(); 
		dateEstablishedPicker.setValue(LocalDate.now().plusDays(1));

		robot.clickOn("#saveNewButton");

		Label causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("Invalid number established\n") && 
				causeOfFailure.getText().contains("The date established cannot be in the future\n"));
		
		// Now try to make the established date before the germinated date
		robot.clickOn("#okButton");
		
		dateEstablishedPicker.setValue(LocalDate.now().minusDays(10));
		robot.clickOn("#saveNewButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("The date established cannot be before the date germinated\n"));

		// Now make the number established zero
		robot.clickOn("#okButton")
		.clickOn("#numberEstablished")
		.type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE)
		.type(KeyCode.DIGIT0)
		.clickOn("#saveNewButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("The number established must be greater than zero\n"));

		// Now try to make the number established greater than the number germinated
		robot.clickOn("#okButton")
		.clickOn("#numberEstablished")
		.type(KeyCode.BACK_SPACE)
		.type(KeyCode.DIGIT5).type(KeyCode.DIGIT0)
		.clickOn("#saveNewButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("The number established must not be greater than the number germinated\n"));
	}


	/*
	 * Private helper method to assert that the invalid input pop-up is displayed, 
	 * and grab the cause of failure.
	 */
	private Label grabCauseOfFailure() {
		verifyThat(window("Invalid Input!"), isShowing());
		return (Label) Stage.getWindows().get(2).getScene().lookup("#causeOfFailure");
	}

}
