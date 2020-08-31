package application.integration;

import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isNotShowing;
import static org.testfx.matcher.base.WindowMatchers.isShowing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.api.FxRobot;

import application.TestApplicationBase;
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
 * Integration style tests to ensure that the shortcuts function correctly in the context of seed 
 * packet repository management.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class TestSeedPacketRepositoryShortcuts extends TestApplicationBase {

	private FxRobot robot = new FxRobot();

	@Autowired private SeedPacketService seedPacketService;
	@Autowired private TypeService typeService;

	/**
	 * Tests all of the following:
	 * 
	 * From the Repository tab, the following commands:
	 * Cmd+N - Opens Add New window
	 * Cmd+V - Opens View window
	 * Cmd+E - Opens Edit window
	 * Cmd+D - Opens Delete window
	 * Double click - Opens View window for the seed packet clicked
	 * 
	 * From the Add New and Edit window pop-up:
	 * Cmd+S - Saves the entry
	 * Esc - Closes the pop-up (also applies to View pop-up)
	 */
	@Test
	public void testShortcuts() {

		// Make sure there is a type in the database
		Type defaultType = new Type();
		defaultType.setName("Type");
		defaultType.setFamily("Family");
		defaultType.setEdible(true);

		typeService.save(defaultType);

		// TEST Cmd+N FROM REPOSITORY OPENS 'ADD NEW' POP-UP
		robot.clickOn("#repositoryTab")
		.clickOn("#seedPacketTable")
		.push(new KeyCodeCombination(
				KeyCode.N, KeyCombination.SHORTCUT_DOWN));

		verifyThat(window("Add New"), isShowing());

		// TEST Cmd+S FROM 'ADD NEW' POP-UP SAVES THE SEED PACKET
		robot.clickOn("#name")
		.type(KeyCode.C).type(KeyCode.M).type(KeyCode.D).type(KeyCode.SPACE).type(KeyCode.S)
		.clickOn("#typeCombo")
		.type(KeyCode.DOWN).type(KeyCode.DOWN)
		.push(new KeyCodeCombination(
				KeyCode.S, KeyCombination.SHORTCUT_DOWN));

		// Assert that the seed packet NOW exists in the database
		assertTrue(seedPacketService.exists("cmd s"));

		//  TEST Cmd+V OPENS 'VIEW' POP-UP
		@SuppressWarnings("unchecked") // Grab the TableView
		TableView<SeedPacket> seedPacketTable = 
		(TableView<SeedPacket>) Stage.getWindows().get(0).getScene()
		.lookup("#seedPacketTable");

		seedPacketTable.getSelectionModel().select(0); 	// Select the first (currently only) entry

		robot.push(new KeyCodeCombination(
				KeyCode.V, KeyCombination.SHORTCUT_DOWN));

		Window viewWindow = window("View");

		verifyThat(viewWindow, isShowing());

		// TEST Esc CLOSES THE 'VIEW' POP-UP
		robot.push(KeyCode.ESCAPE);

		verifyThat(viewWindow, isNotShowing());

		// TEST Cmd+E OPENS THE 'EDIT' POP-UP
		seedPacketTable.getSelectionModel().select(0);

		robot.push(new KeyCodeCombination(
				KeyCode.E, KeyCombination.SHORTCUT_DOWN));

		verifyThat(window("Edit"), isShowing());

		robot.push(KeyCode.ESCAPE);

		// TEST Cmd+D OPENS THE 'DELETE' POP-UP
		robot.push(new KeyCodeCombination(
				KeyCode.D, KeyCombination.SHORTCUT_DOWN));

		verifyThat(window("Delete"), isShowing());

		// TEST DOUBLE CLICK OPENS THE 'VIEW' POP-UP
		Node nameColumn = lookup("#nameColumn").nth(1).query(); 
		
		robot.push(KeyCode.ESCAPE)
			 .doubleClickOn(nameColumn);

		verifyThat(window("View"), isShowing());
	}

}
