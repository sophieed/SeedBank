package application.type;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * The service for controlling the interaction of {@link Type}s with the underlying database.
 */
@Service
public class TypeService {

	@Autowired private TypeRepository typeRepository;


	/**
	 * Saves a {@link Type} to the database.
	 * @param type The type to be saved
	 */
	public void save(Type type) {
		typeRepository.save(type);
	}


	/**
	 * Load all of the {@link Type}s stored in the database.
	 * @return A list of type models
	 */
	public List<Type> loadAll() {
		List<Type> type = new ArrayList<>();
		typeRepository.findAll().forEach(type::add);
		return type;
	}
	
	
	/**
	 * Checks whether a type by that name exists in the database.
	 * @param name The name of the type to be searched for
	 * @return true if the type exists in the database, false if not
	 */
	public boolean existsByName(String name) {
		return typeRepository.existsByName(name);
	}

}
