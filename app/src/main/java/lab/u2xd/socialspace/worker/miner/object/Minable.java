package lab.u2xd.socialspace.worker.miner.object;

import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

/**
 * Created by yim on 2015-09-07.
 */
public interface Minable {

    /** 데이터 쿼리를 받아 처리를 완료했을 때 호출.
     *
     * @param data 요청받은 쿼리 결과
     */
    void onFinish_Request(Datastone[] data);
}
