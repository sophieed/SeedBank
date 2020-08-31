package application.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import application.currentlygrowing.CurrentlyGrowing;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.control.TableView.TableViewSelectionModel;


/**
 * Tests for the {@link ProgressController}.
 */
public class TestProgressController extends ApplicationTest {	

	@Mock private CurrentlyGrowing currentlyGrowingRecord;
	@Mock private TableView<CurrentlyGrowing> currentlyGrowingTable;
	@Mock private TableViewSelectionModel<CurrentlyGrowing> selectionModel;
	@Mock private Label progressDateLabel;
	@Mock private Label progressNumberLabel;
	@Mock private Button saveProgressButton;
	@Mock private Button cancelProgressButton;
	@Mock private CheckBox plantOutCheckBox;
	@Mock private DatePicker progressDatePicker;
	@Mock private TextField progressNumberField;
	@Mock private Scene scene;
	@Mock private ObservableMap<KeyCombination, Runnable> accelerators;



	@InjectMocks ProgressController progressController;

	
	/**
	 * Initialises the mock objects, and mocks any core behaviour.
	 */
	@Before 
	public void init() {
		initMocks(this);
		
		when(currentlyGrowingTable.getSelectionModel()).thenReturn(selectionModel);
		when(selectionModel.getSelectedItem()).thenReturn(currentlyGrowingRecord);
		when(saveProgressButton.defaultButtonProperty()).thenReturn(new SimpleBooleanProperty(true));
		when(saveProgressButton.focusedProperty()).thenReturn(new SimpleBooleanProperty(true));
		when(cancelProgressButton.defaultButtonProperty()).thenReturn(new SimpleBooleanProperty(true));
		when(cancelProgressButton.focusedProperty()).thenReturn(new SimpleBooleanProperty(true));
		when(saveProgressButton.getScene()).thenReturn(scene);
		when(scene.getAccelerators()).thenReturn(accelerators);
	}


	/**
	 * Tests that the labels displayed are appropriate to the progression phase, when the progression 
	 * phase of the currently growing record is 1. 
	 */
	@Test
	public void testLabelIsCorrectPhase1() {
			
		// When
		progressController.initData(currentlyGrowingTable);
		WaitForAsyncUtils.waitForFxEvents();

		// Then
		String expectedDateLabel = "Date Germinated:";
		String expectedNumberLabel = "Number Germinated:";

		verify(progressDateLabel).setText(expectedDateLabel);
		verify(progressNumberLabel).setText(expectedNumberLabel);
	}

	
	/**
	 * Tests that the labels displayed are appropriate to the progression phase, when the progression 
	 * phase of the currently growing record is 2. 
	 */
	@Test
	public void testLabelIsCorrectPhase2() {
		
		// Given
		when(currentlyGrowingRecord.getDateGerminated()).thenReturn(LocalDate.now());
			
		// When
		progressController.initData(currentlyGrowingTable);
		WaitForAsyncUtils.waitForFxEvents();

		// Then
		String expectedDateLabel = "Date Established:";
		String expectedNumberLabel = "Number Established:";

		verify(progressDateLabel).setText(expectedDateLabel);
		verify(progressNumberLabel).setText(expectedNumberLabel);
	}

}
