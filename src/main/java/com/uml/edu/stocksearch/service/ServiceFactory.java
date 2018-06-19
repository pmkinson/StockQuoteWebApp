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

public class ServiceFactory {
    /**
     * StockService Factory method.
     *
     * @return Concrete instance of StockService
     */
    public static StockService getStockServiceInstance() {
        return new StockService();
    }

    /**
     * DatabaseService Factory method.
     *
     * @return Concrete instance of DatabaseService
     */
    public static DatabaseService getDatabaseServiceInstance() {
        return new DatabaseService();
    }
}
