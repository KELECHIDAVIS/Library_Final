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
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import book.CRUDBook;
import bookcopy.*; 

class LoanReportEventHandler implements EventHandler<ActionEvent> {

    private TableView<Loan> table;
    private ObservableList<Loan> availableLoans ;

    @Override
    public void handle(ActionEvent event) {
    	 Stage stage = new Stage(StageStyle.DECORATED);
         stage.setTitle("Return Loans");
         	
         
         // load all avail copies 
         Label description = new Label("Here You Can Select Loans To Return Them.\nBook Copies That Were Associated With The Loans Will Be Available Again After Returning"
         		+ "\nLoans are ordered by recency"
         		+ "\nTo Search For A Specific Student Enter Their Bronco ID "); 
         
         
         // Student search feature 
         Label searchTitle= new Label("BroncoID: ");
         TextField titleField = new TextField(); 
         Button searchButton = new Button("Search Student Loans");
         
         searchButton.setOnAction(e->searchStudent(titleField.getText().trim()));
         HBox searchBanner = new HBox(10, searchTitle,titleField, searchButton); 
         
         
         table = new TableView<>();
         table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
         
         // display loans 
         table = new TableView<>();
         table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

	      TableColumn<Loan, Integer> barcodeCol = new TableColumn<>("Loan Num");
	      barcodeCol.setCellValueFactory(new PropertyValueFactory<>("loanNumber"));
	
	
	
	      TableColumn<Loan, String> locationCol = new TableColumn<>("Student Name");
	      locationCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
	
	
	      TableColumn<Loan, LocalDate> bookIdCol = new TableColumn<>("Borrowing Date");
	      bookIdCol.setCellValueFactory(new PropertyValueFactory<>("borrowingDate"));
	
	      TableColumn<Loan, LocalDate> bookTitleCol = new TableColumn<>("Due Date");
	      bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));


          table.getColumns().addAll(barcodeCol,  locationCol,  bookIdCol,
                                    bookTitleCol);
   
         List<Loan> allCopies = CRUDLoan.getAllLoans();
         availableLoans  = FXCollections.observableArrayList(allCopies);
         table.setItems(availableLoans );

       
         table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Loan>) change -> {
             if (table.getSelectionModel().getSelectedItems().size() > 5) {
                 Platform.runLater(() -> {
                     table.getSelectionModel().clearSelection(change.getList().size() - 1);
                     showAlert("Limit Reached", "You can only select up to 5 book copies per loan.");
                 });
             }
         });

         
         Button deleteLoanButton = new Button("Return Loans");
         deleteLoanButton.setOnAction(e -> {
             try {
                 deleteLoans(table.getSelectionModel().getSelectedItems());
             } catch (Exception ex) {
                 ex.printStackTrace();
                 showAlert("Error", ex.getMessage());
             }
         });

         VBox root = new VBox(20,  description ,searchBanner, table, deleteLoanButton);
         root.setAlignment(Pos.TOP_CENTER);
         root.setPadding(new Insets(10));
         stage.setScene(new Scene(root, 900, 800));
         stage.show();
    }

    private void searchStudent(String id) {
        if (id == null || id.isEmpty()) {
            showAlert("Input Error", "Please enter a Bronco ID.");
            return;
        }

        int broncoId;
        try {
            broncoId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Bronco ID must be a number.");
            return;
        }

        Student student = CRUDStudent.getStudent(broncoId);
        if (student == null) {
            showAlert("Not Found", "No student found with ID: " + broncoId);
            return;
        }

        List<Loan> studentLoans = CRUDLoan.getStudentLoans(broncoId); 
        if (studentLoans == null || studentLoans.isEmpty()) {
            showAlert("No Loans", "This student has no loans.");
            availableLoans.clear();
        } else {
            availableLoans.setAll(studentLoans);
        }

        table.refresh();
    }

	private void deleteLoans(List<Loan> loans) throws Exception {
        
    	if (loans.isEmpty()) throw new Exception("Please select at least one loan to return");

        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            session.beginTransaction();

            for (Loan loan : loans) {
                // Attach the loan to this session
                Loan attachedLoan = session.get(Loan.class, loan.getLoanNumber());

                if (attachedLoan == null) continue; // skip if already deleted

                // Mark all its book copies as available
                for (BookCopy copy : attachedLoan.getCopies()) {
                    copy.setAvailable(true);
                    copy.setLoan(null);
                    session.update(copy);
                }

                // Remove loan from student’s list
                Student student = attachedLoan.getStudent();
                if (student != null) {
                    student.getLoans().remove(attachedLoan);
                    session.update(student); // persist the change
                }

                // Delete the loan
                session.delete(attachedLoan);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            throw new Exception("Failed to return loans");
        } finally {
            session.close();
        }

        availableLoans.setAll(CRUDLoan.getAllLoans());
        table.getSelectionModel().clearSelection();
        table.refresh();

        showAlert("Success", "Loan(s) returned successfully!");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}