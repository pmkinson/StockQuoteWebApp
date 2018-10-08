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

package com.pkin.stocksearch.service;

import com.pkin.stocksearch.service.exceptions.DatabaseServiceException;
import com.pkin.stocksearch.utilities.database.DatabaseUtils;
import com.pkin.stocksearch.model.DAOObject;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseInitializationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {

    protected DatabaseService() {

    }

    /**
     * Method to retrieve the top searches from the database.
     *
     * @param hibernateConfig Hibernate config file to load
     * @param maxResults      Maximum size list of results to return
     * @param querySize       Number of results to return from database for frequency sorting.
     * @return String with top 5 searches formatted in HTML
     * @throws DatabaseServiceException
     */
    public static ArrayList<String> queryDBForTopSearches(String hibernateConfig, int maxResults, int querySize) throws DatabaseServiceException {
        ArrayList<String> sortedResults;

        Query query;
        Session session;

        try {
            //Get a session and begin a transaction
            session = DatabaseUtils.getSessionFactory(hibernateConfig, false).openSession();

            //Retrieve 100 recent queries
            query = session.createQuery("SELECT stock.stockSymbol FROM SearchDAO stock");
            query.setMaxResults(querySize);

            /*  SQL query.  Rest of the code below wouldn't be needed if
                HQL query fully matched the SQL in comments

                    "SELECT stock_symbol FROM stockquote.stocks\n" +
                    "GROUP BY 1\n" +
                    "ORDER BY count(*) DESC\n" +
                    "LIMIT 5";
                    */
            List list = query.list();

            HashMap<String, Integer> map = new HashMap<>();

            //Loop through the queried results
            for (int i = 0; i < list.size(); i++) {
                String stock = (String) list.get(i);

                //Loop over the k,v pairs building a frequency for stock symbols from the queried results.
                for (int b = 0; b < list.size(); b++) {
                    if (list.get(b) == stock) {
                        //See if map contains the stock.  If is doesn't add it with a value of 1.
                        if (!map.containsKey(stock)) {
                            map.put(stock, 1);
                        } else {
                            //Increment the value, aka the rolling count for the key.
                            int value = map.get(stock);
                            map.put(stock, ++value);
                        }
                    }
                }
            }
            //Sort list based on frequency.
            sortedResults = sortByKey(map, maxResults);

        } catch (DatabaseInitializationException e) {
            throw new DatabaseServiceException("An error occurred while connecting to the database", e.getCause());
        }

        return sortedResults;
    }

    /**
     * Method to sort a Map by the key
     *
     * @param map Map to sort
     * @return TreeMap sorted by value
     */
    private static ArrayList<String> sortByKey(Map map, int maxResults) {

        ArrayList<String> descendingList = new ArrayList<>();

        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        int listCount = 0;
        int listSize = 0;

        //See if list of results is smaller than the maxResults.
        if (list.size() > maxResults) {
            listCount = list.size() - (maxResults + 1);
            listSize = list.size() - 1;
        } else {
            listCount = -1;
            listSize = list.size() - 1;
        }

        for (int i = listSize; i > listCount; i--) {
            descendingList.add(list.get(i).getKey());
        }

        return descendingList;
    }

    /**
     * Method to add or update object on the database.
     *
     * @param dao                 DAO object to commit.
     * @param hibernateConfigFile Name of the hibernate config file.
     */
    synchronized public void commitObject(DAOObject dao, String hibernateConfigFile) {

        Session session;
        Transaction transaction = null;

        try {
            session = DatabaseUtils.getSessionFactory(hibernateConfigFile, false).openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(dao);
            transaction.commit();

        } catch (HibernateException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();  // close transaction, don't forget
                new DatabaseServiceException("Could not add SearchDAO data. " + e.getMessage(), e);
            }
        } catch (Throwable e) {
            new DatabaseServiceException("Could not retrieve an active Session from the session-factory. ", e.getCause());
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.commit();
            }
        }
    }
}
