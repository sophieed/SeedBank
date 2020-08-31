package application.currentlygrowing;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * The service for controlling the interaction of {@link CurrentlyGrowing} seeds with the underlying database.
 */
@Service
public class CurrentlyGrowingService {

	@Autowired private CurrentlyGrowingRepository currentlyGrowingRepository;

	private Logger logger = LoggerFactory.getLogger(CurrentlyGrowingService.class);


	/**
	 * Saves a {@link CurrentlyGrowing} record to the database.
	 * @param currentlyGrowing The currently growing record to be saved
	 */
	public void save(CurrentlyGrowing currentlyGrowing) {
		currentlyGrowingRepository.save(currentlyGrowing);
	}


	/**
	 * Delete the {@link CurrentlyGrowing} record from the database.
	 * @param currentlyGrowing The currently growing record to be deleted
	 */
	public void delete(CurrentlyGrowing currentlyGrowing) {
		currentlyGrowingRepository.delete(currentlyGrowing);
		logger.info("This record has been deleted from the database");

	}


	/**
	 * Load all of the {@link CurrentlyGrowing} records stored in the database.
	 * @return A list of records of currently growing seeds
	 */
	public List<CurrentlyGrowing> loadAll() {
		List<CurrentlyGrowing> currentlyGrowing = new ArrayList<>();
		currentlyGrowingRepository.findAll().forEach(currentlyGrowing::add);
		return currentlyGrowing;
	}
	
	
	/**
	 * Returns a boolean denoting whether or not a specific {@link SeedPacket} is currently being grown.
	 * @param seedPacket The ID of the {@link SeedPacket}
	 * @return true if the {@link SeedPacket} is currently being grown, and false if not
	 */
	public boolean exists(Long seedPacket) {
		return currentlyGrowingRepository.existsBySeedPacket(seedPacket);
	}
	
}
