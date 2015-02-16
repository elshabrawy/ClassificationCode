package Util;


import java.io.FileInputStream;
import java.util.Properties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author Mohamed_Elshabrawy
 */
public class DataBaseConnection {
    static String userName="";
    static String Pass="";
    static String server="";
    static String schema="";
    private static final SessionFactory sessionFactory;
    private static Session session;
    static String Driver="";
    static String url="";

    static {

        try {
            final Configuration config = new Configuration();
            sessionFactory = config.configure("Util/hibernate.cfg.xml").buildSessionFactory();
            System.out.println("connected");
        } catch (final Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
        if ( session == null || !session.isOpen()  ) {
            session = sessionFactory.openSession();
            return session;
        } else {
            return session;
        }

    }
    public static void main(String[] args) {
        new DataBaseConnection();
    }
   
}
