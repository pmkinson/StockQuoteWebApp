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
 *
 */

package com.uml.edu.stocksearch.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "stocks", schema = "stockquote", catalog = "dee5uoi05ai36v")
public class SearchDAO implements DAOObject {
    private int id;
    private Timestamp date;
    private int stockId;
    private int systemId;
    private int browserId;
    private int userId;
    private int typeOfSearch;
    private String stockSymbol;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "date", nullable = false)
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Basic
    @Column(name = "stock_id", nullable = false)
    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    @Basic
    @Column(name = "system_id", nullable = false)
    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    @Basic
    @Column(name = "browser_id", nullable = false)
    public int getBrowserId() {
        return browserId;
    }

    public void setBrowserId(int browserId) {
        this.browserId = browserId;
    }

    @Basic
    @Column(name = "user_id", nullable = false)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "type_of_search", nullable = false)
    public int getTypeOfSearch() {
        return typeOfSearch;
    }

    public void setTypeOfSearch(int typeOfSearch) {
        this.typeOfSearch = typeOfSearch;
    }

    @Basic
    @Column(name = "stock_symbol", nullable = true)
    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchDAO that = (SearchDAO) o;
        return id == that.id &&
                stockId == that.stockId &&
                systemId == that.systemId &&
                browserId == that.browserId &&
                userId == that.userId &&
                typeOfSearch == that.typeOfSearch &&
                Objects.equals(date, that.date) &&
                Objects.equals(stockSymbol, that.stockSymbol);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, date, stockId, systemId, browserId, userId, typeOfSearch, stockSymbol);
    }
}
