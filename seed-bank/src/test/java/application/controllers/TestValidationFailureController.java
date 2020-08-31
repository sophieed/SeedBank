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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


/**
 * Tests for the {@link ValidationFailureController}.
 */
public class TestValidationFailureController extends ApplicationTest {	

	@Mock private Label causeOfFailure;
	@Mock private Button okButton;

	@InjectMocks
	private ValidationFailureController validationFailureController;


	/**
	 * Initialises the mock objects, and mocks any core behaviour.
	 */
	@Before 
	public void init() {
		initMocks(this);
		when(okButton.defaultButtonProperty()).thenReturn(new SimpleBooleanProperty(true));
		when(okButton.focusedProperty()).thenReturn(new SimpleBooleanProperty(true));
	}


	/**
	 * Tests that the cause of failure label is correctly populated with the relevant details.
	 */
	@Test
	public void testCauseOfFailureIsCorrect() {

		// Given
		String message = "Name must be set";
	
		// When
		validationFailureController.initData(message);
		WaitForAsyncUtils.waitForFxEvents();

		// Then
		verify(causeOfFailure).setText(message);
	}

}
