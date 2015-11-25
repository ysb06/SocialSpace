package lab.u2xd.socialspace.worker.miner.objects;

import android.util.Log;

import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

/**
 * Created by ysb on 2015-11-24.
 */
public class NotificationPickaxe {

    public static Datastone mine(String packageName, String[] notification) {
        Log.e("NotificationPickaxe", "Mining..." + packageName);

        if(packageName.equals("com.kakao.talk")) {                     //카카오톡
            Log.e("NotificationPickaxe", "It is KakaoTalk data!");
            return runKakaoTalk(notification);
        } else if(packageName.equals("com.facebook.katana")) {         //페이스북
            Log.e("NotificationPickaxe", "Facebook data!");
            return runFacebookStatus(notification);
        } else if(packageName.equals("com.facebook.orca")) {           //페이스북 메신저
            Log.e("NotificationPickaxe", "Facebook message data!");
            return runFacebookMessage(notification);
        } else {
            Log.e("NotificationPickaxe", "It is nothing. Sorry");
            return null;
        }
    }

    private static Datastone runKakaoTalk(String[] noti) {
        Datastone datastone = new Datastone();
        datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_KAKAOTALK);
        datastone.put(DataManager.FIELD_AGENT, noti[1]);
        datastone.put(DataManager.FIELD_TARGET, "Me");
        datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
        datastone.put(DataManager.FIELD_CONTENT, "길이: " + noti[2].length());

        return datastone;
    }

    private static Datastone runFacebookStatus(String[] noti) {
        // TODO: 2015-11-23 페이스 북 메시지 종류 더 파악해서 기능 추가
        Datastone datastone = new Datastone();
        String sType = "Unknown";
        String sAgent = "";
        if(noti[1].equals("Error") && noti[2].equals("Error")) {    //Compat 페이스북 담벼락
            String[] temp = noti[0].split(" ", 2);
            String str = temp[0];
            int iIndexSTR = str.indexOf("님이");
            if (iIndexSTR != -1) {                                  //일반 페이스북 담벼락
                sAgent = str.substring(1, iIndexSTR);
                sType = "타임라인";
            }
        } else {
            String str = noti[2];
            int iIndexSTR = str.indexOf("님이");
            if (iIndexSTR != -1) {                                  //일반 페이스북 담벼락
                sAgent = str.substring(0, iIndexSTR);
                sType = "타임라인";
            } else {                                            //페이스북 메시지
                sAgent = noti[1];
                sType = "메시지";
            }
        }
        datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_FACEBOOK);
        datastone.put(DataManager.FIELD_AGENT, sAgent);
        datastone.put(DataManager.FIELD_TARGET, "Me");
        datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
        datastone.put(DataManager.FIELD_CONTENT, sType);

        return datastone;
    }

    private static Datastone runFacebookMessage(String[] noti) {
        Datastone datastone = new Datastone();

        if(noti[1] == null && noti[2] == null) {
            Log.e("NotificationPickaxe", "It looks Facebook message data, but it is not, I think.");
            Log.e("NotificationPickaxe", noti[0]);
            return null;
        } else {
            Log.e("NotificationPickaxe", "Facebook message data!");
            String content = "";
            if(noti[2] == null) {
                content = "null";
            } else {
                Log.e("NotificationPickaxe", noti[2]);
                content = noti[2].length() + "";
            }

            datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_FACEBOOK);
            datastone.put(DataManager.FIELD_AGENT, noti[1]);
            datastone.put(DataManager.FIELD_TARGET, "Me");
            datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
            datastone.put(DataManager.FIELD_CONTENT, "길이: " + content);
        }

        return datastone;
    }
}
