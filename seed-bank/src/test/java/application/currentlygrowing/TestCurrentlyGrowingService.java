package application.currentlygrowing;

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
 * Tests for the {@link CurrentlyGrowingService}.
 */
public class TestCurrentlyGrowingService  {

	@Mock private CurrentlyGrowingRepository currentlyGrowingRepository;
	
	@InjectMocks private CurrentlyGrowingService currentlyGrowingService;
	
	
	@Before
	public void init() {
	    initMocks(this);
	}
	
	
	/**
	 * Tests that the {@link CurrentlyGrowingService#save(CurrentlyGrowing)} method functions correctly.
	 */
	@Test
	public void testSave() {
		
		// Given
		CurrentlyGrowing currentlyGrowingRecordToBeSaved = mock(CurrentlyGrowing.class);
		when(currentlyGrowingRepository.findAll()).thenReturn(null);
		
		// When
		currentlyGrowingService.save(currentlyGrowingRecordToBeSaved);
		
		// Then
		verify(currentlyGrowingRepository).save(currentlyGrowingRecordToBeSaved);	
	}
	
	
	/**
	 * Tests that the {@link CurrentlyGrowingService#loadAll()} method functions correctly.
	 */
	@Test
	public void testLoadAll() {
		
		// Given
		CurrentlyGrowing currentlyGrowingRecordInRepository = mock(CurrentlyGrowing.class);
		List<CurrentlyGrowing> savedCurrentlyGrowingRecords = new ArrayList<CurrentlyGrowing>();
		savedCurrentlyGrowingRecords.add(currentlyGrowingRecordInRepository);
		when(currentlyGrowingRepository.findAll()).thenReturn(savedCurrentlyGrowingRecords);
		
		// When
		List<CurrentlyGrowing> loadedCurrentlyGrowingRecords = currentlyGrowingService.loadAll();
		
		// Then
		verify(currentlyGrowingRepository).findAll();
		assertEquals("Expect only one entry to be returned", 1, loadedCurrentlyGrowingRecords.size());
		assertEquals("Expect 'currentlyGrowingRecordInRepository' to be returned", currentlyGrowingRecordInRepository, loadedCurrentlyGrowingRecords.get(0));	
	}
	
	
	/**
	 * Tests that the {@link CurrentlyGrowingService#delete(CurrentlyGrowing)} method functions correctly.
	 */
	@Test
	public void testDelete() {
		
		// Given
		CurrentlyGrowing currentlyGrowingRecordToBeDeleted = mock(CurrentlyGrowing.class);
		
		// When
		currentlyGrowingService.delete(currentlyGrowingRecordToBeDeleted);
		
		// Then
		verify(currentlyGrowingRepository).delete(currentlyGrowingRecordToBeDeleted);	
	}
	
	
	/**
	 * Tests that the {@link CurrentlyGrowingService#exists(Long)} method functions correctly.
	 */
	@Test
	public void testExists() {
		
		// Given
		Long seedPacketId = 123L;
		
		// When
		currentlyGrowingService.exists(seedPacketId);
		
		// Then
		verify(currentlyGrowingRepository).existsBySeedPacket(seedPacketId);
	}
		
}