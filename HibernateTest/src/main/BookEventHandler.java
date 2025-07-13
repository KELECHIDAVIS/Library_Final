package main;

import java.time.LocalDate;
import java.util.List;

import book.Book;
import book.CRUDBook;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import student.Student;
import bookcopy.*; 
class BookEventHandler implements EventHandler<ActionEvent>
{

	private TableView<BookCopyView> table; 
	
	@Override
	public void handle(ActionEvent event) {
		Stage stage = new Stage(StageStyle.DECORATED); 
		stage.setTitle("Book Management");
		
		
		//CREATE
        Label enterTitle= new Label("Title: "), enterISBN= new Label("ISBN: "), enterDescription= new Label("Desc: "); 
        Label enterNumPgs= new Label("Num Pgs: "), enterPub= new Label("Pub: "), enterPubDate= new Label("Pub. Date: "), enterNumCopies= new Label("Num. Copies: "); 
        
        // init all text fields 
        TextField[] enterFields= new TextField[6]; 
        DatePicker dp = new DatePicker(); 
        for(int i = 0 ; i< enterFields.length; i++) { enterFields[i] = new TextField(); }
        
        
        // create button that fetches info from fields 
        Button createButton = new Button("Create Book"); 
        createButton.setOnAction(e-> createBook(enterFields,dp)); 
        HBox createBanner = new HBox(10, 
        		enterTitle, enterFields[0], 
        		enterISBN,enterFields[1], 
        		enterDescription, enterFields[2], 
        		enterNumPgs, enterFields[3], 
        		enterPub, enterFields[4], 
        		enterPubDate, dp, 
        		enterNumCopies, enterFields[5], 
        		createButton); 
        
        // add copies field 
        Label bookId= new Label("Id: "),  numCopies= new Label("Num Copies: "); 
        TextField[] copyFields= new TextField[2]; 
        
        for(int i = 0 ; i< copyFields.length; i++) { copyFields[i] = new TextField(); }
        
        Button copyButton = new Button("Add Copies ");
        
        copyButton.setOnAction(e->addCopies( copyFields ));
        HBox copyBanner = new HBox(10,bookId, copyFields[0], numCopies, copyFields[1], copyButton); 
        
        // UPDATE  
        Label upID = new Label("Id:"), upTitle= new Label("Title: "), upISBN= new Label("ISBN: "), upDescription= new Label("Desc: "); 
        Label upNumPgs= new Label("Num Pgs: "), upPub= new Label("Pub: "), upPubDate= new Label("Pub. Date: "); 
        // init all text fields 
        TextField[] updateFields= new TextField[6]; 
        DatePicker dp2 = new DatePicker(); 
        for(int i = 0 ; i< updateFields.length; i++) { updateFields[i] = new TextField(); }
        
        
        // create button that fetches info from fields 
        Button updateButton = new Button("Update Book"); 
        updateButton.setOnAction(e-> updateBook(updateFields, dp2)); 
        HBox updateBanner = new HBox(10, 
        		upID, updateFields[0], 
        		upTitle, updateFields[1], 
        		upISBN,updateFields[2], 
        		upDescription, updateFields[3], 
        		upNumPgs, updateFields[4], 
        		upPub, updateFields[5], 
        		upPubDate, dp2, 
        		updateButton); 
        
        
        //DELETE
        Label deleteID= new Label("Id: ");
        TextField deleteIDField = new TextField(); 
        Button deleteButton = new Button("Delete Book");
        
        deleteButton.setOnAction(e->deleteBook( deleteIDField ));
        HBox deleteBanner = new HBox(10, deleteID, deleteIDField, deleteButton); 
        
        
       
        
        
        
        
        // SEARCH AND TABLE 
        List<BookCopyView> allCopies = CRUDBook.searchForBookCopies(); // if nothing passed returns all 
        table = new TableView<>();

        TableColumn<BookCopyView, Integer> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        TableColumn<BookCopyView, Boolean> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("available"));

        TableColumn<BookCopyView, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<BookCopyView, Integer> loanNumCol = new TableColumn<>("Loan Num");
        loanNumCol.setCellValueFactory(new PropertyValueFactory<>("loanNum"));

        TableColumn<BookCopyView, Integer> bookIdCol = new TableColumn<>("Book ID");
        bookIdCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        
        TableColumn<BookCopyView, Integer> bookTitleCol = new TableColumn<>("Book Title");
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        
        TableColumn<BookCopyView, String> studentNameCol = new TableColumn<>("Student Name");
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<BookCopyView, LocalDate> borrowDateCol = new TableColumn<>("Borrow Date");
        borrowDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowingDate"));

        TableColumn<BookCopyView, LocalDate> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        table.getColumns().addAll(barcodeCol, availableCol, locationCol, loanNumCol, bookIdCol, bookTitleCol,
                                  studentNameCol, borrowDateCol, dueDateCol);

        ObservableList<BookCopyView> data = FXCollections.observableArrayList(allCopies);
        table.setItems(data);
        
        // SEARCH
        Label searchTitle= new Label("Enter Book Title: ");
        TextField titleField = new TextField(); 
        Button searchButton = new Button("Search For Book Copies");
        
        searchButton.setOnAction(e->searchForCopies(titleField.getText().trim()));
        HBox searchBanner = new HBox(10, searchTitle,titleField, searchButton); 
        
        
        Label description= new Label("Here you can create, update, and delete books."
        		+ "\nYou can only delete a book if all the copies are not loaned out\n"
        		+ "For updating, you can change one or all of the fields at once. Unentered fields will just remain the same\n"
        		+ "All book copies are on display including ones that are rented out."
        		+ "You can filter more by searching by a book's title "
        		);
        VBox root = new VBox(20, description, createBanner, copyBanner, updateBanner, deleteBanner, searchBanner, table);
        root.setAlignment(Pos.TOP_CENTER); 
        
        Scene scene = new Scene(root, 1800, 800);

        stage.setTitle("Book Management");
        stage.setScene(scene);
        stage.show();
	}

	private void searchForCopies(String title) {
		List<BookCopyView> searchedCopies = CRUDBook.searchForBookCopies(title); 
		ObservableList<BookCopyView> data = FXCollections.observableArrayList(searchedCopies);
        table.setItems(data);
	}

	private void deleteBook(TextField deleteIDField) {
		try {
			int id = Integer.parseInt(deleteIDField.getText()); 
			CRUDBook.deleteBook(id);
		}catch (Exception e ){
			System.out.println("Invalid ID field"); 
		}
	}

	private void updateBook(TextField[] updateFields, DatePicker dp) {
	    try {
	        int bookId = Integer.parseInt(updateFields[0].getText());

	        String title = updateFields[1].getText();
	        String author = updateFields[2].getText();
	        String publisher = updateFields[3].getText();

	        int numPages = updateFields[4].getText().isEmpty() ? 0 : Integer.parseInt(updateFields[4].getText());

	        String isbn = updateFields[5].getText();
	        LocalDate pubDate = dp.getValue(); // May be null — handle that in CRUDBook if needed

	        Book newBook = new Book(title, author, publisher, numPages, isbn, pubDate);
	        CRUDBook.updateBook(bookId, newBook);
	    } catch (NumberFormatException e) {
	        System.out.println("Invalid number input: " + e.getMessage());
	    } catch (Exception e) {
	        System.out.println("Error updating book: " + e.getMessage());
	    }
	}

	

	private void createBook(TextField[] enterFields, DatePicker dp) {
	    try {
	        String title = enterFields[0].getText();
	        String author = enterFields[1].getText();
	        String publisher = enterFields[2].getText();

	        int numPages = enterFields[3].getText().isEmpty() ? 0 : Integer.parseInt(enterFields[3].getText());

	        String isbn = enterFields[4].getText();
	        LocalDate pubDate = dp.getValue(); // Might be null

	        int copyCount = enterFields[5].getText().isEmpty() ? 0 : Integer.parseInt(enterFields[5].getText());

	        CRUDBook.createBook(title, author, publisher, numPages, isbn, pubDate, copyCount);
	    } catch (NumberFormatException e) { // if you don't put any number in 
	        System.out.println("Invalid number input: " + e.getMessage());
	    } catch (Exception e) {
	        System.out.println("Error creating book: " + e.getMessage());
	    }
	}


	private void addCopies(TextField[] copyFields) {
		try {
	        int bookId = Integer.parseInt(copyFields[0].getText());
	        int numCopies = Integer.parseInt(copyFields[1].getText());
	         
	        Book book = CRUDBook.getBook(bookId); 
	        CRUDBook.addCopies(numCopies, book);
	    } catch (NumberFormatException e) {
	        System.out.println("Invalid id or numCopies input: " + e.getMessage());
	    } catch (Exception e) {
	        System.out.println("Error updating book: " + e.getMessage());
	    }
	}
	
	
	
}