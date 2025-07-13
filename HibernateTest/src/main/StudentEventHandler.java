package main;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import loan.Loan;
import student.Student;
import bookcopy.BookCopy;
class StudentEventHandler implements EventHandler<ActionEvent>
{

	@Override
	public void handle(ActionEvent event) {
		
		// get all students in db 
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Student> allStudents = new ArrayList<>(); 
		
	
		
    	// set up stage 
		Stage stage = new Stage(StageStyle.DECORATED); 
    	stage.setTitle("Student Management");
    	
    	allStudents = fetchStudents(session); 
    	TableView<Student> table = new TableView<>();

        TableColumn<Student, Integer> broncoIDCol = new TableColumn<>("BroncoID");
        broncoIDCol.setCellValueFactory(new PropertyValueFactory<>("broncoID"));

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> ageCol = new TableColumn<>("Address");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        
        TableColumn<Student, String> degree = new TableColumn<>("Degree");
        degree.setCellValueFactory(new PropertyValueFactory<>("degree"));
        
        TableColumn<Student, Integer> numLoans = new TableColumn<>("Num Loans");
        numLoans.setCellValueFactory(cellData ->
            new ReadOnlyObjectWrapper<>(cellData.getValue().getLoans().size())
        );

        table.getColumns().addAll(broncoIDCol, nameCol, ageCol, degree, numLoans);

        // Fetch data and add to TableView
        ObservableList<Student> data = FXCollections.observableArrayList(allStudents);
        table.setItems(data);

        
        
       
        
        //CREATE
        Label enterName= new Label("Name: "), enterAddress= new Label("Address: "), enterDegree= new Label("Degree: "); 
        
        // init all text fields 
        TextField[] enterFields= new TextField[3]; 
        
        for(int i = 0 ; i< enterFields.length; i++) { enterFields[i] = new TextField(); }
        
        
        // create button that fetches info from fields 
        Button createButton = new Button("Create Student"); 
        createButton.setOnAction(e-> createStudent(session, enterFields)); 
        HBox createBanner = new HBox(10, enterName, enterFields[0], enterAddress,enterFields[1], enterDegree, enterFields[2], createButton); 
        
        // UPDATE  
        Label broncoID= new Label("BroncoID: "), updateName= new Label("Name: "), updateAddress= new Label("Address: "), updateDegree= new Label("Degree: "); 
        
        // init all text fields 
        TextField[] updateFields= new TextField[4]; 
        
        for(int i = 0 ; i< updateFields.length; i++) { updateFields[i] = new TextField(); }
        
        
        // create button that fetches info from fields 
        Button updateButton = new Button("Update Student"); 
        updateButton.setOnAction(e-> updateStudent(session, updateFields)); 
        HBox updateBanner = new HBox(10, broncoID , updateFields[0], updateName, updateFields[1], updateAddress,updateFields[2], updateDegree, updateFields[3], updateButton);
        
        
        //DELETE
        Label deleteID= new Label("BroncoID: ");
        TextField deleteIDField = new TextField(); 
        Button deleteButton = new Button("Delete Student");
        
        deleteButton.setOnAction(e->deleteStudent(session, deleteIDField ));
        HBox deleteBanner = new HBox(10, deleteID, deleteIDField, deleteButton); 
        
        
        Label description= new Label("Here you can create, update, and delete students.\nFor student deletion, the copies that the student loaned out are set to available and the loans are deleted as well as the student");
        
        VBox root = new VBox(20, description, createBanner, updateBanner, deleteBanner, table);
        root.setAlignment(Pos.CENTER); 
        
        Scene scene = new Scene(root, 1200, 800);

        stage.setTitle("Student Management");
        stage.setScene(scene);
        stage.show();
	}
	
	private List<Student> fetchStudents(Session session){ 
		List<Student> students = null; 
		try {	
			session.beginTransaction();
			// lists all students with the amount of loans they have 
			students = session.createQuery(
			        "SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.loans ORDER BY s.broncoID",
			        Student.class
			    ).getResultList();
			
			session.getTransaction().commit();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
    	
		
	    return students; 
	}
	
	private void createStudent(Session session, TextField[] fields) {
		String name = fields[0].getText().trim(); 
		String address = fields[1].getText().trim(); 
		String degree = fields[2].getText().trim(); 
		
		// only save if all fields are filled out 
		if (!name.isEmpty() && !address.isEmpty() && !degree.isEmpty()) {
			Student student = new Student (name, address, degree); 
			
			try {
				session.beginTransaction();
				session.save(student); 
				session.getTransaction().commit();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		// clear fields 
        for (TextField f : fields) f.clear();
	}
	
	// only changes the fields that were changed, if no change field stays the same 
	
	private void updateStudent(Session session, TextField[] fields) {
		//if ID isn't alpha numeric exit early 
		String idField = fields[0].getText().trim(); 
		
		if(isNumeric(idField)) {
			
			int ID = Integer.parseInt(idField); 
			String name = fields[1].getText().trim(); 
			String address = fields[2].getText().trim(); 
			String degree = fields[3].getText().trim(); 
			
			// only save if all fields are filled out 
			try {
				session.beginTransaction();
				Student oldStudent = session.get(Student.class, ID); 
				if(!name.isEmpty()) oldStudent.setName(name);
				if(!address.isEmpty()) oldStudent.setAddress(address);
				if(!degree.isEmpty()) oldStudent.setDegree(degree);
				session.getTransaction().commit();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		// clear fields 
        for (TextField f : fields) f.clear();
	}
	
	// for deletion, if the student has current loans, those book copies become available
	public static void deleteStudent(Session session, TextField field) {
		//if ID isn't alpha numeric exit early 
		String idField = field.getText().trim(); 
		
		if(isNumeric(idField)) {
			//set all the book copies that pertain to loans that pertain to users to available 
			// then delete all loans attached to user 
			try {
		        session.beginTransaction();

		        String sql = """
		            UPDATE book_copy
		            SET available = true, loan_num = NULL
		            WHERE loan_num IN (
		                SELECT loan_num FROM loan WHERE student_bronco_id = :broncoId
		            )
		        """;

		        session.createNativeQuery(sql)
		               .setParameter("broncoId", Integer.parseInt(idField))
		               .executeUpdate();
		        
		        // now the cascading delete should delete all the loans as well 
		        Student student = session.get(Student.class, Integer.parseInt(idField)); 
		        
		        if (student != null)
		        	session.delete(student); 
		        else {
		        	System.out.println("Student Doesn't Exist");
		        }
		        session.getTransaction().commit();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			
		}
		
		field.clear(); 
	}
	
	public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str); // or Integer.parseInt(str), etc.
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
