package main;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import loan.Loan;
import student.Student;
import book.Book;
import bookcopy.BookCopy;
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Student.class)
                    .addAnnotatedClass(Loan.class)
                    .addAnnotatedClass(Book.class)
                    .addAnnotatedClass(BookCopy.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}