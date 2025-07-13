package loan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import book.Book;
import bookcopy.*;
import main.HibernateUtil;
import student.Student;

public class CRUDLoan {
	
	// to create a loan, you need student and a list of book copies. 
	// copies can't exceed 5 
	// student total item's plus loans can't exceed 5 
	// student can't loan in the first place if student already has overdue loans
	
	public static Loan createLoan(Student student , List<BookCopyView> copies ) {
	    Session session = HibernateUtil.getSessionFactory().openSession();
	    Loan loan = new Loan(student);
	    
	    // change bookCopyView into book copys 
	    try {
	        session.beginTransaction();

	        for (BookCopyView c : copies) {
	            BookCopy managedCopy = session.get(BookCopy.class, c.getBarcode());
	            loan.addCopy(managedCopy);  // sets loan + available
	        }

	        student = session.get(Student.class, student.getBroncoID()); // attach student to session
	        student.addLoan(loan);

	        session.saveOrUpdate(student); // cascades Loan + updates
	        session.getTransaction().commit();
	        return loan;

	    } catch (Exception e) {
	        e.printStackTrace();
	        session.getTransaction().rollback();
	        return null;

	    } finally {
	        session.close();
	    }
	}
	
	public static List<Loan> getAllLoans(){
		Session session = HibernateUtil.getSessionFactory().openSession();
	    List<Loan> loans = new ArrayList<>();

	    try {
	        session.beginTransaction();
	        loans = session.createQuery(
	            "SELECT DISTINCT l FROM Loan l LEFT JOIN FETCH l.copies ORDER BY l.borrowingDate DESC", Loan.class
	        ).getResultList();
	        session.getTransaction().commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	        session.getTransaction().rollback();
	    } finally {
	        session.close();
	    }

	    return loans;
	}
	
	public static List<Loan> getStudentLoans(int broncoID){
		Session session = HibernateUtil.getSessionFactory().openSession();
	    List<Loan> loans = new ArrayList<>();

	    try {
	        session.beginTransaction();
	        loans = session.createQuery(
	            "SELECT DISTINCT l FROM Loan l LEFT JOIN FETCH l.copies WHERE l.student.broncoID="+broncoID+" ORDER BY l.borrowingDate DESC", Loan.class
	        ).getResultList();
	        session.getTransaction().commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	        session.getTransaction().rollback();
	    } finally {
	        session.close();
	    }

	    return loans;
	}
	

}
