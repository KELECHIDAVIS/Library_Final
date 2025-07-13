package bookcopy;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import book.Book;
import main.HibernateUtil;

public class CRUDBookCopy {
	// saves in the db 
	// loan starts out null 
	public static BookCopy createBookCopy(String location , boolean isAvailable , Book book) {
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		BookCopy copy =null; 
		try {
			session.beginTransaction(); 
			copy =new BookCopy(location , isAvailable, book , null); 
			session.save(copy); 
			session.getTransaction().commit(); 
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return copy ; 
	}
	public static List<BookCopyView> getAvailableCopies() {
	    Session session = HibernateUtil.getSessionFactory().openSession();
	    

	    List<BookCopyView> bookCopies = session.createQuery(
	            "SELECT new bookcopy.BookCopyView(" +
	            "cp.barcode, cp.available, cp.location, " +
	            "loan.loanNumber, book.id, book.title, loan.studentName, " +
	            "loan.borrowingDate, loan.dueDate) " +
	            "FROM BookCopy cp " +
	            "LEFT JOIN cp.loan loan " +
	            "LEFT JOIN cp.book book " +
	            "WHERE cp.available= true"
	            )
	            .getResultList();

	    session.close();

	    return bookCopies;
	}
	public static BookCopy getBookCopy(int barcode) {
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		BookCopy copy =null; 
		try {
			session.beginTransaction(); 
			copy = session.get(BookCopy.class, barcode); 
			session.getTransaction().commit(); 
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return copy ;
	}
	
	public static BookCopy updateBookCopy (int barcode, BookCopy newCopy) {
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		BookCopy copy =null; 
		try {
			session.beginTransaction(); 
			copy = session.get(BookCopy.class, barcode);
			
			if(copy != null) {
				copy.setAvailable(newCopy.isAvailable());
				copy.setBook(newCopy.getBook());
				copy.setLoan(newCopy.getLoan());
				copy.setLocation(newCopy.getLocation());
			}
			session.save(copy); 
			session.getTransaction().commit(); 
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return copy ;
	}
	
	// check loan dependency before deletion 
	// if the copy is loaned out it shouldn't be able to be deleted 
	public static void deleteCopy(int barcode) {
		String query = "DELETE FROM book_copy"
				+ "WHERE loan_num IS NULL "
				+ "AND barcode = "+barcode; 
	
		BookCopy copy = getBookCopy(barcode);  // first get the copy
		
		if (copy == null) {
			System.out.print("Book Copy Under That Barcode Doesn't Exist");
			return; 
		}
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		
		// delete copy if it does 
		try {
			session.beginTransaction(); 
			session.delete(copy);
			session.getTransaction().commit(); 
		}catch(Exception e ) {
			e.printStackTrace();
		}
		
	}
}
