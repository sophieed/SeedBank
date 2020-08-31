package application.previouslygrowing;

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
 * Tests for the {@link PreviouslyGrowingService}.
 */
public class TestPreviouslyGrowingService  {

	@Mock private PreviouslyGrowingRepository previouslyGrowingRepository;
	
	@InjectMocks private PreviouslyGrowingService previouslyGrowingService;
	
	
	@Before
	public void init() {
	    initMocks(this);
	}
	
	
	/**
	 * Tests that the {@link PreviouslyGrowingService#save(PreviouslyGrowing)} method functions correctly.
	 */
	@Test
	public void testSave() {
		
		// Given
		PreviouslyGrowing previouslyGrowingRecordToBeSaved = mock(PreviouslyGrowing.class);
		when(previouslyGrowingRepository.findAll()).thenReturn(null);
		
		// When
		previouslyGrowingService.save(previouslyGrowingRecordToBeSaved);
		
		// Then
		verify(previouslyGrowingRepository).save(previouslyGrowingRecordToBeSaved);	
	}
	
	
	/**
	 * Tests that the {@link PreviouslyGrowingService#loadAll()} method functions correctly.
	 */
	@Test
	public void testLoadAll() {
		
		// Given
		PreviouslyGrowing previouslyGrowingRecordInRepository = mock(PreviouslyGrowing.class);
		List<PreviouslyGrowing> savedPreviouslyGrowingRecords = new ArrayList<PreviouslyGrowing>();
		savedPreviouslyGrowingRecords.add(previouslyGrowingRecordInRepository);
		when(previouslyGrowingRepository.findAll()).thenReturn(savedPreviouslyGrowingRecords);
		
		// When
		List<PreviouslyGrowing> loadedPreviouslyGrowingRecords = previouslyGrowingService.loadAll();
		
		// Then
		verify(previouslyGrowingRepository).findAll();
		assertEquals("Expect only one entry to be returned", 1, loadedPreviouslyGrowingRecords.size());
		assertEquals("Expect 'previouslyGrowingRecordInRepository' to be returned", previouslyGrowingRecordInRepository, loadedPreviouslyGrowingRecords.get(0));	
	}
	
	
	/**
	 * Tests that the {@link PreviouslyGrowingService#exists(Long)} method functions correctly.
	 */
	@Test
	public void testExists() {
		
		// Given
		Long seedPacketId = 123L;
		
		// When
		previouslyGrowingService.exists(seedPacketId);
		
		// Then
		verify(previouslyGrowingRepository).existsBySeedPacket(seedPacketId);
	}
		
}