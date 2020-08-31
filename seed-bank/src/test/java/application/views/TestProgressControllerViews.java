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
 * Tests the Progress views
 */
public class TestProgressControllerViews extends ApplicationTest {

	
	/**
	 * Loads the FXML and sets the scene.
	 */
	@Override
	public void start (Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/fxml/popups/Progress.fxml"));

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
				verifyThat("#progressDateLabel", isVisible());
				verifyThat("#progressNumberLabel", isVisible());
				verifyThat("#progressDatePicker", isVisible());
				verifyThat("#plantOutCheckBox", isVisible());
				verifyThat("#progressNumberField", isVisible());
				verifyThat("#saveProgressButton", isVisible());
				verifyThat("#cancelProgressButton", isVisible());
			}		
		});
	}

}
