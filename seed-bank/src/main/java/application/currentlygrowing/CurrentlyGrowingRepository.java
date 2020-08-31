package application.currentlygrowing;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * The repository for storing {@link CurrentlyGrowing} seeds. Corresponds to the currently_growing 
 * table of the SeedBank database.
 */
@Repository
public interface CurrentlyGrowingRepository extends CrudRepository<CurrentlyGrowing, Long> {
	
	boolean existsBySeedPacket(Long seedPacket);

}