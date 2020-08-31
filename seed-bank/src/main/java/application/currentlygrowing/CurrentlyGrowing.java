package application.currentlygrowing;

import java.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entity class defining the currently growing seeds. This is mapped to the currently_growing table 
 * of the SeedBank database.
 */
@Entity
public class CurrentlyGrowing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") public Long id;
	
	@NotNull(message = "Seed Packet must be specified")
	@Column(name = "seed_packet") private Long seedPacket;

	@NotNull(message = "Number sown must be specified")
	@Column(name = "indoors") private boolean indoors;	
	
	@NotNull(message = "Number sown must be specified")
	@Column(name = "number_sown") private Integer numberSown;
	
	@NotNull(message = "Date sown must be specified")
	@Column(name = "date_sown", columnDefinition = "DATE") private LocalDate dateSown;
	
	@Column(name = "number_germinated") private Integer numberGerminated;
	@Column(name = "date_germinated", columnDefinition = "DATE") private LocalDate dateGerminated;
	@Column(name = "number_established") private Integer numberEstablished;
	@Column(name = "date_established", columnDefinition = "DATE") private LocalDate dateEstablished;		
	@Column(name = "location") private String location;
	@Column(name = "notes") private String notes;

	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getSeedPacket() {
		return seedPacket;
	}


	public void setSeedPacket(Long seedPacket) {
		this.seedPacket = seedPacket;
	}


	public boolean isIndoors() {
		return indoors;
	}


	public void setIndoors(boolean indoors) {
		this.indoors = indoors;
	}


	public Integer getNumberSown() {
		return numberSown;
	}


	public void setNumberSown(Integer numberSown) {
		this.numberSown = numberSown;
	}


	public LocalDate getDateSown() {
		return dateSown;
	}


	public void setDateSown(LocalDate dateSown) {
		this.dateSown = dateSown;
	}


	public Integer getNumberGerminated() {
		return numberGerminated;
	}


	public void setNumberGerminated(Integer numberGerminated) {
		this.numberGerminated = numberGerminated;
	}


	public LocalDate getDateGerminated() {
		return dateGerminated;
	}


	public void setDateGerminated(LocalDate dateGerminated) {
		this.dateGerminated = dateGerminated;
	}


	public Integer getNumberEstablished() {
		return numberEstablished;
	}


	public void setNumberEstablished(Integer numberEstablished) {
		this.numberEstablished = numberEstablished;
	}


	public LocalDate getDateEstablished() {
		return dateEstablished;
	}


	public void setDateEstablished(LocalDate dateEstablished) {
		this.dateEstablished = dateEstablished;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}

}
