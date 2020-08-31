package application.manufacturer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * The service for controlling the interaction of {@link Manufacturer}s with the underlying database.
 */
@Service
public class ManufacturerService {

	@Autowired private ManufacturerRepository manufacturerRepository;


	/**
	 * Saves a {@link Manufacturer} to the database.
	 * @param manufacturer The manufacturer to be saved
	 */
	public void save(Manufacturer manufacturer) {
		manufacturerRepository.save(manufacturer);
	}


	/**
	 * Load all of the {@link Manufacturer}s stored in the database.
	 * @return A list of manufacturer models
	 */
	public List<Manufacturer> loadAll() {
		List<Manufacturer> manufacturer = new ArrayList<>();
		manufacturerRepository.findAll().forEach(manufacturer::add);
		return manufacturer;
	}
	
	
	/**
	 * Checks whether a manufacturer by that name exists in the database.
	 * @param name The name of the manufacturer to be searched for
	 * @return true if the manufacturer exists in the database, false if not
	 */
	public boolean existsByName(String name) {
		return manufacturerRepository.existsByName(name);
	}

}
