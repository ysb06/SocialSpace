package lab.u2xd.socialspace.worker.warehouse.objects;

import lab.u2xd.socialspace.worker.warehouse.DataManager;

/** 솔직히 이 클래스도 별로 맘에 안듬, SQL Queue 방식의 처리 때문에 어쩔 수 없이 만들었지만
 * 이걸 보기 깔끔하게 정리할 아이디어가 생각이 나지 않음. 특히 쿼리 생성 부분,
 * 일단 떠오르는 아이디어로는 기본 QueryRequest를 만들고 SELECT, INSERT, UPDATE...등등 별로 상속받는 Request를 따로 만들면 좋을 듯
 * 언젠가 보기 좋게 정리를 해야 할 듯. 아니면 갈아엎던가...
 * Created by ysb on 2015-10-05.
 */
public class QueryRequest {

    private static final String SQL_INSERT = "";

    public  static final int QUERY_TYPE_INSERT = 1;
    public  static final int QUERY_TYPE_UPDATE = 2;

    private String sTableName;
    private int iQueryType = 0;
    private Datastone data;

    private String sWhere;
    private String[] sWhereConditions;

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

    public QueryRequest(String table, Datastone data, String field, String[] where_condition, int query_type) {
        sTableName = table;
        this.data = data;
        iQueryType = query_type;
        sWhere = field;
        sWhereConditions = where_condition;
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

    public String getWhere() {
        return sWhere;
    }

    public String[] getWhereConditions() {
        return sWhereConditions;
    }
}
