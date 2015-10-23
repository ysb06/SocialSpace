package lab.u2xd.socialspace.worker.warehouse.objects;

/**
 * Created by yim on 2015-09-07.
 */
public interface Queryable {

    /** 데이터 쿼리를 받아 처리를 완료했을 때 호출.  */
    void onFinish_Query();
}
