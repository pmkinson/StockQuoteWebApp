package com.pkin.stocksearch.model;

import org.junit.*;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Fields;

import javax.sql.DataSource;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class SearchDAOTest {

    @Mock
    DataSource mockDataSource;
    @Mock
    Connection mockConn;
    @Mock
    PreparedStatement mockPreparedStmnt;
    @Mock
    ResultSet mockResultSet;
    int userId = 100;

    public SearchDAOTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws SQLException {
        when(mockDataSource.getConnection()).thenReturn(mockConn);
        when(mockDataSource.getConnection(anyString(), anyString())).thenReturn(mockConn);

        doNothing().when(mockConn).commit();
        when(mockConn.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStmnt);
        doNothing().when(mockPreparedStmnt).setString(anyInt(), anyString());
        when(mockPreparedStmnt.execute()).thenReturn(Boolean.TRUE);
        when(mockPreparedStmnt.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        // when(mockResultSet.getInt(Fields.GENERATED_KEYS)).thenReturn(userId);
    }

    @After
    public void tearDown() {
    }

    //  @Test
    public void testCreateWithNoExceptions() throws SQLException {

        SearchDAO instance = new SearchDAO();
        // instance.

        //verify and assert
     /*   verify(mockConn, times(1)).prepareStatement(anyString(), anyInt());
        verify(mockPreparedStmnt, times(6)).setString(anyInt(), anyString());
        verify(mockPreparedStmnt, times(1)).execute();
        verify(mockConn, times(1)).commit();
        verify(mockResultSet, times(2)).next();
        verify(mockResultSet, times(1)).getInt(Fields.GENERATED_KEYS);
        */
    }

    // @Test(expected = SQLException.class)
    public void testCreateWithPreparedStmntException() throws SQLException {

        //mock
        when(mockConn.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException());

/*
        try {
            UserDAO instance = new UserDAO(mockDataSource);
            instance.create(new User());
        } catch (SQLException se) {
            //verify and assert
            verify(mockConn, times(1)).prepareStatement(anyString(), anyInt());
            verify(mockPreparedStmnt, times(0)).setString(anyInt(), anyString());
            verify(mockPreparedStmnt, times(0)).execute();
            verify(mockConn, times(0)).commit();
            verify(mockResultSet, times(0)).next();
            verify(mockResultSet, times(0)).getInt(Fields.GENERATED_KEYS);
            throw se;
        }
*/
    }
}