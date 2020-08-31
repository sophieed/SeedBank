package application.controllers;

import static javafx.collections.FXCollections.observableArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.currentlygrowing.CurrentlyGrowing;
import application.currentlygrowing.CurrentlyGrowingService;
import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

@Component
public class TabPaneManager {

	private final RepositoryTabController repositoryTabController;
	private final TodayTabController todayTabController;
	private final CurrentlyGrowingTabController currentlyGrowingController;
	
	@Autowired private SeedPacketService seedPacketService;
	@Autowired private CurrentlyGrowingService currentlyGrowingService;


	@Autowired
	public TabPaneManager(
			RepositoryTabController repositoryTabController, 
			TodayTabController todayTabController,
			CurrentlyGrowingTabController currentlyGrowingController) {
		this.repositoryTabController = repositoryTabController;
		this.todayTabController = todayTabController;
		this.currentlyGrowingController = currentlyGrowingController;
	}
	
	/**
	 * Initialise the controller.
	 */
	@FXML
	private void initialize() {
		ObservableList<SeedPacket> seedPackets = observableArrayList(seedPacketService.loadAll());
		ObservableList<CurrentlyGrowing> currentlyGrowing = observableArrayList(currentlyGrowingService.loadAll());
		repositoryTabController.initialise(seedPackets);
		todayTabController.initialise(seedPackets, currentlyGrowing);
		currentlyGrowingController.initialise(currentlyGrowing);
	}

}