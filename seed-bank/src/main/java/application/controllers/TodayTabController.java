package application.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.FULL;
import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import application.currentlygrowing.CurrentlyGrowing;
import application.seeds.SeedPacket;
import application.seeds.SeedPacketService;

/**
 * Controller for the today tab.
 */
@Component
public class TodayTabController {

	@FXML private Tab todayTab;
	@FXML private Label date;
	@FXML private Label weatherReport;
	@FXML private Label seedsInRepository;
	@FXML private Label readyToSowIndoors;
	@FXML private Label readyToSowOutdoors;
	@FXML private Label numberCurrentlyGrowing;
	@FXML private Label numberExpired;
	@FXML private Label emptyPackets;
	@FXML private ListView<SeedPacket> canBeSownIndoorsListView;
	@FXML private ListView<SeedPacket> canBeSownOutdoorsListView;
	@FXML private ListView<SeedPacket> currentlyGrowingListView;

	@Autowired private ApplicationContext applicationContext;
	@Autowired private SeedPacketService seedPacketService;

	private LocalDate now = LocalDate.now();
	private ObservableList<SeedPacket> seedPackets;
	private ObservableList<CurrentlyGrowing> currentlyGrowing;


	/**
	 * Initialise the controller.
	 * @param seedPackets The seed packets to be used
	 */
	void initialise(ObservableList<SeedPacket> seedPackets, ObservableList<CurrentlyGrowing> currentlyGrowing) {
		this.seedPackets = seedPackets;
		this.currentlyGrowing = currentlyGrowing;
		initialiseLists();
		initialiseSummaries();
		initialiseEventHandling(Arrays.asList(canBeSownIndoorsListView, canBeSownOutdoorsListView, currentlyGrowingListView));
	}


	/*
	 * Private helper method to initialise the lists displayed on the today tab.
	 */
	private void initialiseLists() {

		setCellFactoriesForLists(Arrays.asList(canBeSownIndoorsListView, canBeSownOutdoorsListView, currentlyGrowingListView));
		Comparator<SeedPacket> seedPacketComparator = Comparator.comparing(SeedPacket::getName);

		// Sow indoors			
		ObservableList<SeedPacket> sowIndoorsList = observableArrayList();

		for (SeedPacket seed : seedPackets) {		
			boolean canNowBeSownIndoors = false;
			if (seed.getSowingIndoorsStartMonth() != null) {
				canNowBeSownIndoors = 
						seed.getSowingIndoorsStartMonth().compareTo(now.getMonth()) <= 0 
						&&	seed.getSowingIndoorsEndMonth().compareTo(now.getMonth()) >= 0;
			}

			if (canNowBeSownIndoors) {
				sowIndoorsList.add(seed);
			}
		}			
		canBeSownIndoorsListView.setItems(sowIndoorsList.sorted(seedPacketComparator));

		// Sow outdoors			
		ObservableList<SeedPacket> sowOutdoorsList = observableArrayList();

		for (SeedPacket seed : seedPackets) {		
			boolean canNowBeSownOutdoors = false;
			if (seed.getSowingOutdoorsStartMonth() != null) {
				canNowBeSownOutdoors = 
						seed.getSowingOutdoorsStartMonth().compareTo(now.getMonth()) <= 0 
						&&	seed.getSowingOutdoorsEndMonth().compareTo(now.getMonth()) >= 0;
			}

			if (canNowBeSownOutdoors) {
				sowOutdoorsList.add(seed);
			}
		}	
		canBeSownOutdoorsListView.setItems(sowOutdoorsList.sorted(seedPacketComparator));

		// Currently growing		
		List<SeedPacket> currentSeedPackets = new ArrayList<SeedPacket>();

		for (CurrentlyGrowing currentlyGrowing : currentlyGrowing) {
			currentSeedPackets.add(seedPacketService.loadById(currentlyGrowing.getSeedPacket()).get());
		}

		ObservableList<SeedPacket> currentlyGrowingList = FXCollections.observableArrayList(
				currentSeedPackets.stream()
				.filter(distinctByKey(SeedPacket::getId))
				.collect(toList()));

		currentlyGrowingListView.setItems(currentlyGrowingList.sorted(seedPacketComparator));
	}


	/*
	 * Private helper method to initialise the daily summaries
	 */
	private void initialiseSummaries() {

		date.setText(now.format(ofLocalizedDate(FULL)));

		seedsInRepository.setText(seedPackets.size() + " seed packets in your library");

		// Ready to sow indoors
		long numberReadyToSowIndoors = seedPackets.stream()
				.filter(s -> s.getSowingIndoorsStartMonth() != null) // exclude any where sowing times not set
				.filter(s -> s.getSowingIndoorsStartMonth().compareTo(now.getMonth()) <= 0 	
				&&	s.getSowingIndoorsEndMonth().compareTo(now.getMonth()) >= 0)
				.count();

		readyToSowIndoors.setText(numberReadyToSowIndoors + " packets can now be sown indoors");

		// Ready to sow outdoors
		long numberReadyToSowOutdoors = seedPackets.stream()
				.filter(s -> s.getSowingOutdoorsStartMonth() != null) // exclude any where sowing times not set
				.filter(s -> s.getSowingOutdoorsStartMonth().compareTo(now.getMonth()) <= 0 	
				&&	s.getSowingOutdoorsEndMonth().compareTo(now.getMonth()) >= 0)
				.count();

		readyToSowOutdoors.setText(numberReadyToSowOutdoors + " packets can now be sown outdoors");

		// Number currently growing
		long numberOfSeedsCurrentlyGrowing = currentlyGrowing.size();

		numberCurrentlyGrowing.setText(numberOfSeedsCurrentlyGrowing + " active sowings");

		// Expired
		long numberOfExpiredPacks = seedPackets.stream()
				.filter(s -> s.getExpirationDate() != 0) // exclude any where expiration date not set
				.filter(s -> s.getExpirationDate().compareTo(now.getYear()) < 0)
				.count();

		numberExpired.setText(numberOfExpiredPacks + " packets are expired");

		// Empty
		long numberOfEmptyPacks = seedPackets.stream()
				.filter(s -> s.getPackSize() > 0 && s.getNumberRemaining() == 0)
				.count();

		emptyPackets.setText(numberOfEmptyPacks + " packets are empty");

	}


	/*
	 * Private helper method to initialise the event handling.
	 */
	private void initialiseEventHandling(List<ListView<SeedPacket>> listViews) {

		// Handle when a user double clicks on a seed packet in any of the list views
		for (ListView<SeedPacket> listView : listViews) {

			listView.setOnMouseClicked((MouseEvent event) -> {
				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){

					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popups/EditSeedPacket.fxml"));
					fxmlLoader.setControllerFactory(applicationContext::getBean);

					Stage stage = new Stage();
					try {
						stage.setScene(new Scene((Pane) fxmlLoader.load()));

						EditSeedPacketController editSeedPacketController = fxmlLoader.getController();

						editSeedPacketController.initData(listView);
						stage.setTitle("View"); 
						stage.show();

					} catch (IOException e) {
						Logger logger = Logger.getLogger(getClass().getName());
						logger.log(Level.ERROR, "Failed to create new Window.", e);		
					}
				}
			});
		}

		// Refresh the today tab every time the tab is clicked
		todayTab.getTabPane().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
				if(newTab == todayTab) {
					initialiseSummaries();
					initialiseLists();
				}
			}
		});
	}


	/*
	 * Private helper method to set the cell factories for each ListView so that the seed packet 
	 * name is displayed rather than the objects default toString.
	 */
	private void setCellFactoriesForLists(List<ListView<SeedPacket>> lists) {

		for (ListView<SeedPacket> list : lists) {
			list.setCellFactory(lv -> new ListCell<SeedPacket>() {
				@Override
				protected void updateItem(SeedPacket seedPacket, boolean empty) {
					super.updateItem(seedPacket, empty);
					setText(seedPacket == null ? null : seedPacket.getName());
				}
			});
		}
	}


	/*
	 * Predicate for filtering a stream by a particular key
	 */
	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

}