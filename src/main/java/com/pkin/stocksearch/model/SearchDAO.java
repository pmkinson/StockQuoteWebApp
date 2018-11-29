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
package com.pkin.stocksearch.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

//schema = "public", catalog = "d9kni5dbb0lvdg"
@Entity
@Table(name = "searches")
public class SearchDAO implements DAOObject {
    private int id;
    private Integer typeOfSearch;
    private Integer userId;
    private String stockSymbol;
    private String family;
    private String familyVersion;
    private String os;
    private String osVersion;
    private String device;
    private Timestamp timeStamp;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "type_of_search", nullable = true)
    public Integer getTypeOfSearch() {
        return typeOfSearch;
    }

    public void setTypeOfSearch(Integer typeOfSearch) {
        this.typeOfSearch = typeOfSearch;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "stock_symbol", nullable = false, length = 6)
    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    @Basic
    @Column(name = "family", nullable = true, length = 20)
    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    @Basic
    @Column(name = "family_version", nullable = true, length = 6)
    public String getFamilyVersion() {
        return familyVersion;
    }

    public void setFamilyVersion(String familyVersion) {
        this.familyVersion = familyVersion;
    }

    @Basic
    @Column(name = "os", nullable = true, length = 6)
    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    @Basic
    @Column(name = "os_version", nullable = true, length = 6)
    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    @Basic
    @Column(name = "device", nullable = true, length = 20)
    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Basic
    @Column(name = "time_stamp", nullable = true)
    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchDAO that = (SearchDAO) o;
        return id == that.id &&
                Objects.equals(typeOfSearch, that.typeOfSearch) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(stockSymbol, that.stockSymbol) &&
                Objects.equals(family, that.family) &&
                Objects.equals(familyVersion, that.familyVersion) &&
                Objects.equals(os, that.os) &&
                Objects.equals(osVersion, that.osVersion) &&
                Objects.equals(device, that.device) &&
                Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, typeOfSearch, userId, stockSymbol, family, familyVersion, os, osVersion, device, timeStamp);
    }
}

