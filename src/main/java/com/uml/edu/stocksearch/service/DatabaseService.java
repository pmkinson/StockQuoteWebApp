/**
 * Copyright 2018 Peter Kinson
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 */

package com.uml.edu.stocksearch.service;

import com.uml.edu.stocksearch.model.DAOObject;
import com.uml.edu.stocksearch.service.exceptions.DatabaseServiceException;
import com.uml.edu.stocksearch.utilities.database.DatabaseUtils;
import com.uml.edu.stocksearch.utilities.database.exceptions.DatabaseInitializationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DatabaseService {

    protected DatabaseService() {

    }

    /**
     * Method to add or update object on the database.
     *
     * @param dao
     */
    synchronized public void commitObject(DAOObject dao) {

        Session session;
        Transaction transaction = null;

            try {
                session = DatabaseUtils.getSessionFactory().openSession();
                transaction = session.beginTransaction();

                session.saveOrUpdate(dao);
                transaction.commit();

            } catch (HibernateException e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();  // close transaction, don't forget
                    new DatabaseServiceException("Could not add SearchDAO data. " + e.getMessage(), e);
                }
            } catch (DatabaseInitializationException e) {
                new DatabaseServiceException("Could not retrieve an active Session from the session-factory. ", e.getCause());
            } finally {
                if (transaction != null && transaction.isActive()) {
                    transaction.commit();
                }
            }
    }
}
