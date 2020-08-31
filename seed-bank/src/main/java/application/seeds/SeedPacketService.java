package application.seeds;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * The service for controlling the interaction of {@link SeedPacket}s with the underlying database.
 */
@Service
public class SeedPacketService {

	@Autowired private SeedPacketRepository seedPacketRepository;

	private Logger logger = LoggerFactory.getLogger(SeedPacketService.class);


	/**
	 * Saves a {@link SeedPacket} to the database.
	 * @param seedPacket The seed packet to be saved
	 */
	public void save(SeedPacket seedPacket) {
		seedPacketRepository.save(seedPacket);
	}


	/**
	 * Delete the {@link SeedPacket} from the database.
	 * @param seedPacket The seed packet to be deleted
	 */
	public void delete(SeedPacket seedPacket) {
		seedPacketRepository.delete(seedPacket);
		logger.info(seedPacket.getName() + " has been deleted from the database");

	}


	/**
	 * Load all of the {@link SeedPacket}s stored in the database.
	 * @return A list of seed packet models
	 */
	public List<SeedPacket> loadAll() {
		List<SeedPacket> seedPackets = new ArrayList<>();
		seedPacketRepository.findAll().forEach(seedPackets::add);
		return seedPackets;
	}
	
	
	/**
	 * Returns a boolean denoting whether or not a {@link SeedPacket} with the 
	 * specified name exists in the database.
	 * @param name The name of the {@link SeedPacket}
	 * @return true if the {@link SeedPacket} exists in the database, and false if not
	 */
	public boolean exists(String name) {
		return seedPacketRepository.existsByName(name);
	}
	
	
	/**
	 * Uses the specified name to retrieve the required {@link SeedPacket} from the database.
	 * @param name The name of the {@link SeedPacket}
	 * @return The required {@link SeedPacket}
	 */
	public SeedPacket loadByName(String name) {
		return seedPacketRepository.findByName(name);
	}
	
	
	/**
	 * Uses the specified ID to retrieve the required {@link SeedPacket} from the database.
	 * @param id The ID of the {@link SeedPacket}
	 * @return The required {@link SeedPacket}
	 */
	public Optional<SeedPacket> loadById(Long id) {
		return seedPacketRepository.findById(id);
	}

}
