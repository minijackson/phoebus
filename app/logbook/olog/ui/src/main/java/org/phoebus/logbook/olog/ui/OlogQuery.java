/*
 * Copyright (C) 2020 European Spallation Source ERIC.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.phoebus.logbook.olog.ui;

import java.time.Instant;

public class OlogQuery{

    /**
     * Timestamp keeping track of when query was last used.
     */
    private Instant lastUsed;
    /**
     * The string representation of the query, e.g. "start=12 hours&end=now".
     */
    private String query;
    /**
     * Indicates if this query is the default one. This is never flushed.
     */
    private boolean defaultQuery;

    public OlogQuery(String query){
        this.query = query;
        this.lastUsed = Instant.now();
    }

    public Instant getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Instant lastUsed) {
        this.lastUsed = lastUsed;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isDefaultQuery() {
        return defaultQuery;
    }

    public void setDefaultQuery(boolean defaultQuery) {
        this.defaultQuery = defaultQuery;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof OlogQuery)){
            return false;
        }
        OlogQuery otherOlogQuery = (OlogQuery)other;
        return query.equals(otherOlogQuery.getQuery());
    }

    @Override
    public int hashCode(){
        return query.hashCode();
    }
}
