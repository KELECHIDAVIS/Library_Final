package main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import loan.CRUDLoan;
import loan.Loan;
import student.CRUDStudent;
import student.Student;

import java.time.LocalDate;
import java.util.List;

import book.CRUDBook;
import bookcopy.*; 

class LoanEventHandler implements EventHandler<ActionEvent> {

    private TableView<BookCopyView> table;
    private ObservableList<BookCopyView> availableCopyViews;

    @Override
    public void handle(ActionEvent event) {
    	 Stage stage = new Stage(StageStyle.DECORATED);
         stage.setTitle("Create Loan");


         Label studentLabel = new Label("Student ID:");
         TextField studentIDField = new TextField();
         HBox studentBox = new HBox(10, studentLabel, studentIDField);
         	
         
         // load all avail copies 
         Label description = new Label("Enter Student's Bronco ID And Select Books They Would Like To Loan Out\n"
         		+ "If Student Tries To Loan Out More Than 5 Total Items At A Time OR Has An OverDue Loan Creation Will Not Go Through"); 
         table = new TableView<>();
         table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

         TableColumn<BookCopyView, Integer> barcodeCol = new TableColumn<>("Barcode");
         barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));



         TableColumn<BookCopyView, String> locationCol = new TableColumn<>("Location");
         locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));


         TableColumn<BookCopyView, Integer> bookIdCol = new TableColumn<>("Book ID");
         bookIdCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));

         TableColumn<BookCopyView, String> bookTitleCol = new TableColumn<>("Book Title");
         bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));


         table.getColumns().addAll(barcodeCol,  locationCol,  bookIdCol,
                                   bookTitleCol);

   
         List<BookCopyView> allCopies = CRUDBookCopy.getAvailableCopies();
         availableCopyViews = FXCollections.observableArrayList(allCopies);
         table.setItems(availableCopyViews);

       
         table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<BookCopyView>) change -> {
             if (table.getSelectionModel().getSelectedItems().size() > 5) {
                 Platform.runLater(() -> {
                     table.getSelectionModel().clearSelection(change.getList().size() - 1);
                     showAlert("Limit Reached", "You can only select up to 5 book copies per loan.");
                 });
             }
         });

         
         Button createLoanButton = new Button("Create Loan");
         createLoanButton.setOnAction(e -> {
             try {
                 createLoan(studentIDField.getText(), table.getSelectionModel().getSelectedItems());
             } catch (Exception ex) {
                 ex.printStackTrace();
                 showAlert("Error", ex.getMessage());
             }
         });

         VBox root = new VBox(20, description, studentBox , table, createLoanButton);
         root.setAlignment(Pos.TOP_CENTER);
         root.setPadding(new Insets(10));
         stage.setScene(new Scene(root, 800, 800));
         stage.show();
    }

    private void createLoan(String studentIdText, List<BookCopyView> selectedCopies) throws Exception {
        if (studentIdText.isEmpty()) throw new Exception("Student ID is required.");
        if (selectedCopies.isEmpty()) throw new Exception("Please select at least one book copy.");
        if (selectedCopies.size() > 5) throw new Exception("Cannot select more than 5 copies.");

        int studentId = Integer.parseInt(studentIdText.trim());
        Student student = CRUDStudent.getStudent(studentId);

        Loan loan = CRUDLoan.createLoan(student, selectedCopies); 
        if(loan == null) {
        	showAlert("Failure", "Student Couldn't Create A New Loan With Due To Being Overdue or Having More Than 5 Total Items"); 
        	return; 
        }
        availableCopyViews.setAll(CRUDBookCopy.getAvailableCopies());
        table.getSelectionModel().clearSelection();
        table.refresh();

        showAlert("Success", "Loan created successfully!");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

