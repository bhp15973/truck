package org.hibernate.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import eti.bartek.sqlite.model.Truck;
import eti.bartek.sqlite.model.CKIN;
import eti.bartek.sqlite.model.RoutePath;
import eti.bartek.sqlite.model.Routes;
import eti.bartek.sqlite.model.Gate;

public class HibernateController {
    
    public static final String FIND_PATH_QUERY = "WITH RECURSIVE transitive_closure(TruckID, destination, pathString) AS " +
                                                    "(SELECT TruckID, destination, TruckID || '.' || destination || '.' AS pathString " +
                                                    "FROM Routes WHERE TruckID = :from " + 
                                                    "UNION ALL " + 
                                                    "SELECT tc.TruckID, f.destination, tc.pathString || f.destination || '.' AS pathString "+
                                                    "FROM Routes AS f " + 
                                                    "JOIN transitive_closure AS tc ON f.TruckID = tc.destination " +
                                                    "WHERE tc.pathString NOT LIKE '%' || f.destination || '.%' ) " +
                                                    "SELECT DISTINCT * FROM transitive_closure WHERE destination = :to " +
                                                    "ORDER BY TruckID, destination";

	private static SessionFactory sessionFactory = null;
	private static ServiceRegistry serviceRegistry = null;
	
	public static void init() {
		addDataToBase();
	}
	
	private static SessionFactory configureSessionFactory() throws HibernateException {
		Configuration configuration = new Configuration();
		configuration.configure();
		
		configuration.addAnnotatedClass(Truck.class);
		configuration.addAnnotatedClass(Routes.class);
		configuration.addAnnotatedClass(Gate.class);
		configuration.addAnnotatedClass(CKIN.class);
		
		Properties properties = configuration.getProperties();
		
		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(properties).build();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		return sessionFactory;
	}
	private static void addDataToBase() {
    	configureSessionFactory();
    	
    	Session session = null;
    	Transaction tx = null;
    	
    	try {
    		session = sessionFactory.openSession();
    		tx = session.beginTransaction();
    		
    		//Magazyny
    		Truck gdansk = new Truck(1, "Gdansk", "EPGD", "GDN", "54.3762", "18.4718");
    		Truck wawa = new Truck(2, "Warszawa", "EPWA", "WAW", "52.16656", "20.98735");
    		Truck Krakyn = new Truck(3, "Krakow", "EGLL", "LHR", "50.064316", "19.972592");
    		Truck Pozn = new Truck(4, "Poznan", "KJFK", "JFK", "52.414688", "16.920199");
    		Truck Szczney = new Truck(5, "Szczecin", "YSSY", "Szcz", "53.437387", "14.549774");
    		Truck Rzeszin = new Truck(6, "Rzeszow", "ZBAA", "Rzesz", "50.037173", "22.003216");
    		
    		//Trasy
    		Routes gdToWawa1 = new Routes(1, "Gd->Wawa", 1, 2, new Date(2016, 6, 1, 10, 20), 50, 50, 300, 1.0);
    		Routes gdToWawa2 = new Routes(2, "Gd->Wawa", 1, 2, new Date(2016, 6, 1, 15, 30), 50, 50, 300, 1.0);
    		Routes gdToKrak1 = new Routes(3, "Gd->Krak", 1, 3, new Date(2016, 6, 1, 8, 50), 120, 200, 1306, 2.3);
    		Routes gdToKrak2 = new Routes(4, "Gd->Krak", 1, 3, new Date(2016, 6, 1, 16, 20), 120, 200, 1306, 2.3);
    		Routes wawaToRzesz1 = new Routes(5, "Wawa->Rzesz", 2, 6, new Date(2016, 6, 1, 4, 20), 100, 800, 7000, 8.0);
    		Routes wawaToRzesz2 = new Routes(6, "Wawa->Rzesz", 2, 6, new Date(2016, 6, 1, 20, 40), 100, 800, 7000, 8.0);
    		Routes RzeszToSzcz = new Routes(7, "Rzesz->Szczn", 6, 5, new Date(2016, 6, 1, 11, 06), 150, 750, 8926, 12.0);
    		Routes SzczToPozn = new Routes(8, "Szcz->Pozn", 5, 4, new Date(2016, 6, 1, 14, 15), 150, 1200, 15960, 20.2);
    		Routes RzeszToPozn = new Routes(9, "Rzesz->Pozn", 6, 4, new Date(2016, 6, 1, 4, 6), 150, 1000, 10962, 13.33);
    		Routes KrakToPozn1 = new Routes(10, "Krak->Pozn", 3, 4, new Date(2016, 6, 1, 5, 0), 150, 600, 5535, 7.8);
    		Routes KrakToPozn2 = new Routes(11, "Krak->Pozn", 3, 4, new Date(2016, 6, 1, 20, 55), 150, 600, 5535, 7.9);
    		Routes PoznToKrak = new Routes(12, "Pozn->Krak", 4, 3, new Date(2016, 6, 1, 7, 0), 150, 600, 5535, 8.0);
    		Routes PoznToRzesz = new Routes(13, "Pozn->Rzesz", 4, 6, new Date(2016, 6, 1, 8, 0), 150, 1000, 10962, 13.5);
    		Routes RzeszToWawa = new Routes(14, "Rzesz->Wawa", 6, 2, new Date(2016, 6, 1, 2, 0), 150, 800, 7000, 8.0);
    		Routes SzczToRzesz = new Routes(15, "Szcz->Rzesz", 5, 6, new Date(2016, 6, 1, 23, 0), 150, 750, 8926, 12.0);
    		Routes KrakToWawa = new Routes(16, "Krak->Wawa", 3, 2, new Date(2016, 6, 1, 20, 0), 150, 200, 1500, 2.75);
    		
    		//bramki
    		Gate gateF1 = new Gate(1, "Gd->Wawa", 2, new Date(2016, 6, 1, 9, 0), new Date(2016, 6, 1, 10, 0), 1);
    		Gate gateF2 = new Gate(2, "Gd->Wawa", 2, new Date(2016, 6, 1, 14, 35), new Date(2016, 6, 1, 15, 10), 1);
    		Gate gateF3 = new Gate(3, "Gd->Krak", 3, new Date(2016, 6, 1, 7, 55), new Date(2016, 6, 1, 8, 40), 1);
    		Gate gateF4 = new Gate(4, "Gd->Krak", 3, new Date(2016, 6, 1, 15, 35), new Date(2016, 6, 1, 16, 10), 1);
    		Gate gateF5 = new Gate(5, "Wawa->Rzesz", 6, new Date(2016, 6, 1, 3, 35), new Date(2016, 6, 1, 4, 10), 1);
    		Gate gateF6 = new Gate(6, "Wawa->Rzesz", 6, new Date(2016, 6, 1, 19, 35), new Date(2016, 6, 1, 20, 25), 1);
    		Gate gateF7 = new Gate(7, "Rzesz->Szczn", 5, new Date(2016, 6, 1, 10, 10), new Date(2016, 6, 1, 10, 57), 1);
    		Gate gateF8 = new Gate(8, "Szcz->Pozn", 4, new Date(2016, 6, 1, 13, 10), new Date(2016, 6, 1, 14, 0), 1);
    		Gate gateF9 = new Gate(9, "Rzesz->Pozn", 4, new Date(2016, 6, 1, 3, 0), new Date(2016, 6, 1, 3, 55), 1);
    		Gate gateF10 = new Gate(10, "Krak->Pozn", 4, new Date(2016, 6, 1, 4, 10), new Date(2016, 6, 1, 4, 50), 1);
    		Gate gateF11 = new Gate(11, "Krak->Pozn", 4, new Date(2016, 6, 1, 19, 50), new Date(2016, 6, 1, 20, 40), 1);
    		Gate gateF12 = new Gate(12, "Pozn->Krak", 3, new Date(2016, 6, 1, 6, 0), new Date(2016, 6, 1, 6, 50), 1);
    		Gate gateF13 = new Gate(13, "Pozn->Rzesz", 6, new Date(2016, 6, 1, 7, 0), new Date(2016, 6, 1, 7, 50), 1);
    		Gate gateF14 = new Gate(14, "Rzesz->Wawa", 2, new Date(2016, 6, 1, 1, 0), new Date(2016, 6, 1, 1, 50), 1);
    		Gate gateF15 = new Gate(15, "Szcz->Rzesz", 6, new Date(2016, 6, 1, 22, 0), new Date(2016, 6, 1, 22, 50), 1);
    		Gate gateF16 = new Gate(16, "Krak->Wawa", 2, new Date(2016, 6, 1, 19, 0), new Date(2016, 6, 1, 19, 50), 1);
    		
    		//kontrola baga�u
    		CKIN ckinF1 = new CKIN(1, "Gd->Wawa", 2, new Date(2016, 6, 1, 9, 0), new Date(2016, 6, 1, 10, 0), 20, 1);
    		CKIN ckinF2 = new CKIN(2, "Gd->Wawa", 2, new Date(2016, 6, 1, 14, 35), new Date(2016, 6, 1, 15, 10), 20, 1);
    		CKIN ckinF3 = new CKIN(3, "Gd->Krak", 3, new Date(2016, 6, 1, 7, 55), new Date(2016, 6, 1, 8, 40), 20, 1);
    		CKIN ckinF4 = new CKIN(4, "Gd->Krak", 3, new Date(2016, 6, 1, 15, 35), new Date(2016, 6, 1, 16, 10), 20, 1);
    		CKIN ckinF5 = new CKIN(5, "Wawa->Rzesz", 6, new Date(2016, 6, 1, 3, 35), new Date(2016, 6, 1, 4, 10), 20, 1);
    		CKIN ckinF6 = new CKIN(6, "Wawa->Rzesz", 6, new Date(2016, 6, 1, 19, 35), new Date(2016, 6, 1, 20, 25), 25, 1);
    		CKIN ckinF7 = new CKIN(7, "Rzesz->Szczn", 5, new Date(2016, 6, 1, 10, 10), new Date(2016, 6, 1, 10, 57), 30, 1);
    		CKIN ckinF8 = new CKIN(8, "Szcz->Pozn", 4, new Date(2016, 6, 1, 13, 10), new Date(2016, 6, 1, 14, 0), 30, 1);
    		CKIN ckinF9 = new CKIN(9, "Rzesz->Pozn", 4, new Date(2016, 6, 1, 3, 0), new Date(2016, 6, 1, 3, 55), 15, 1);
    		CKIN ckinF10 = new CKIN(10, "Krak->Pozn", 4, new Date(2016, 6, 1, 4, 10), new Date(2016, 6, 1, 4, 50), 10, 1);
    		CKIN ckinF11 = new CKIN(11, "Krak->Pozn", 4, new Date(2016, 6, 1, 19, 50), new Date(2016, 6, 1, 20, 40), 20, 1);
    		CKIN ckinF12 = new CKIN(12, "Pozn->Krak", 3, new Date(2016, 6, 1, 6, 0), new Date(2016, 6, 1, 6, 50), 15, 1);
    		CKIN ckinF13 = new CKIN(13, "Pozn->Rzesz", 6, new Date(2016, 6, 1, 7, 0), new Date(2016, 6, 1, 7, 50), 20, 1);
    		CKIN ckinF14 = new CKIN(14, "Rzesz->Wawa", 2, new Date(2016, 6, 1, 1, 0), new Date(2016, 6, 1, 1, 50), 15, 1);
    		CKIN ckinF15 = new CKIN(15, "Szcz->Rzesz", 6, new Date(2016, 6, 1, 22, 0), new Date(2016, 6, 1, 22, 50), 15, 1);
    		CKIN ckinF16 = new CKIN(16, "Krak->Wawa", 6, new Date(2016, 6, 1, 19, 0), new Date(2016, 6, 1, 19, 50), 15, 1);
    		
    		//zapis
    		session.saveOrUpdate(gdansk);
    		session.saveOrUpdate(Krakyn);
    		session.saveOrUpdate(wawa);
    		session.saveOrUpdate(Pozn);
    		session.saveOrUpdate(Rzeszin);
    		session.saveOrUpdate(Szczney);
    		
    		session.saveOrUpdate(gdToWawa1);
    		session.saveOrUpdate(gdToWawa2);
    		session.saveOrUpdate(gdToKrak1);
    		session.saveOrUpdate(gdToKrak2);
    		session.saveOrUpdate(wawaToRzesz1);
    		session.saveOrUpdate(wawaToRzesz2);
    		session.saveOrUpdate(KrakToPozn1);
    		session.saveOrUpdate(KrakToPozn2);
    		session.saveOrUpdate(PoznToKrak);
    		session.saveOrUpdate(PoznToRzesz);
    		session.saveOrUpdate(RzeszToPozn);
    		session.saveOrUpdate(RzeszToSzcz);
    		session.saveOrUpdate(RzeszToWawa);
    		session.saveOrUpdate(SzczToPozn);
    		session.saveOrUpdate(SzczToRzesz);
    		session.saveOrUpdate(KrakToWawa);
    		
    		session.saveOrUpdate(gateF1);
    		session.saveOrUpdate(gateF2);
    		session.saveOrUpdate(gateF3);
    		session.saveOrUpdate(gateF4);
    		session.saveOrUpdate(gateF5);
    		session.saveOrUpdate(gateF6);
    		session.saveOrUpdate(gateF7);
    		session.saveOrUpdate(gateF8);
    		session.saveOrUpdate(gateF9);
    		session.saveOrUpdate(gateF10);
    		session.saveOrUpdate(gateF11);
    		session.saveOrUpdate(gateF12);
    		session.saveOrUpdate(gateF13);
    		session.saveOrUpdate(gateF14);
    		session.saveOrUpdate(gateF15);
    		session.saveOrUpdate(gateF16);
    		
    		session.saveOrUpdate(ckinF1);
    		session.saveOrUpdate(ckinF2);
    		session.saveOrUpdate(ckinF3);
    		session.saveOrUpdate(ckinF4);
    		session.saveOrUpdate(ckinF5);
    		session.saveOrUpdate(ckinF6);
    		session.saveOrUpdate(ckinF7);
    		session.saveOrUpdate(ckinF8);
    		session.saveOrUpdate(ckinF9);
    		session.saveOrUpdate(ckinF10);
    		session.saveOrUpdate(ckinF11);
    		session.saveOrUpdate(ckinF12);
    		session.saveOrUpdate(ckinF13);
    		session.saveOrUpdate(ckinF14);
    		session.saveOrUpdate(ckinF15);
    		session.saveOrUpdate(ckinF16);
    		
    		session.flush();
    		tx.commit();
    	} catch(Exception e) {
    		e.printStackTrace();
    		tx.rollback();
    	} finally {
    		if(session != null) {
    			session.close();
    		}
    	}
    }
	
	public static <T> List<T> getDataList(String from, String where) {
		Session session = null;
		List<T> data = new ArrayList<T>();
		try {
			session = sessionFactory.openSession();
			data = session.createQuery("from " + from + 
					(where.isEmpty() ? "" : " where " + where)).list();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(session != null) {
				session.close();
			}
		}
		return data;
	}
	
	public static <T> T getSingleElement(String from, String where) {
	    Session session = null;
		Object data = null;
		try {
			session = sessionFactory.openSession();
			data = session.createQuery("from " + from + 
					(where.isEmpty() ? "" : " where " + where)).uniqueResult();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(session != null) {
				session.close();
			}
		}
		return (T)data;
	}
	
	public static List<RoutePath> getRoutePath(Integer from, Integer to) {
	    Session session = null;
	    List<RoutePath> paths = new ArrayList<>();
	    try {
	        session = sessionFactory.openSession();
	        Query query = session.createSQLQuery(FIND_PATH_QUERY)
	           .addScalar("TruckID", new IntegerType())
	           .addScalar("destination", new IntegerType())
	           .addScalar("pathString", new StringType())
	           .setResultTransformer(Transformers.aliasToBean(RoutePath.class));
	           query.setParameter("from", from);
	           query.setParameter("to", to);

	           paths = query.list();
	    } catch(Exception e) {
	        e.printStackTrace();
	    } finally {
	        if(session != null) {
	            session.close();
	        }
	    }
	    return paths;
	}
	
}
