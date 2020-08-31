package application.previouslygrowing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * The service for controlling the interaction of {@link PreviouslyGrowing} seeds with the underlying database.
 */
@Service
public class PreviouslyGrowingService {

	@Autowired private PreviouslyGrowingRepository previouslyGrowingRepository;


	/**
	 * Saves a {@link PreviouslyGrowing} record to the database.
	 * @param previouslyGrowing The previously growing record to be saved
	 */
	public void save(PreviouslyGrowing previouslyGrowing) {
		previouslyGrowingRepository.save(previouslyGrowing);
	}


	/**
	 * Load all of the {@link PreviouslyGrowing} records stored in the database.
	 * @return A list of records of previously growing seeds
	 */
	public List<PreviouslyGrowing> loadAll() {
		List<PreviouslyGrowing> previouslyGrowing = new ArrayList<>();
		previouslyGrowingRepository.findAll().forEach(previouslyGrowing::add);
		return previouslyGrowing;
	}
	
	
	/**
	 * Returns a boolean denoting whether or not a specific {@link SeedPacket} has previously been grown.
	 * @param seedPacket The ID of the {@link SeedPacket}
	 * @return true if the {@link SeedPacket} has previously been grown, and false if not
	 */
	public boolean exists(Long seedPacket) {
		return previouslyGrowingRepository.existsBySeedPacket(seedPacket);
	}
	
}
