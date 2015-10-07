package lab.u2xd.socialspace.worker.warehouse;

/**
 * Created by ysb on 2015-10-05.
 */
public class QueryRequest {

    private static final String SQL_INSERT = "";

    public  static final int QUERY_TYPE_INSERT = 1;

    private int iQueryType = 0;
    private Datastone data;

    public QueryRequest(Datastone data, int query_type) {
        this.data = data;
        iQueryType = query_type;
    }

    public Datastone getData() {
        return data;
    }

    public int getType() {
        return iQueryType;
    }
}
