package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.*;
import javafx.scene.layout.*; 
public class Main extends Application {
	
    @Override
    public void start(Stage primaryStage) throws Exception{
    	
    	/*
    	 * Create, fetch all, update, and delete students
    	 * Allow staff to manage student information
    	 * 
    	 * */
    	Button launchStudent = new Button("Student Management");
    	launchStudent.setOnAction(new StudentEventHandler());
    	/*
    	 * Create, fetch all, update, and delete Books
    	 * Can also set the amount of book copies the book has 
    	 * Allow staff to manage books and their associated copies.
		• Allow staff to search for books and display the availability and due dates of their copies
			when applicable.
    	 * */
    	Button launchBook= new Button("Book Management");
    	launchBook.setOnAction(new BookEventHandler());
    	/*
    	 * Allow staff to manage loans and their associated book copies.
		• Allow staff to display (on screen) receipts when loans are registered.
		• Allow staff to generate (on screen) consolidated loan reports, filtered by student and
		period.

    	 * */
    	Button launchLoans = new Button("Loan Creation");
    	launchLoans.setOnAction(new LoanEventHandler());
    	
    	
    	Button launchLoanReports = new Button("Loan Reports / Management");
    	launchLoanReports.setOnAction(new LoanReportEventHandler());
    	
    	VBox vbox = new VBox(20, launchStudent, launchBook, launchLoans, launchLoanReports);
    	
    	vbox.setAlignment(Pos.CENTER);
    	Scene homeScene = new Scene(vbox, 300, 300); 
    	
    	primaryStage.setScene(homeScene);
        primaryStage.setTitle("Library Management Software");
        primaryStage.show();
    }

    
    
    
    
    
    
    public static void main(String[] args) {
        launch(args);
    }
}