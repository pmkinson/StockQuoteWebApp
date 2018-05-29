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

import java.time.format.DateTimeFormatter;

/**
 * Abstract class to wrap all implementations of various StockQuotes with.
 * Contains commonalities that will be shared by all data models.
 * <p>
 * Ie, The stock symbol and a date format will be standardized for all StockQuoteDAO daoobjects.
 *
 * @author Peter Kinson
 */

public abstract class StockData {


    protected DateTimeFormatter dateTimeFormatter;  //Declare a date formatter.

    public StockData() {

        //Return a format of yyyy-MM-dd
        dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    }
}