package application.integration;

import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.FULL;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;
import static org.testfx.matcher.control.ListViewMatchers.isEmpty;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.api.FxRobot;

import application.TestApplicationBase;
import application.type.Type;
import application.type.TypeService;
import javafx.scene.input.KeyCode;


/**
 * Integration style tests to ensure that the today tab functionality works correctly.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestTodayTabIntegration extends TestApplicationBase {

	private FxRobot robot = new FxRobot();

	@Autowired private TypeService typeService;
	

	/**
	 * Ensure that there is a valid Type in the database before the tests run.
	 */
	@Before
	public void setUpData() {
		// Make sure there is a 'Type' in the database
		Type type = new Type();
		type.setName("Type");
		type.setFamily("Family");
		typeService.save(type);
	}


	/**
	 * Tests the following:
	 * 
	 * * 1. Ensure the summaries initially accurately reflect an empty database.
	 * 
	 * * 2. Create a new seed packet which can be sown indoors now. Ensure the summary is 
	 * 		accurately updated and the seed packet appears in the relevant table.
	 *   
	 * * 3. Create TWO new seed packets which can be sown outdoors now. Ensure the summary is 
	 * 		accurately updated and the seed packets appear in the relevant table.
	 *   
	 * * 4. Create an expired, empty seed packet with no sowing date information. Ensure the summary 
	 * 		is accurately updated and the packet does NOT appear in the tables.
	 * 
	 * * 5. Ensure that the date is correct.
	 * 
	 * * NB. Note that the Today Tab also has a table and summary for currently growing records. These 
	 * 		 are asserted in the {@link TestCurrentlyGrowingManagement} tests.
	 */
	@Test
	public void testTodayTabSummariesReflectDatabase() {
		
		// Verify that the summaries correctly show an empty library
		robot.clickOn("#todayTab");
		verifyThat("#seedsInRepository", hasText("0 seed packets in your library"));
		verifyThat("#readyToSowIndoors", hasText("0 packets can now be sown indoors"));
		verifyThat("#readyToSowOutdoors", hasText("0 packets can now be sown outdoors"));
		verifyThat("#numberExpired", hasText("0 packets are expired"));
		verifyThat("#emptyPackets", hasText("0 packets are empty"));
		verifyThat("#canBeSownIndoorsListView", isEmpty());
		verifyThat("#canBeSownOutdoorsListView", isEmpty());
		verifyThat("#currentlyGrowingListView", isEmpty());
	
		// Add a new seed packet that can be sown indoors 'now' to the database
		robot.clickOn("#repositoryTab")
		     .clickOn("#addButton")
			 .clickOn("#name")
			 .type(KeyCode.S).type(KeyCode.E).type(KeyCode.E).type(KeyCode.D).type(KeyCode.DIGIT1)
		     .clickOn("#typeCombo")
		     .type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .clickOn("#sowingIndoorUntilCombo") // Not most elegant, but Jan-December
		     .type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .clickOn("#sowingIndoorFromCombo")
		     .type(KeyCode.DOWN)
		     .clickOn("#saveButton")
			 .clickOn("#todayTab");
		
		verifyThat("#seedsInRepository", hasText("1 seed packets in your library"));
		verifyThat("#readyToSowIndoors", hasText("1 packets can now be sown indoors"));
		verifyThat("#canBeSownIndoorsListView", hasItems(1));
		
		// Add two new seed packets that can be sown outdoors 'now' to the database
		robot.clickOn("#repositoryTab")
		     .clickOn("#addButton")
			 .clickOn("#name")
			 .type(KeyCode.S).type(KeyCode.E).type(KeyCode.E).type(KeyCode.D).type(KeyCode.DIGIT2)
		     .clickOn("#typeCombo")
		     .type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .clickOn("#sowingOutdoorUntilCombo")
		     .type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .clickOn("#sowingOutdoorFromCombo")
		     .type(KeyCode.DOWN)
		     .clickOn("#saveButton")
		     .clickOn("#addButton")
			 .clickOn("#name")
			 .type(KeyCode.S).type(KeyCode.E).type(KeyCode.E).type(KeyCode.D).type(KeyCode.DIGIT3)
		     .clickOn("#typeCombo")
		     .type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .clickOn("#sowingOutdoorUntilCombo")
		     .type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .clickOn("#sowingOutdoorFromCombo")
		     .type(KeyCode.DOWN)
		     .clickOn("#saveButton")       
			 .clickOn("#todayTab");
		
		verifyThat("#seedsInRepository", hasText("3 seed packets in your library"));
		verifyThat("#readyToSowOutdoors", hasText("2 packets can now be sown outdoors"));
		verifyThat("#canBeSownOutdoorsListView", hasItems(2));
		
		// Add a new expired and empty seed packet to the database
		robot.clickOn("#repositoryTab")
		     .clickOn("#addButton")
			 .clickOn("#name")
			 .type(KeyCode.S).type(KeyCode.E).type(KeyCode.E).type(KeyCode.D).type(KeyCode.DIGIT4)
		     .clickOn("#typeCombo")
		     .type(KeyCode.DOWN).type(KeyCode.DOWN)
		     .clickOn("#packSize") 
		     .type(KeyCode.DIGIT5)
		     .clickOn("#numberRemaining")
		     .type(KeyCode.DIGIT0)
		     .clickOn("#expirationDate")
		     .type(KeyCode.DIGIT2).type(KeyCode.DIGIT0).type(KeyCode.DIGIT0).type(KeyCode.DIGIT5)
		     .clickOn("#saveButton")
			 .clickOn("#todayTab");
		
		verifyThat("#seedsInRepository", hasText("4 seed packets in your library"));
		verifyThat("#canBeSownIndoorsListView", hasItems(1)); // Hasn't been added to the lists
		verifyThat("#canBeSownOutdoorsListView", hasItems(2));
		verifyThat("#numberExpired", hasText("1 packets are expired"));
		verifyThat("#emptyPackets", hasText("1 packets are empty"));
		
		// Ensure today's date is correct
		String expectedFormattedDate = LocalDate.now().format(ofLocalizedDate(FULL));
		verifyThat("#date", hasText(expectedFormattedDate));
	}
}
