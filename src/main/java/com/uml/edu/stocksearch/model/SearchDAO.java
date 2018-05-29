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
 * package com.uml.edu.stocksearch.model;
 * <p>
 * /**
 * Empty wrapper for all DAOObject objects.
 */

package com.uml.edu.stocksearch.model;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "stocks", schema = "stockquote")
public class SearchDAO extends StockData implements DAOObject {

    public SearchDAO() {
        //Empty constructor per hibernate
    }

    //Declare variable to correspond to database columns
    private int id;
    private Timestamp date;
    private int stock_id;
    private int system_id;
    private int browser_id;
    private int user_id;
    private int type_of_search;


    /**
     * Primary Key - Unique ID for a particular row in the hobby table.
     *
     * @return an <CODE>int</CODE> value
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {

        return id;
    }

    /**
     * Primary Key - Sets unique ID for a particular row in the table.
     * This method should not be called by client code. The value is managed in internally.
     *
     * @param id a unique <CODE>int</CODE> value.
     **/
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the stock symbol
     *
     * @return - <CODE>String</CODE> The stocks symbol associated with the StockQuoteDAO object.
     */
    @Basic
    @Column(name = "date", nullable = false, insertable = true, updatable = true)
    public Timestamp getDate() {

        return date;
    }

    /**
     * Setter for date.
     *
     * @param timestamp
     */
    public void setDate(Timestamp timestamp) {
        this.date = timestamp;
    }

    /**
     * Getter for stock_id
     *
     * @return stock_id
     */
    @Basic
    @Column(name = "stock_id", nullable = false, insertable = true, updatable = true)
    public int getStock_id() {
        return stock_id;
    }

    /**
     * Setter for stock_id
     *
     * @param stock_id
     */
    public void setStock_id(int stock_id) {
        this.stock_id = stock_id;
    }

    /**
     * Getter for system_id
     *
     * @return system_id
     */
    @Basic
    @Column(name = "system_id", nullable = false, insertable = true, updatable = true)
    public int getSystem_id() {
        return system_id;
    }

    /**
     * Setter for system_id
     *
     * @param system_id
     */
    public void setSystem_id(int system_id) {
        this.system_id = system_id;
    }

    /**
     * Getter for browser_id
     *
     * @return browser_id
     */
    @Basic
    @Column(name = "getBrowser_id", nullable = false, insertable = true, updatable = true)
    public int getBrowser_id() {
        return browser_id;
    }

    /**
     * Setter for browser_id
     *
     * @param browser_id
     */
    public void setBrowser_id(int browser_id) {
        this.browser_id = browser_id;
    }

    /**
     * Getter for user_id
     *
     * @return user_id
     */
    @Basic
    @Column(name = "user_id", nullable = false, insertable = true, updatable = true)
    public int getUser_id() {
        return user_id;
    }

    /**
     * Setter for user_id
     *
     * @param user_id
     */
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    /**
     * Getter for type_of_search
     *
     * @return type_of_search
     */
    @Basic
    @Column(name = "type_of_search", nullable = false, insertable = true, updatable = true)
    public int getType_of_search() {
        return type_of_search;
    }

    /**
     * Setter for type_of_search
     *
     * @param type_of_search
     */
    public void setType_of_search(int type_of_search) {
        this.type_of_search = type_of_search;
    }
}
