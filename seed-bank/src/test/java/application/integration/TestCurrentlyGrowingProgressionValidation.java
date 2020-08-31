package application.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isShowing;

import java.time.LocalDate;

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
 * Integration style tests to ensure that invalid currently growing records cannot be progressed, and 
 * the record changes are not saved to the database.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCurrentlyGrowingProgressionValidation extends TestApplicationBase {

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
		.type(KeyCode.DIGIT5)
		.clickOn("#saveNewButton");
	}


	/**
	 * Tests the following:
	 * 
	 * * 1. If a user tries to progress a currently growing record with invalid values, 
	 * a pop-up with an appropriate message explains the problem to the user. The 
	 * currently growing record will NOT be saved.
	 * 
	 * * 2. If a validation issue is resolved, but other issues still exist, the pop up will accurately 
	 * reflect the current status.
	 *   
	 * * 3. Once all the validation issues have been resolved, the pop up no longer appears and the  
	 * currently growing record can be progressed as expected.
	 */

	@Test
	public void testInvalidInputHandling() {

		String causeOfFailure;

		// Try to progress to germinated with an invalid number of seeds and an invalid date
		Node column = lookup("#sownIndoorsSeedColumn").nth(1).query(); 

		robot.clickOn(column)
		.clickOn("#progressIndoorsPhase1")
		.clickOn("#progressNumberField")
		.type(KeyCode.X);

		DatePicker progressDatePicker = lookup("#progressDatePicker").query(); 
		progressDatePicker.setValue(today.plusDays(1));

		robot.clickOn("#saveProgressButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.contains("Invalid number\n") &&
				causeOfFailure.contains("The date cannot be in the future\n"));

		// Resolve the number issue but not the date issue
		robot.clickOn("#okButton")
		.clickOn("#progressNumberField")
		.type(KeyCode.BACK_SPACE)
		.clickOn("#saveProgressButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				!causeOfFailure.contains("Invalid number\n"));

		// Ensure the progression did not save to the database
		CurrentlyGrowing currentlyGrowingRecord = currentlyGrowingService.loadAll().get(0);
		assertNull("Progress should not have been saved", currentlyGrowingRecord.getDateGerminated()); 

		// Now make it valid and check the progression saved
		robot.clickOn("#okButton");
		progressDatePicker.setValue(today);
		robot.clickOn("#saveProgressButton");

		currentlyGrowingRecord = currentlyGrowingService.loadAll().get(0);
		assertEquals("Progress should have saved", today, currentlyGrowingRecord.getDateGerminated());
	}


	/**
	 * Tests the following specific validation rules:
	 * 
	 * * 1. The number set must be numeric.
	 *   
	 * * 2. The number set must be greater than zero.
	 * 
	 * * 4. Where relevant, the number germinated must not be greater than the number sown.
	 * 
	 * * 5. Where relevant, the number established must not be greater than the number germinated.
	 * 
	 * * 6. The date set must not be in the future.
	 * 
	 * * 7. Where relevant, the germination date must not be before the sown date.
	 * 
	 * * 8. Where relevant, the established date must not be before the germinated date.
	 */
	@Test
	public void testSpecificValidationRules() {

		String causeOfFailure;

		// Try to progress to germinated with a non-numeric number of seeds
		Node column = lookup("#sownIndoorsSeedColumn").nth(1).query(); 

		robot.clickOn(column)
		.clickOn("#progressIndoorsPhase1")
		.clickOn("#progressNumberField")
		.type(KeyCode.X)
		.clickOn("#saveProgressButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.contains("Invalid number\n"));

		// Now try to progress to germinated with a 0 seeds and a germination date before the sown date
		robot.clickOn("#okButton")
		.clickOn("#progressNumberField")
		.type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE)
		.type(KeyCode.DIGIT0);
		
		DatePicker progressDatePicker = lookup("#progressDatePicker").query(); 
		progressDatePicker.setValue(today.minusDays(5));
	
		robot.clickOn("#saveProgressButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.contains("Number must be greater than zero\n") &&
				causeOfFailure.contains("The date germinated cannot be before the date sown\n"));

		// Now try to progress where the number germinated is greater than the number sown
		robot.clickOn("#okButton")
		.clickOn("#progressNumberField")
		.type(KeyCode.BACK_SPACE)
		.type(KeyCode.DIGIT5).type(KeyCode.DIGIT0)
		.clickOn("#saveProgressButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.contains("The number germinated must not be greater than the number sown\n"));

		// Now make it valid, so we can progress to check the next bit
		robot.clickOn("#okButton")
		.clickOn("#progressNumberField")
		.type(KeyCode.BACK_SPACE);
		
		progressDatePicker.setValue(today);
		
		robot.clickOn("#saveProgressButton");

		/* Try to progress where the number established is greater than the number germinated and 
		 * the established date is before the germination date  */
		column = lookup("#germinatedIndoorsSeedColumn").nth(1).query(); 

		robot.clickOn(column)
		.clickOn("#progressIndoorsPhase2")
		.clickOn("#progressNumberField")
		.type(KeyCode.DIGIT0);
		
		progressDatePicker = lookup("#progressDatePicker").query(); 
		progressDatePicker.setValue(today.minusDays(5));
		
		robot.clickOn("#saveProgressButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.contains("The number established must not be greater than the number germinated\n") &&
				causeOfFailure.contains("The date established cannot be before the date germinated\n"));

		// Now try to progress where the date established is in the future
		robot.clickOn("#okButton");

		progressDatePicker = lookup("#progressDatePicker").query(); 
		progressDatePicker.setValue(today.plusDays(1));

		robot.clickOn("#saveProgressButton");
		
		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.contains("The date cannot be in the future\n"));
	}


	/*
	 * Private helper method to assert that the invalid input pop-up is displayed, 
	 * and grab the cause of failure.
	 */
	private String grabCauseOfFailure() {
		verifyThat(window("Invalid Input!"), isShowing());
		Label label = (Label) Stage.getWindows().get(2).getScene().lookup("#causeOfFailure");
		return label.getText();
	}

}
