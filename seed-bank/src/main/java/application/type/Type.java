package application.type;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * Entity class defining the type. This is mapped to the type table 
 * of the SeedBank database.
 */
@Entity
public class Type {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") public Long id;
	
	@NotEmpty(message = "Name must be set")
	@Column(name = "name") private String name;
	
	@Column(name = "family") private String family;
	
	@Column(name = "edible") private boolean edible;
	
	@Column(name = "ornamental") private boolean ornamental;
	
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	
	public String getFamily() {
		return family;
	}

	
	public void setFamily(String family) {
		this.family = family;
	}
	
	
	public boolean isEdible() {
		return edible;
	}

	
	public void setEdible(boolean edible) {
		this.edible = edible;
	}
	
	
	public boolean isOrnamental() {
		return ornamental;
	}

	
	public void setOrnamental(boolean ornamental) {
		this.ornamental = ornamental;
	}
	
}
