package application.manufacturer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * The repository for storing {@link Manufacturer}s. Corresponds to the manufacturer 
 * table of the SeedBank database.
 */
@Repository
public interface ManufacturerRepository extends CrudRepository<Manufacturer, Long> {

	boolean existsByName(String name);
	
}