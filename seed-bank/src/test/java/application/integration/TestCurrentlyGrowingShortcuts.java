package application.integration;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isNotShowing;
import static org.testfx.matcher.base.WindowMatchers.isShowing;

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
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.Window;


/**
 * Integration style tests to ensure that the shortcuts function correctly in the context of the 
 * management of currently growing records.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class TestCurrentlyGrowingShortcuts extends TestApplicationBase {

	private FxRobot robot = new FxRobot();

	@Autowired private CurrentlyGrowingService currentlyGrowingService;
	@Autowired private SeedPacketService seedPacketService;
	@Autowired private TypeService typeService;


	/**
	 * Make sure we have some appropriate data in the database before starting.
	 */
	@Before
	public void setUpData() {

		// Make sure there is a type in the database
		Type defaultType = new Type();
		defaultType.setName("type");
		typeService.save(defaultType);

		// Make sure there is a seed packet in the database
		SeedPacket seedPacket = new SeedPacketBuilder()
				.withName("seed")
				.withType("type")
				.withPackSize(500).build();

		seedPacketService.save(seedPacket);
	}

	/**
	 * Tests all of the following:
	 * 
	 * From the Currently Growing tab, the following commands:
	 * Cmd+N - Opens Sow New window
	 * Cmd+V - Opens the View Sowing Information window
	 * Cmd+B - Opens the View Seed Packet Information window
	 * Cmd+E - Opens Edit window
	 * Double click - Opens View window for the record clicked
	 * 
	 * From the Sow New and Edit window pop-up:
	 * Cmd+S - Saves the entry
	 * Esc - Closes the pop-up (also applies to View pop-ups)
	 * 
	 * From the Progression window pop-up:
	 * Cmd+S - Saves the entry
	 * Esc - Closes the pop-up
	 * 
	 * Note that Cmd+P for 'Quick Progression' is instead tested in 
	 * {@link TestCurrentlyGrowingProgressionManagement#testCurrentlyGrowingQuickProgression()}.
	 */
	@Test
	public void testShortcuts() {

		// Assert that there are no currently growing records in the database
		assertEquals("There should be no currently growing records in the database", 0, currentlyGrowingService.loadAll().size());

		// TEST Cmd+N FROM CURRENTLY GROWING TAB OPENS 'SOW NEW' POP-UP
		robot.clickOn("#currentlyGrowingTab")
		.clickOn("#sownIndoorsTable")
		.push(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));

		verifyThat(window("Sow New Seeds"), isShowing());

		// TEST Cmd+S FROM 'SOW NEW' POP-UP SAVES THE CURRENTLY GROWING RECORD
		robot.clickOn("#seedComboBox")
		.type(KeyCode.DOWN)
		.clickOn("#numberSown")
		.type(KeyCode.DIGIT5)
		.push(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));

		// Assert that the record NOW exists in the database
		List<CurrentlyGrowing> currentlyGrowingSavedRecords = currentlyGrowingService.loadAll();
		assertEquals("There should be one currently growing record in the database", 1, currentlyGrowingSavedRecords.size());
		assertEquals("The correct record should have been stored",  (Integer) 5, currentlyGrowingSavedRecords.get(0).getNumberSown()); 

		//  TEST Cmd+V OPENS 'VIEW' POP-UP
		@SuppressWarnings("unchecked") // Grab the TableView
		TableView<CurrentlyGrowing> sownIndoorsTable = 
		(TableView<CurrentlyGrowing>) Stage.getWindows().get(0).getScene()
		.lookup("#sownIndoorsTable");

		sownIndoorsTable.getSelectionModel().select(0); 	// Select the first  entry

		robot.push(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));

		Window viewWindow = window("View");

		verifyThat(viewWindow, isShowing());

		// TEST Esc CLOSES THE POP-UP
		robot.push(KeyCode.ESCAPE);

		verifyThat(viewWindow, isNotShowing());

		//  TEST Cmd+B OPENS 'VIEW SEED PACKET' POP-UP
		sownIndoorsTable.getSelectionModel().select(0);

		robot.push(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN));

		verifyThat(window("View Seed Packet"), isShowing());

		// TEST Cmd+E OPENS THE 'EDIT' POP-UP
		sownIndoorsTable.getSelectionModel().select(0);

		robot.push(KeyCode.ESCAPE)
		.push(new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN));

		verifyThat(window("Edit"), isShowing());

		// TEST DOUBLE CLICK OPENS THE 'VIEW' POP-UP
		Node sownIndoorsSeedColumn = lookup("#sownIndoorsSeedColumn").nth(1).query(); 

		robot.push(KeyCode.ESCAPE)
		.doubleClickOn(sownIndoorsSeedColumn);

		verifyThat(window("View"), isShowing());
		
		// TEST Esc FROM 'PROGRESSION' POP-UP CLOSES THE POP-UP
		robot.push(KeyCode.ESCAPE)
		.clickOn("#progressIndoorsPhase1");
		
		Window progressWindow = window("Progress");
		verifyThat(progressWindow, isShowing());
		
		robot.push(KeyCode.ESCAPE);
		verifyThat(progressWindow, isNotShowing());
		
		CurrentlyGrowing currentlyGrowingRecord = currentlyGrowingService.loadAll().get(0);
		assertEquals("The number germinated should be blank", null, currentlyGrowingRecord.getNumberGerminated());
		
		// TEST Cmd+S FROM 'PROGRESSION' POP-UP SAVES THE PROGRESSION
		robot.clickOn("#progressIndoorsPhase1")
		.clickOn("#progressNumberField")
		.type(KeyCode.BACK_SPACE).type(KeyCode.DIGIT2)
		.push(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));

		currentlyGrowingRecord = currentlyGrowingService.loadAll().get(0);
		assertEquals("The number germinated should be 2", (Integer) 2, currentlyGrowingRecord.getNumberGerminated());
	}

}
