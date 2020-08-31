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
 * Tests the SowNew views
 */
public class TestSowNewViews extends ApplicationTest {

	
	/**
	 * Loads the FXML and sets the scene.
	 */
	@Override
	public void start (Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/fxml/popups/SowNew.fxml"));

		stage.setScene(new Scene((Pane) fxmlLoader.load()));
		stage.show();
	}
	
	
	/**
	 * Tests that the page is displayed as expected, with the relevant fields and buttons.
	 * Note that the germinated and established data will toggle on/off in terms of visibility 
	 * dependent on what currently growing record we are looking at and what stage it is at. 
	 * This is tested in the integration tests.
	 */
	@Test
	public void testDisplay() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				verifyThat("#seedComboBox", isVisible());
				verifyThat("#indoorsRadio", isVisible());
				verifyThat("#outdoorsRadio", isVisible());
				verifyThat("#dateSown", isVisible());
				verifyThat("#locationStored", isVisible());
				verifyThat("#numberSown", isVisible());
				verifyThat("#saveNewButton", isVisible());
				verifyThat("#dateGerminated", isVisible());
				verifyThat("#numberGerminated", isVisible());
				verifyThat("#dateEstablished", isVisible());
				verifyThat("#numberEstablished", isVisible());
			}		
		});
	}

}
