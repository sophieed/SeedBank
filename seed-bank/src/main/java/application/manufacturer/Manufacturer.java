package application.manufacturer;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * Entity class defining the manufacturer. This is mapped to the manufacturer table 
 * of the SeedBank database.
 */
@Entity
public class Manufacturer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") public Long id;
	
	@NotEmpty(message = "Name must be set")
	@Column(name = "name") private String name;
	
	@Column(name = "url") private String url;
	
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

	
	public String getUrl() {
		return url;
	}

	
	public void setUrl(String url) {
		this.url = url;
	}
}
