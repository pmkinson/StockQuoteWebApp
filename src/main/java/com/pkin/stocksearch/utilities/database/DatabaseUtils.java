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

package com.pkin.stocksearch.utilities.database;

import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConnectionException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseInitializationException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.*;

/**
 * A class that contains database related utility methods.
 */

public class DatabaseUtils {

    private static Configuration configuration;
    private static SessionFactory sessionFactory;

    /**
     * Utility method to return a connection to the database
     *
     * @param hibernateConfigFile The name of the hibernate config file to load for the connection.
     * @param reloadConfigFile
     * @return <CODE>Connection</CODE> connection
     * @throws DatabaseConnectionException    Thrown when a connection cannot be established to DB
     * @throws DatabaseConfigurationException Thrown when the hibernate configuration file cannot be loaded.
     */
    public static synchronized Connection getConnection(String hibernateConfigFile, boolean reloadConfigFile) throws DatabaseConnectionException, DatabaseConfigurationException {

        HibernateUtils.verifyHibernateConfig(hibernateConfigFile);
        Connection connection;
        configuration = getConfiguration(hibernateConfigFile, reloadConfigFile);
        try {

            //Class.forName(HibernateUtils.getDriver(hibernateConfigFile));

            String databaseUrl = configuration.getProperty("hibernate.connection.url");
            String username = configuration.getProperty("hibernate.connection.username");
            String password = configuration.getProperty("hibernate.connection.password");

            connection = DriverManager.getConnection(databaseUrl, username, password);

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Could not open a connection to database." + e.getMessage(), e);
        }
        return connection;
    }


    /**
     * A Session Factory
     *
     * @return SessionFactory for use with database transactions
     * @throws DatabaseInitializationException Thrown when there is an issue returning a concrete session
     */
    synchronized public static SessionFactory getSessionFactory(String hibernateConfigFile, boolean newSession) throws DatabaseInitializationException {

        // singleton pattern
        try {
            if (sessionFactory == null || newSession == true) {
                HibernateUtils.verifyHibernateConfig(hibernateConfigFile);
                sessionFactory = buildSessionFactory(hibernateConfigFile);
            } else {
                return sessionFactory;
            }
        } catch (Throwable e) {
            throw new DatabaseInitializationException("Could not return a concrete session factory because of: "
                    + e.getMessage(), e);
        }
        return sessionFactory;
    }


    /**
     * A SessionFactory instance generator.
     *
     * @return <CODE>SessionFactory</CODE> sessionFactory
     */

    private static SessionFactory buildSessionFactory(String hibernateConfigFile) {

        try {

            // Create the ServiceRegistry from hibernate config file
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .configure(hibernateConfigFile).build();

            // Create a metadata sources using the specified service registry.
            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();

            return metadata.getSessionFactoryBuilder().build();

        } catch (ExceptionInInitializerError e) {
            throw new ExceptionInInitializerError("SessionFactory creation failed. " + e.getMessage());
        }
    }


    /**
     * Create a new or return an existing database configuration object.
     *
     * @return A hibernate configuration
     * @throws DatabaseConfigurationException
     */
    private static Configuration getConfiguration(String filePath, boolean reloadConfigFile) throws DatabaseConfigurationException {

        //Singleton pattern
        try {
            File file = HibernateUtils.getFile(filePath);

            if (configuration == null || reloadConfigFile == true) {
                configuration = new Configuration();
                configuration.configure(file);
                configuration.getProperties();
            } else {
                return configuration;
            }

        } catch (URISyntaxException e) {
            throw new DatabaseConfigurationException("Failed to load the URI for " + filePath
                    + " configuration file from the classpath.", e);
        } catch (Throwable e) {
            throw new DatabaseConfigurationException("Couldn't load hibernate.cfg.cml configuration file because of: "
                    + e.getMessage(), e);
        }

        return configuration;
    }


}

