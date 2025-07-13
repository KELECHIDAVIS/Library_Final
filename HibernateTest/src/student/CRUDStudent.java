package student;

import org.hibernate.Session;

import main.HibernateUtil;

public class CRUDStudent {
	
	public static  Student getStudent(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
        Student student = null;

        try {
            session.beginTransaction();
            student = session.get(Student.class, id);
            session.getTransaction().commit();

            if (student == null) {
                throw new Exception("No student found with ID: " + id);
            }

        } catch (Exception e) {
            session.getTransaction().rollback();
            return null; 
        } finally {
            session.close();
        }

        return student;
	}
}
