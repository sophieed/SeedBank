package application.type;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


/**
 * Tests for the {@link TypeService}.
 */
public class TestTypeService  {

	@Mock private TypeRepository typeRepository;
	
	@InjectMocks private TypeService typeService;
	
	
	@Before
	public void init() {
	    initMocks(this);
	}
	
	
	/**
	 * Tests that the {@link TypeService#save(Type)} method functions correctly.
	 */
	@Test
	public void testSave() {
		
		// Given
		Type typeToBeSaved = mock(Type.class);
		when(typeRepository.findAll()).thenReturn(null);
		
		// When
		typeService.save(typeToBeSaved);
		
		// Then
		verify(typeRepository).save(typeToBeSaved);	
	}
	
	
	/**
	 * Tests that the {@link TypeService#loadAll()} method functions correctly.
	 */
	@Test
	public void testLoadAll() {
		
		// Given
		Type typeInRepository = mock(Type.class);
		List<Type> savedTypes = new ArrayList<Type>();
		savedTypes.add(typeInRepository);
		when(typeRepository.findAll()).thenReturn(savedTypes);
		
		// When
		List<Type> loadedTypes = typeService.loadAll();
		
		// Then
		verify(typeRepository).findAll();
		assertEquals("Expect only one entry to be returned", 1, loadedTypes.size());
		assertEquals("Expect Manufacturer to be returned", typeInRepository, loadedTypes.get(0));	
	}
			
}