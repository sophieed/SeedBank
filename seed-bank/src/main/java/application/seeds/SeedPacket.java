package application.seeds;

import java.time.Month;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * Entity class defining the seed packet. This is mapped to the seed_packet table 
 * of the SeedBank database.
 */
@Entity
public class SeedPacket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") public Long id;
	
	@NotEmpty(message = "Name must be set")
	@Column(name = "name") private String name;
	@Column(name = "latin_name") private String latinName;
	
	@NotEmpty(message = "Type must be set")
	@Column(name = "type") private String type;
	
	@Column(name = "variety") private String variety;
	@Column(name = "manufacturer") private String manufacturer;
	@Column(name = "manufacturer_code") private String manufacturerCode;
	@Column(name = "pack_size") private Integer packSize;
	@Column(name = "number_remaining") private Integer numberRemaining;
	@Column(name = "expiration_date") private Integer expirationDate;
	@Column(name = "sowing_indoors_start_month") private Month sowingIndoorsStartMonth;
	@Column(name = "sowing_indoors_end_month") private Month sowingIndoorsEndMonth;
	@Column(name = "sowing_outdoors_start_month") private Month sowingOutdoorsStartMonth;
	@Column(name = "sowing_outdoors_end_month") private Month sowingOutdoorsEndMonth;
	@Column(name = "harvest_start_month") private Month harvestStartMonth;
	@Column(name = "harvest_end_month") private Month harvestEndMonth;
	@Column(name = "flowering_start_month") private Month floweringStartMonth;
	@Column(name = "flowering_end_month") private Month floweringEndMonth;
		@Column(name = "keywords") private String keywords;
	@Column(name = "description") private String description;

	
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


	public String getLatinName() {
		return latinName;
	}


	public void setLatinName(String latinName) {
		this.latinName = latinName;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getVariety() {
		return variety;
	}


	public void setVariety(String variety) {
		this.variety = variety;
	}


	public String getManufacturer() {
		return manufacturer;
	}


	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}


	public String getManufacturerCode() {
		return manufacturerCode;
	}


	public void setManufacturerCode(String manufacturerCode) {
		this.manufacturerCode = manufacturerCode;
	}


	public Integer getPackSize() {
		return packSize;
	}


	public void setPackSize(Integer packSize) {
		this.packSize = packSize;
	}


	public Integer getNumberRemaining() {
		return numberRemaining;
	}


	public void setNumberRemaining(Integer numberRemaining) {
		this.numberRemaining = numberRemaining;
	}


	public Integer getExpirationDate() {
		return expirationDate;
	}


	public void setExpirationDate(Integer expirationDate) {
		this.expirationDate = expirationDate;
	}


	public Month getSowingIndoorsStartMonth() {
		return sowingIndoorsStartMonth;
	}


	public void setSowingIndoorsStartMonth(Month sowingIndoorsStartMonth) {
		this.sowingIndoorsStartMonth = sowingIndoorsStartMonth;
	}


	public Month getSowingIndoorsEndMonth() {
		return sowingIndoorsEndMonth;
	}


	public void setSowingIndoorsEndMonth(Month sowingIndoorsEndMonth) {
		this.sowingIndoorsEndMonth = sowingIndoorsEndMonth;
	}


	public Month getSowingOutdoorsStartMonth() {
		return sowingOutdoorsStartMonth;
	}


	public void setSowingOutdoorsStartMonth(Month sowingOutdoorsStartMonth) {
		this.sowingOutdoorsStartMonth = sowingOutdoorsStartMonth;
	}


	public Month getSowingOutdoorsEndMonth() {
		return sowingOutdoorsEndMonth;
	}


	public void setSowingOutdoorsEndMonth(Month sowingOutdoorsEndMonth) {
		this.sowingOutdoorsEndMonth = sowingOutdoorsEndMonth;
	}


	public Month getHarvestStartMonth() {
		return harvestStartMonth;
	}


	public void setHarvestStartMonth(Month harvestStartMonth) {
		this.harvestStartMonth = harvestStartMonth;
	}


	public Month getHarvestEndMonth() {
		return harvestEndMonth;
	}


	public void setHarvestEndMonth(Month harvestEndMonth) {
		this.harvestEndMonth = harvestEndMonth;
	}

	
	public Month getFloweringStartMonth() {
		return floweringStartMonth;
	}


	public void setFloweringStartMonth(Month floweringStartMonth) {
		this.floweringStartMonth = floweringStartMonth;
	}


	public Month getFloweringEndMonth() {
		return floweringEndMonth;
	}


	public void setFloweringEndMonth(Month floweringEndMonth) {
		this.floweringEndMonth = floweringEndMonth;
	}


	public String getKeywords() {
		return keywords;
	}


	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

}
