package application.previouslygrowing;

import java.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import application.currentlygrowing.CurrentlyGrowing;

/**
 * Entity class defining the previously growing seeds. This is mapped to the previously_growing table 
 * of the SeedBank database. It contains the same fields as the {@link CurrentlyGrowing} entity, plus 
 * additional fields relating to the growing record's final overall performance.
 */
@Entity
public class PreviouslyGrowing {

	@Id
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
	
	@NotNull(message = "Date closed must be specified")
	@Column(name = "date_closed", columnDefinition = "DATE") private LocalDate dateClosed;
	@Column(name = "performance_notes") private String performanceNotes;
	@Column(name = "star_rating") private Integer starRating;
	@Column(name = "was_successful") private boolean wasSuccessful;
	@Column(name = "cause_of_failure") private String causeOfFailure;

	
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


	public LocalDate getDateClosed() {
		return dateClosed;
	}


	public void setDateClosed(LocalDate dateClosed) {
		this.dateClosed = dateClosed;
	}


	public String getPerformanceNotes() {
		return performanceNotes;
	}


	public void setPerformanceNotes(String performanceNotes) {
		this.performanceNotes = performanceNotes;
	}


	public Integer getStarRating() {
		return starRating;
	}


	public void setStarRating(Integer starRating) {
		this.starRating = starRating;
	}


	public boolean wasSuccessful() {
		return wasSuccessful;
	}


	public void setSuccessful(boolean wasSuccessful) {
		this.wasSuccessful = wasSuccessful;
	}


	public String getCauseOfFailure() {
		return causeOfFailure;
	}

	
	public void setCauseOfFailure(String causeOfFailure) {
		this.causeOfFailure = causeOfFailure;
	}

}
