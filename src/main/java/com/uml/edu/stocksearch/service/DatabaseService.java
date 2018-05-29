package com.uml.edu.stocksearch.service;

import com.uml.edu.stocksearch.model.SearchDAO;
import com.uml.edu.stocksearch.service.exceptions.DatabaseServiceException;
import com.uml.edu.stocksearch.utilities.database.DatabaseUtils;
import com.uml.edu.stocksearch.utilities.database.exceptions.DatabaseInitializationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Timestamp;

public class DatabaseService {

    protected DatabaseService() {

    }

    public void addStockQueryMetaData(SearchDAO searchDAO) {

        //Query client system for os / browser

        Session session;
        Transaction transaction = null;

        synchronized (SearchDAO.class) {

            try {
                session = DatabaseUtils.getSessionFactory().openSession();
                transaction = session.beginTransaction();

                session.saveOrUpdate(searchDAO);
                transaction.commit();

            } catch (HibernateException e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();  // close transaction, don't forget
                }
                new DatabaseServiceException("Could not add SearchDAO data. " + e.getMessage(), e);
            } catch (DatabaseInitializationException e) {
                new DatabaseServiceException("Could not retrieve an active Session from the session-factory. ", e.getCause());
            } finally {
                if (transaction != null && transaction.isActive()) {
                    transaction.commit();
                }
            }
        }
    }
}
