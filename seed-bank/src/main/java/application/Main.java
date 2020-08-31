package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


@EnableTransactionManagement
@SpringBootApplication
public class Main extends Application {

	private ConfigurableApplicationContext springContext;
	private Parent rootNode;

	
	public static void main(final String[] args) {
		Application.launch(args);
	}

	
	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(Main.class);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);
		rootNode = fxmlLoader.load();
	}

	
	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(new Scene(rootNode));
		stage.setTitle("SeedBank");
		stage.show();
	}
	

	@Override
	public void stop() throws Exception {
		springContext.close();
	}

}