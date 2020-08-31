package application.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isShowing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.api.FxRobot;

import application.TestApplicationBase;
import application.seeds.SeedPacketService;
import application.type.Type;
import application.type.TypeService;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;


/**
 * Integration style tests to ensure that invalid seed packets cannot be added to the repository.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSeedPacketRepositoryValidation extends TestApplicationBase {

	private FxRobot robot = new FxRobot();

	@Autowired private SeedPacketService seedPacketService;
	@Autowired private TypeService typeService;
	
	
	/**
	 * Ensure that the appropriate data is present in the database before the tests run
	 */
	@Before
	public void setUpData() {
		// Make sure there is a type in the database
		Type defaultType = new Type();
		defaultType.setName("Type");
		defaultType.setFamily("Family");
		defaultType.setEdible(true);
		
		typeService.save(defaultType);
	}

	/**
	 * Tests the following:
	 * 
	 * * 1. If a seed packet is missing required information (I.e. Name or type), a pop-up with an 
	 * appropriate message explains the problem to the user. The seed packet will NOT be saved.
	 *   
	 * * 2. If a seed packet with that name already exists in the database, a pop-up will explain 
	 * the problem, and the seed packet will NOT be saved.
	 * 
	 * * 3. If a validation issue is resolved, but other issues still exist, the pop up will accurately 
	 * reflect the current status.
	 *   
	 * * 4. Once all the validation issues have been resolved, the pop up no longer appears and the  
	 * seed packet can be saved as expected.
	 */

	@Test
	public void testInvalidInputHandling() {
		
		Label causeOfFailure;

		// Open the Add New screen and try to save a blank seed packet
		robot.clickOn("#repositoryTab")
			 .clickOn("#addButton")
			 .clickOn("#saveButton");

		causeOfFailure = grabCauseOfFailure();
		
		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("Name must be set\n") && 
				causeOfFailure.getText().contains("Type must be set\n"));

		// Try saving a seed packet without a type
		robot.clickOn("#okButton")
			 .clickOn("#name")
			 .type(KeyCode.S).type(KeyCode.E).type(KeyCode.E).type(KeyCode.D)
			 .clickOn("#saveButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("Type must be set\n"));
		
		// Ensure it did not save to the database
		assertFalse(seedPacketService.exists("seed")); 
		
		// Now make it valid and confirm we can save
		robot.clickOn("#okButton")
			 .clickOn("#typeCombo")
			 .type(KeyCode.DOWN).type(KeyCode.DOWN)
			 .clickOn("#saveButton");

		assertTrue(seedPacketService.exists("seed")); 
		
		// Now try to save another seed packet with the same name
		robot.clickOn("#repositoryTab")
		 	 .clickOn("#addButton")
			 .clickOn("#name")
			 .type(KeyCode.S).type(KeyCode.E).type(KeyCode.E).type(KeyCode.D)
		 	 .clickOn("#saveButton");
		
		causeOfFailure = grabCauseOfFailure();
		
		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("seed already exists in the database.\n"));
	}
	
	
	/**
	 * Tests the following specific validation rules:
	 * 
	 * * 1. If the expiration date is set, it must be a valid date (YYYY).
	 *   
	 * * 2. If the pack size is set, it must be numeric.
	 * 
	 * * 3. If the number remaining is set, it must be numeric.
	 *   
	 * * 4. If both the pack size and number remaining are set, the number remaining must be less than or 
	 * 		equal to the original pack size.
	 * 
	 * * 5. If a 'from' combobox value is set, the 'until' value must also be set, and vice versa.
	 */
	@Test
	public void testSpecificValidationRules() {
		
		Label causeOfFailure;

		// Try to save a seed packet with an invalid expiration date, pack size, and number remaining.
		robot.clickOn("#repositoryTab")
	 	 .clickOn("#addButton")
		 .clickOn("#expirationDate")
		 .type(KeyCode.DIGIT1).type(KeyCode.DIGIT2).type(KeyCode.DIGIT3).type(KeyCode.DIGIT4)
		 .clickOn("#packSize")
		 .type(KeyCode.F).type(KeyCode.O).type(KeyCode.O)
		 .clickOn("#numberRemaining")
		 .type(KeyCode.B).type(KeyCode.A).type(KeyCode.R)
	 	 .clickOn("#saveButton");
		
		causeOfFailure = grabCauseOfFailure();
		
		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("1234 is not a valid year.\n") &&
				causeOfFailure.getText().contains("foo is not a valid pack size.\n") && 
				causeOfFailure.getText().contains("bar is not a valid number remaining.\n"));

		// Now try saving a seed packet where there are more seeds remaining than the pack should contain.
		robot.clickOn("#okButton")
		 .clickOn("#cancelButton")
	 	 .clickOn("#addButton")
		 .clickOn("#packSize")
		 .type(KeyCode.DIGIT1)
		 .clickOn("#numberRemaining")
		 .type(KeyCode.DIGIT5)
	 	 .clickOn("#saveButton");

		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("There are more seeds remaining than the pack should contain!\n"));

		// Now try saving a seed packet with incomplete sowing/harvesting/flowering information.
		robot.clickOn("#okButton")
		 .clickOn("#cancelButton")
	 	 .clickOn("#addButton")
		 .clickOn("#sowingIndoorFromCombo").type(KeyCode.DOWN).type(KeyCode.ENTER) // Invalid - from only
		 .clickOn("#sowingOutdoorFromCombo").type(KeyCode.DOWN).type(KeyCode.ENTER) // Invalid - from only
		 .clickOn("#harvestUntilCombo").type(KeyCode.DOWN).type(KeyCode.ENTER) // Invalid - until only
		 .clickOn("#floweringFromCombo").type(KeyCode.DOWN).type(KeyCode.ENTER) // Valid - from and until
		 .clickOn("#floweringUntilCombo").type(KeyCode.DOWN).type(KeyCode.ENTER) // Valid - from and until
	 	 .clickOn("#saveButton");
		
		causeOfFailure = grabCauseOfFailure();

		assertTrue("Validation message is incorrect", 
				causeOfFailure.getText().contains("Sowing indoor times are incomplete.\n")  &&
				causeOfFailure.getText().contains("Sowing outdoor times are incomplete.\n") && 
				causeOfFailure.getText().contains("Harvesting times are incomplete.\n"));
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
