package application.previouslygrowing;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * The repository for storing {@link PreviouslyGrowing} seeds. Corresponds to the previously_growing 
 * table of the SeedBank database.
 */
@Repository
public interface PreviouslyGrowingRepository extends CrudRepository<PreviouslyGrowing, Long> {
	
	boolean existsBySeedPacket(Long seedPacket);

}