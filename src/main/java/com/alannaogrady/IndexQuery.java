package com.alannaogrady;

public class IndexQuery {
    private int queryNum;
    private String IndexQuery;


    public IndexQuery(int queryId, String query) {
        queryNum = queryId;
        IndexQuery = query;
    }

    public int getQueryId() {
        return queryNum;
    }

    public String getQuery() {
        return IndexQuery;
    }

}
