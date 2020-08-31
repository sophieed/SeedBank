package application.seeds;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * The repository for storing {@link SeedPacket}s. Corresponds to the seed_packet 
 * table of the SeedBank database.
 */
@Repository
public interface SeedPacketRepository extends CrudRepository<SeedPacket, Long> {

	boolean existsByName(String name);
	
	SeedPacket findByName(String name);
}