package application;

import static org.testfx.api.FxToolkit.hideStage;
import static org.testfx.api.FxToolkit.registerPrimaryStage;

import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.testfx.framework.junit.ApplicationTest;

import application.Main;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

/**
 * Runs the application for integration testing.
 */
public class TestApplicationBase extends ApplicationTest {

	@Before
	public void setUpClass() throws Exception {
		launch(Main.class);
		registerPrimaryStage();
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.show();
	}

	@After
	public void afterEachTest() throws TimeoutException {
		hideStage();
		release(new KeyCode[]{});
		release(new MouseButton[]{});
	}

}
