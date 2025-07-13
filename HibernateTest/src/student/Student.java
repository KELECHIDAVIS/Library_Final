package student; 

import javax.persistence.*;

import bookcopy.BookCopy;
import loan.Loan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bronco_id")
    private int broncoID;
    @Column(name="name")
    private String name;
    @Column(name="address")
    private String address;
    @Column(name="degree")
    private String degree;
    
    // delete loans when student is deleted 
    @OneToMany(mappedBy="student", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<Loan> loans; // max is 5

    public Student(String name, String address, String degree) {
        this.name = name;
        this.address = address;
        this.degree = degree;
    }
    public Student() {}
    public void addLoan(Loan loan) throws Exception {
        if (this.loans == null) {
            this.loans = new ArrayList<Loan>() ;
        }
        //ONLY ADD LOAN PREV LOANS ITEMS + NEW LOAN ITEMS IS <=5
        int newTotal =  getTotalLoanedItems() + loan.getCopies().size();
        if ( newTotal<=5){
            // if none of the other loans are past due then add the new loan
            if(hasOverDueLoans()){
                throw new Exception("Can't Add New Loan When Student Has An Overdue Loan. Return Loan Before Starting A New One");
            }else{
                this.loans.add(loan);
                loan.setStudent(this);
                loan.setStudentName(this.name);
                
                
            }
        }else{
            throw new Exception("New Loan Could Not Be Taken Out By Student Due To Having Too Many Total Items. New Total Would Have Been: "+newTotal);
        }
    }

    private boolean hasOverDueLoans() {
        LocalDate currentDate = LocalDate.now();
        for(Loan loan : loans){
            if(currentDate.isAfter(loan.getDueDate())){
                return true;
            }
        }
        return false;
    }

    public int getTotalLoanedItems(){
        int total = 0 ;
        for (Loan loan : loans) {
            total += loan.getCopies().size();
        }
        return total;
    }

    //set all the copies within the loan as available
    public void returnLoan(Loan loan) {
        for(BookCopy copy : loan.getCopies()){
            copy.setAvailable(true) ;
        }
        this.loans.remove(loan);
    }


    public int getBroncoID() {
        return broncoID;
    }

    public void setBroncoID(int broncoID) {
        this.broncoID = broncoID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }
}
