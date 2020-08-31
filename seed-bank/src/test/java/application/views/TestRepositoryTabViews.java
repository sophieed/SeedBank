package application.views;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;


/**
 * Tests the RepositoryTab views
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRepositoryTabViews extends ApplicationTest {

	@Autowired private ApplicationContext applicationContext;


	/**
	 * Loads the FXML and sets the scene.
	 */
	@Override
	public void start (Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/fxml/tabs/RepositoryTab.fxml"));

		fxmlLoader.setControllerFactory(applicationContext::getBean);

		stage.setScene(new Scene(new TabPane((Tab) fxmlLoader.load()), 980, 500));
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
				verifyThat("#seedPacketTable", isVisible());
				verifyThat("#nameColumn", isVisible());
				verifyThat("#typeColumn", isVisible());
				verifyThat("#sowIndoorsFromColumn", isVisible());
				verifyThat("#sowIndoorsUntilColumn", isVisible());
				verifyThat("#sowOutdoorsFromColumn", isVisible());
				verifyThat("#sowOutdoorsUntilColumn", isVisible());
				verifyThat("#harvestFromColumn", isVisible());
				verifyThat("#harvestUntilColumn", isVisible());
				verifyThat("#floweringFromColumn", isVisible());
				verifyThat("#floweringUntilColumn", isVisible());
				
				verifyThat("#addButton", isVisible());
				verifyThat("#viewButton", isVisible());
				verifyThat("#editButton", isVisible());
				verifyThat("#deleteButton", isVisible());
			}		
		});
	}

}
