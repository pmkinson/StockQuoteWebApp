package com.pkin.stocksearch.utilities.database;

import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.HibernateUtilitiesException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class HibernateUtilsTest {

    SupportMethods config = new SupportMethods();

    final private String url = "postgres://yyntvlsruodewk:824bc9538b51522e5fe41537b41b611998efc8e7da23422dd5b0edf86370a69a@ec2-174-129-206-173.compute-1.amazonaws.com:5432/dee5uoi05ai36v";
    final private String badURL = "postgres//yyntvls2dd5b0edf86370a69a@ec2-174-129-206-173.compute-1.amazonaws.com:5432/dee5uoi05ai36v";

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Before
    public void setUp() {
        SupportMethods config = new SupportMethods();

        // config.copyMainFile();
    }

    @After
    public void cleanUp() {
        SupportMethods config = new SupportMethods();

        // config.resetMainHibernateFile();
    }

    @Test(expected = DatabaseConfigurationException.class)
    public void verifyHibernateConfigError() throws DatabaseConfigurationException {
        setEnvironmentVariable(badURL);
        config.changeConfigFile("backend", "1", "hibernate-test.cfg.xml");

        HibernateUtils.verifyHibernateConfig("hibernate-test.cfg.xml");

    }

    @Test
    public void verifyHibernateConfig() throws DatabaseConfigurationException {
        setEnvironmentVariable(url);

        //Make sure var 0 doesn't throw error
        config.changeConfigFile("backend", "0", "hibernate-test.cfg.xml");
        HibernateUtils.verifyHibernateConfig("hibernate-test.cfg.xml");

        //Make sure var 1 doesn't throw error
        config.changeConfigFile("backend", "1", "hibernate-test.cfg.xml");
        HibernateUtils.verifyHibernateConfig("hibernate-test.cfg.xml");

        //Make sure var 2 doesn't throw error
        config.changeConfigFile("backend", "2", "hibernate-test.cfg.xml");
        HibernateUtils.verifyHibernateConfig("hibernate-test.cfg.xml");

        //Make sure default doesn't throw error
        config.changeConfigFile("backend", "2", "hibernate-test.cfg.xml");
        HibernateUtils.verifyHibernateConfig("hibernate-test.cfg.xml");
    }


    @Test
    public void getDriver() throws HibernateUtilitiesException {

        String result = HibernateUtils.getDriver("hibernate-test.cfg.xml");
        String expResult = "org.apache.derby.jdbc.EmbeddedDriver";

        assertEquals(result, expResult);
    }

    //Supporting Methods

    /********************************************************************************************/

    public void setEnvironmentVariable(String var) {
        environmentVariables.set("DATABASE_URL", var);
    }

    public void clearEnvironmentVariable() {
        if (environmentVariables != null) {
            environmentVariables.clear("DATABASE_URL");
        } else {
        }
    }
}