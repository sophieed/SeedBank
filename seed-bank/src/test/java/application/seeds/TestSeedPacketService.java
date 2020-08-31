package application.seeds;

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

import application.seeds.SeedPacket;
import application.seeds.SeedPacketRepository;
import application.seeds.SeedPacketService;


/**
 * Tests for the {@link SeedPacketService}.
 */
public class TestSeedPacketService  {

	@Mock private SeedPacketRepository seedPacketRepository;
	
	@InjectMocks private SeedPacketService seedPacketService;
	
	private String name = "Seed packet name";
	private Long id = 1234L;
	
	@Before
	public void init() {
	    initMocks(this);
	}
	
	
	/**
	 * Tests that the {@link SeedPacketService#save(SeedPacket)} method functions correctly.
	 */
	@Test
	public void testSave() {
		
		// Given
		SeedPacket seedPacketToBeSaved = mock(SeedPacket.class);
		when(seedPacketRepository.findAll()).thenReturn(null);
		
		// When
		seedPacketService.save(seedPacketToBeSaved);
		
		// Then
		verify(seedPacketRepository).save(seedPacketToBeSaved);	
	}
	
	
	/**
	 * Tests that the {@link SeedPacketService#loadAll()} method functions correctly.
	 */
	@Test
	public void testLoadAll() {
		
		// Given
		SeedPacket seedPacketInRepository = mock(SeedPacket.class);
		List<SeedPacket> savedSeedPackets = new ArrayList<SeedPacket>();
		savedSeedPackets.add(seedPacketInRepository);
		when(seedPacketRepository.findAll()).thenReturn(savedSeedPackets);
		
		// When
		List<SeedPacket> loadedSeedPackets = seedPacketService.loadAll();
		
		// Then
		verify(seedPacketRepository).findAll();
		assertEquals("Expect only one entry to be returned", 1, loadedSeedPackets.size());
		assertEquals("Expect SeedPacket to be returned", seedPacketInRepository, loadedSeedPackets.get(0));	
	}
	
	
	/**
	 * Tests that the {@link SeedPacketService#delete(SeedPacket)} method functions correctly.
	 */
	@Test
	public void testDelete() {
		
		// Given
		SeedPacket seedPacketToBeDeleted = mock(SeedPacket.class);
		
		// When
		seedPacketService.delete(seedPacketToBeDeleted);
		
		// Then
		verify(seedPacketRepository).delete(seedPacketToBeDeleted);	
	}
	
	
	/**
	 * Tests that the {@link SeedPacketService#exists(String)} method functions correctly.
	 */
	@Test
	public void testExists() {
		
		// When
		seedPacketService.exists(name);
		
		// Then
		verify(seedPacketRepository).existsByName(name);
	}
	
	
	/**
	 * Tests that the {@link SeedPacketService#loadByName(String)} method functions correctly.
	 */
	@Test
	public void testLoadByName() {
		
		// When
		seedPacketService.loadByName(name);
		
		// Then
		verify(seedPacketRepository).findByName(name);
	}
	
	
	/**
	 * Tests that the {@link SeedPacketService#loadById(Long)} method functions correctly.
	 */
	@Test
	public void testLoadById() {
		
		// When
		seedPacketService.loadById(id);
		
		// Then
		verify(seedPacketRepository).findById(id);
	}
	
}