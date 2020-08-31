package application.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import application.seeds.SeedPacket;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;


/**
 * Tests for the {@link ConfirmDeleteController}.
 */
public class TestConfirmDeleteController extends ApplicationTest {	

	@Mock private SeedPacket seedPacket;
	@Mock private TableViewSelectionModel<SeedPacket> selectionModel;
	@Mock private TableView<SeedPacket> seedPacketTable;
	@Mock private Label labelConfirmDelete;
	@Mock private Button confirmButton;
	@Mock private Button cancelButton;


	@InjectMocks
	private ConfirmDeleteController confirmDeleteController;


	/**
	 * Initialises the mock objects, and mocks any core behaviour.
	 */
	@Before 
	public void init() {
		initMocks(this);
		when(seedPacketTable.getSelectionModel()).thenReturn(selectionModel);
		when(selectionModel.getSelectedItem()).thenReturn(seedPacket);
		when(confirmButton.defaultButtonProperty()).thenReturn(new SimpleBooleanProperty(true));
		when(confirmButton.focusedProperty()).thenReturn(new SimpleBooleanProperty(true));
		when(cancelButton.defaultButtonProperty()).thenReturn(new SimpleBooleanProperty(true));
		when(cancelButton.focusedProperty()).thenReturn(new SimpleBooleanProperty(true));
	}


	/**
	 * Tests that the label used to prompt the user to confirm they wish to delete the 
	 * seed packet is correctly populated with the name of the packet.
	 */
	@Test
	public void testLabelIsCorrect() {

		// Given
		String seedPacketName = "Seed Packet Name";
		when(seedPacket.getName()).thenReturn(seedPacketName);

		// When
		confirmDeleteController.initData(seedPacketTable);
		WaitForAsyncUtils.waitForFxEvents();

		// Then
		String expectedLabel = "Are you sure you want to delete " 
				+ seedPacketName + " from your portfolio?";

		verify(labelConfirmDelete).setText(expectedLabel);
	}

}
