package book;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;

import bookcopy.*;
import bookcopy.CRUDBookCopy;
import main.HibernateUtil;

public class CRUDBook{
	// saves in the db 
	// create with number of copies ,
	
	public static Book createBook(String title, String ISBN, String description, int numPages, String publisher, LocalDate publicationDate, int numCopies) {
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		Book book =null; 
		try {
			session.beginTransaction(); 
			book =new Book(title, ISBN, description, numPages,  publisher, publicationDate); 
			session.save(book); 
			session.getTransaction().commit(); 
		}catch(Exception e ) {
			e.printStackTrace();
		}
		
		// now create all the copies 
		if (book!=null)
			addCopies(numCopies, book); 
		return book ; 
	}
	public static void addCopies(int numCopies, Book book) {
		
		for (int i =0; i<numCopies ; i++) {
			CRUDBookCopy.createBookCopy("CPP Library", true, book); 
		}
	}
	
	public static Book getBook(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		Book  book =null; 
		try {
			session.beginTransaction(); 
			book  = session.get(Book.class, id ); 
			session.getTransaction().commit(); 
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return book  ;
	}
	
	// this returns a list of copies with loan info and due dates 
	
	public static List<BookCopyView> searchForBookCopies(String titleSearch) {
	    Session session = HibernateUtil.getSessionFactory().openSession();

	    List<BookCopyView> bookCopies = session.createQuery(
	            "SELECT new bookcopy.BookCopyView(" +
	            "cp.barcode, cp.available, cp.location, " +
	            "loan.loanNumber, book.id, book.title, loan.studentName, " +
	            "loan.borrowingDate, loan.dueDate) " +
	            "FROM BookCopy cp " +
	            "LEFT JOIN cp.loan loan " +
	            "LEFT JOIN cp.book book " +
	            "WHERE LOWER(book.title) LIKE LOWER(:title)", BookCopyView.class)
	            .setParameter("title", "%" + titleSearch + "%")
	            .getResultList();

	    session.close();
	    return bookCopies;
	}
	
	public static List<BookCopyView> searchForBookCopies() {
	    Session session = HibernateUtil.getSessionFactory().openSession();

	    List<BookCopyView> bookCopies = session.createQuery(
	            "SELECT new bookcopy.BookCopyView(" +
	            "cp.barcode, cp.available, cp.location, " +
	            "loan.loanNumber, book.id, book.title, loan.studentName, " +
	            "loan.borrowingDate, loan.dueDate) " +
	            "FROM BookCopy cp " +
	            "LEFT JOIN cp.loan loan " +
	            "LEFT JOIN cp.book book " )
	            .getResultList();

	    session.close();
	    return bookCopies;
	}
	public static Book updateBook(int id, Book newBook) {
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		Book book =null; 
		try {
			session.beginTransaction(); 
			book = session.get(Book.class, id);
			
			if(book != null) {
				book.setDescription(newBook.getDescription().isEmpty() ? book.getDescription() : newBook.getDescription());

				book.setISBN(newBook.getISBN().isEmpty() ? book.getISBN() : newBook.getISBN());
				book.setTitle(newBook.getTitle().isEmpty() ? book.getTitle() : newBook.getTitle());
				book.setPublisher(newBook.getPublisher().isEmpty() ? book.getPublisher() : newBook.getPublisher());

				book.setPublicationDate(newBook.getPublicationDate() == null ? book.getPublicationDate() : newBook.getPublicationDate());
				book.setNumPages(newBook.getNumPages() == 0 ? book.getNumPages() : newBook.getNumPages());
			}
			
			session.save(book); 
			session.getTransaction().commit(); 
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return book ;
	}
	
	// Can only delete book only if all copies aren't loaned out 
	public static void deleteBook(int id) {
		
		// otherwise you can delete the book which will in turn delete all the copies due to cascade 
		Session session = HibernateUtil.getSessionFactory().openSession(); 
	 
		try {
			Book book =session.get(Book.class, id);; 
			
			// find a book copy with that id that has a loan 
			for (BookCopy copy: book.getCopies()) {
				if (!copy.isAvailable() || copy.getLoan() != null)
				{
					System.out.println("Book can't be deleted if one of it's copies is still loaned out.\n Apart of loan number: "+copy.getLoan());
					return; 
				}
			}
			session.beginTransaction(); 
			session.delete(book); 
			session.getTransaction().commit(); 
		}catch(Exception e ) {
			System.out.println("Book Doesn't Exist ");
		}
		
	}
	
}
