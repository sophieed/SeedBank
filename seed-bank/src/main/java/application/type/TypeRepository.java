package application.type;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * The repository for storing {@link Type}s. Corresponds to the type 
 * table of the SeedBank database.
 */
@Repository
public interface TypeRepository extends CrudRepository<Type, Long> {

	boolean existsByName(String name);
	
}