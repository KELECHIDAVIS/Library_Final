package main;
import student.*;

import java.time.LocalDate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import book.*; 
import bookcopy.*; 
import loan.*; 

public class DBTest {
	public static void main(String[] args) {
		
		SessionFactory factory = new Configuration().configure("hibernate.cfg.xml")
        .addAnnotatedClass(Student.class)
        .addAnnotatedClass(Book.class)
        .addAnnotatedClass(Loan.class)
        .addAnnotatedClass(BookCopy.class)
        .buildSessionFactory();
		
		Session session = factory.getCurrentSession(); 
		
		Student student = new Student("kelechi", "pomona", "cs"); 
		student.setBroncoID(14);
		Book book = new Book("great gatsby", "56", "description", 560, "publisher", LocalDate.now()); 
		
		Loan loan = new Loan(student); 
		
		BookCopy copy = new BookCopy("updateTestCopy", true, book, loan);
		
		session.beginTransaction();
		
		session.save(student); 
		session.save(book); 
		session.save(loan); 
		session.save(copy); 
		
		session.getTransaction().commit();
		
		
			
	}
}
