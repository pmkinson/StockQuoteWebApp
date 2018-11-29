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
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConnectionException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseInitializationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.*;
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
     * @param reload          Reload the hibernate config file
     * @return String with top 5 searches formatted in HTML
     */
    public synchronized static ArrayList<String> queryDBForTopSearches(String hibernateConfig, int maxResults, int querySize, boolean reload) {
        ArrayList<String> sortedResults = new ArrayList<>();

        List list = new ArrayList();

        try {

            list = resultsHibernate(hibernateConfig, reload, querySize);

        } catch (Throwable e) {

            try {
                //Hibernate failed, try straight SQL query.
                list = resultsSQL(hibernateConfig, reload, querySize);
            } catch (Throwable exc) {
                sortedResults.add("Error");
            }
        }

        sortedResults = buildFrequency(list, maxResults);

        return sortedResults;
    }

    /**
     * Method to build a frequency count for items in a list.
     *
     * @param list
     * @param maxResults
     * @return
     */
    private static ArrayList<String> buildFrequency(List list, int maxResults) {
        ArrayList<String> sortedResults = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap<>();


        //Loop through the queried results
        for (int i = 0; i < list.size(); i++) {
            String stock = (String) list.get(i);

            //Loop over the k,v pairs building a frequency for stock symbols from the queried results.
            for (int b = 0; b < list.size(); b++) {
                if (list.get(b) == stock) {
                    //See if map contains the stock.  If it doesn't add it with an initial value of 1.
                    if (!map.containsKey(stock)) {
                        map.put(stock, 1);
                    } else {
                        //Increment the rolling count for the key.
                        int value = map.get(stock);
                        map.put(stock, ++value);
                    }
                }
            }
        }

        //Sort list based on frequency.
        sortedResults = sortByKey(map, maxResults);

        return sortedResults;
    }

    /**
     * Method to retrieve top results from database using traditional SQL.
     *
     * @param hibernateConfig Hibernate config file to load
     * @param reload          Reload the hibernate config file before making a connection
     * @param querySize       Total number of rows to select.
     * @return
     * @throws SQLException
     * @throws DatabaseConfigurationException
     * @throws DatabaseConnectionException
     */
    private static List<String> resultsSQL(String hibernateConfig, Boolean reload, int querySize)
            throws SQLException, DatabaseConfigurationException, DatabaseConnectionException {

        Connection connection = DatabaseUtils.getConnection(hibernateConfig, reload);

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT stock_symbol FROM searches LIMIT " + querySize + ";");

                    /* "SELECT stock_symbol FROM stockquote.stocks " +
                    "GROUP BY 1 " +
                    "ORDER BY count(*) DESC " +
                    "LIMIT 5;");
                    */

        List<String> list = new ArrayList();

        while (resultSet.next()) {
            list.add(resultSet.getString("stock_symbol"));
        }

        return list;
    }

    /**
     * Method to retrieve top results from database using Hibernate.
     *
     * @param hibernateConfig
     * @param reload
     * @param querySize
     * @return
     * @throws DatabaseInitializationException
     */
    private static List<String> resultsHibernate(String hibernateConfig, Boolean reload, int querySize) throws DatabaseInitializationException {

        Query query;
        Session session;

        //Get a session and begin a transaction
        session = DatabaseUtils.getSessionFactory(hibernateConfig, reload).openSession();

        //Retrieve 100 recent queries
        query = session.createQuery("SELECT searches.stockSymbol FROM SearchDAO searches");
        query.setMaxResults(querySize);


        List list = query.list();

        return list;
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
