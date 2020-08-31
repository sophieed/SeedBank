package application.manufacturer;

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
 * Tests for the {@link ManufacturerService}.
 */
public class TestManufacturerService  {

	@Mock private ManufacturerRepository manufacturerRepository;
	
	@InjectMocks private ManufacturerService manufacturerService;
	
	
	@Before
	public void init() {
	    initMocks(this);
	}
	
	
	/**
	 * Tests that the {@link ManufacturerService#save(Manufacturer)} method functions correctly.
	 */
	@Test
	public void testSave() {
		
		// Given
		Manufacturer manufacturerToBeSaved = mock(Manufacturer.class);
		when(manufacturerRepository.findAll()).thenReturn(null);
		
		// When
		manufacturerService.save(manufacturerToBeSaved);
		
		// Then
		verify(manufacturerRepository).save(manufacturerToBeSaved);	
	}
	
	
	/**
	 * Tests that the {@link ManufacturerService#loadAll()} method functions correctly.
	 */
	@Test
	public void testLoadAll() {
		
		// Given
		Manufacturer manufacturerInRepository = mock(Manufacturer.class);
		List<Manufacturer> savedManufacturers = new ArrayList<Manufacturer>();
		savedManufacturers.add(manufacturerInRepository);
		when(manufacturerRepository.findAll()).thenReturn(savedManufacturers);
		
		// When
		List<Manufacturer> loadedManufacturers = manufacturerService.loadAll();
		
		// Then
		verify(manufacturerRepository).findAll();
		assertEquals("Expect only one entry to be returned", 1, loadedManufacturers.size());
		assertEquals("Expect Manufacturer to be returned", manufacturerInRepository, loadedManufacturers.get(0));	
	}
			
}