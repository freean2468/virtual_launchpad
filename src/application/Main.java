/*
 * 프로그램 : 가상 Launchpad 
 * 개발자 : 송훈일
 * 개발기간 : Dec 19, 2020 ~
 * 문의 : https://www.youtube.com/channel/UC5yZGtDDMZDe3jmCDUB_rJA
 */
package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	public static Stage stage = null;
	public static RootController rootController = null;
	
	@Override
	public void init() {
		
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../root.fxml"));
			Parent root = fxmlLoader.load();
			rootController = (RootController) fxmlLoader.getController();
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage = primaryStage;
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		System.out.println(Thread.currentThread().getName()+": stop() 호출");
		MusicPlayer.getInstance().closeSequencer();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
