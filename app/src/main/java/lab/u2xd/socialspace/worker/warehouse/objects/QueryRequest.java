package lab.u2xd.socialspace.worker.warehouse.objects;

import lab.u2xd.socialspace.worker.warehouse.DataManager;

/**
 * Created by ysb on 2015-10-05.
 */
public class QueryRequest {

    private static final String SQL_INSERT = "";

    public  static final int QUERY_TYPE_INSERT = 1;

    private String sTableName;
    private int iQueryType = 0;
    private Datastone data;

    public QueryRequest(String table, Datastone data, int query_type) {
        sTableName = table;
        this.data = data;
        iQueryType = query_type;
    }

    public QueryRequest(Datastone data, int query_type) {
        sTableName = DataManager.NAME_MAINTABLE;
        this.data = data;
        iQueryType = query_type;
    }

    public Datastone getData() {
        return data;
    }

    public int getType() {
        return iQueryType;
    }

    public String getTableName() {
        return sTableName;
    }
}
