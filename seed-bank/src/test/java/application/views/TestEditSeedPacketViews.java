package application.views;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Tests the EditSeedPacket views
 */
public class TestEditSeedPacketViews extends ApplicationTest {
	
	
	/**
	 * Loads the FXML and sets the scene.
	 */
	@Override
	public void start (Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/fxml/popups/EditSeedPacket.fxml"));

		stage.setScene(new Scene((Pane) fxmlLoader.load()));
		stage.show();
	}
	
	
	/**
	 * Tests that the page is displayed as expected, with the relevant fields and buttons.
	 */
	@Test
	public void testDisplay() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				verifyThat("#name", isVisible());
				verifyThat("#latinName", isVisible());
				verifyThat("#typeCombo", isVisible());
				verifyThat("#variety", isVisible());
				verifyThat("#manufacturerCombo", isVisible());
				verifyThat("#manufacturerCode", isVisible());
				verifyThat("#packSize", isVisible());
				verifyThat("#numberRemaining", isVisible());
				verifyThat("#expirationDate", isVisible());
				verifyThat("#sowingIndoorFromCombo", isVisible());
				verifyThat("#sowingIndoorUntilCombo", isVisible());
				verifyThat("#sowingOutdoorFromCombo", isVisible());
				verifyThat("#sowingOutdoorUntilCombo", isVisible());
				verifyThat("#harvestFromCombo", isVisible());
				verifyThat("#harvestUntilCombo", isVisible());
				verifyThat("#floweringFromCombo", isVisible());
				verifyThat("#floweringUntilCombo", isVisible());
				verifyThat("#keywords", isVisible());
				verifyThat("#description", isVisible());
			}		
		});
	}
	
}
