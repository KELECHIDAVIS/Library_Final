package loan; 

import javax.persistence.*;

import bookcopy.BookCopy;
import student.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.text.SimpleDateFormat;


@Entity
@Table(name="loan")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="loan_num")
    private int loanNumber;
    @Column(name= "student_name")
    private String studentName;
    @Column(name="borrowing_date")
    private LocalDate borrowingDate;
    @Column(name="due_date")
    private LocalDate dueDate;

    @ManyToOne()
    @JoinColumn(name="student_bronco_id")
    private Student student;

    @OneToMany(mappedBy = "loan", cascade = {CascadeType.PERSIST})
    private List<BookCopy> copies;

    public Loan(Student student ){
        this.student = student;
        this.studentName= student.getName();

        // set date
        
        this.borrowingDate = LocalDate.now();
        

        // Due Date is 180 days in the future
        this.dueDate = borrowingDate.plusDays(180);
        

    }
    public Loan() {
    	
    	// set date
        
        this.borrowingDate = LocalDate.now();
        

        // Due Date is 180 days in the future
        this.dueDate = borrowingDate.plusDays(180);
    }
    public void addCopy(BookCopy copy) throws Exception {
    	if(copies == null ) copies=  new ArrayList<>() ; 
    	
        if (copies.size()  < 5){
            copy.setAvailable(false); // copy is loaned out
            copy.setLoan(this); 
            copies.add(copy);
        }else{
             throw new Exception("Loans should have 5 or less items");
        }
    }
    
    public void returnLoanedCopies() {
        Iterator<BookCopy> iterator = copies.iterator();
        // have to do this way to avoid concurrency error :<
        while (iterator.hasNext()) {
            BookCopy c = iterator.next();
            c.setAvailable(true);
            c.setLoan(null);
            iterator.remove(); 
        }
    }
    public int getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(int loanNumber) {
        this.loanNumber = loanNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LocalDate getBorrowingDate() {
        return borrowingDate;
    }

    public void setBorrowingDate(LocalDate borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public List<BookCopy> getCopies() {
        return copies;
    }

    public void setCopies(List<BookCopy> copies) {
        this.copies = copies;
    }
}
