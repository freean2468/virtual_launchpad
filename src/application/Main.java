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
	@Override
	public void init() {
		
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../root.fxml"));
			Scene scene = new Scene(root,900,900);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		System.out.println(Thread.currentThread().getName()+": stop() 호출");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
