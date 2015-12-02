package lab.u2xd.socialspace.worker.miner.objects;

import android.util.Log;

import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

/**
 * Created by ysb on 2015-11-24.
 */
public class NotificationPickaxe {

    public static Datastone mine(String packageName, String[] notification, boolean isCompatMode) {
        Log.e("NotificationPickaxe", "Mining..." + packageName);

        if(packageName.equals("com.kakao.talk")) {                       //카카오톡
            Log.e("NotificationPickaxe", "It is KakaoTalk data!");
            return runKakaoTalk(notification);

        } else if(packageName.equals("com.facebook.katana")) {         //페이스북
            Log.e("NotificationPickaxe", "Facebook data!");
            return runFacebookStatus(notification, isCompatMode);

        } else if(packageName.equals("com.facebook.orca")) {           //페이스북 메신저
            Log.e("NotificationPickaxe", "Facebook message data!");
            return runFacebookMessage(notification);

        } else if(packageName.equals("com.twitter.android")) {        //트위터
            Log.e("NotificationPickaxe", "Tweeter data!");
            return runTweeter(notification, isCompatMode);

        } else if(packageName.equals("jp.naver.line.android")) {      //라인 메신저
            Log.e("NotificationPickaxe", "Line data!");
            return runLine(notification, isCompatMode);

        } else if(packageName.equals("com.kakao.story")) {                     //카카오스토리
            Log.e("NotificationPickaxe", "Kakaostory data!");
            return runKakaoStory(notification, isCompatMode);

        } else if(packageName.equals("com.nhn.android.band")) {                     //카카오스토리
            Log.e("NotificationPickaxe", "band data!");
            return runKakaoStory(notification, isCompatMode);

        } else {
            Log.e("NotificationPickaxe", "It is nothing. Sorry");
            return null;
        }
    }

    private static Datastone runKakaoTalk(String[] noti) {
        Datastone datastone = new Datastone();
        datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_KAKAOTALK);
        datastone.put(DataManager.FIELD_AGENT, noti[1]);
        datastone.put(DataManager.FIELD_TARGET, "Null");
        datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
        datastone.put(DataManager.FIELD_CONTENT, "길이: " + noti[2].length());

        return datastone;
    }

    private static Datastone runFacebookStatus(String[] noti, boolean isCompatMode) {
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
        datastone.put(DataManager.FIELD_TARGET, "Null");
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
            datastone.put(DataManager.FIELD_TARGET, "Null");
            datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
            datastone.put(DataManager.FIELD_CONTENT, "길이: " + content);
        }

        return datastone;
    }

    private static Datastone runTweeter(String[] rawString, boolean isCompatMode) {
        Datastone datastone = new Datastone();

        String sAgent = "";
        String sContent = "";

        if(isCompatMode) {
            if(rawString[3].equals("")) {                             //트윗, 메시지 정상
                sAgent = rawString[1];
                sContent = (rawString[2].length() - 1) + "";
            } else if(rawString[2].equals("")) {                    //트윗 중복
                sAgent = rawString[3];
                sContent = "null";
            } else if(rawString[2].equals("Null")) {                //메시지 중복
                sAgent = rawString[3].split("@")[0];
                sAgent = sAgent.substring(0, sAgent.length() - 1);
                sContent = "null";
            } else {
                Log.e("NotificationPickaxe", "Save Failed");
                return null;
            }
        } else {
            if(rawString[1].equals("")) {
                Log.e("NotificationPickaxe", "Twitter but not Twitter");
                return null;
            } else if(rawString[1].equals("트윗을 보냈습니다")) {
                Log.e("NotificationPickaxe", "Twitter just notification");
                return null;
            } else {
                sAgent = rawString[1];
                sContent = (rawString[2].length() - 1) + "";
            }
        }

        datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_TWITTER);
        datastone.put(DataManager.FIELD_AGENT, sAgent);
        datastone.put(DataManager.FIELD_TARGET, "Null");
        datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
        datastone.put(DataManager.FIELD_CONTENT, "길이: " + sContent);

        return datastone;
    }

    private static Datastone runLine(String[] rawString, boolean isCompatMode) {
        Datastone datastone = new Datastone();

        String sAgent = "";
        String sContent = "";

        if(isCompatMode) {
            String str = rawString[0];
            str = str.substring(1, str.length() - 1);

            String[] strs;
            if(str.contains(", ")) {
                strs = str.split(", ", 2);
                try {
                    sAgent = strs[1];
                    sContent = strs[0].length() + "";
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.wtf("MainActivity", "Unexpected Form : " + str);
                    return null;
                }
            } else if(str.contains(":")) {
                return null;
            } else {
                return null;
            }
        } else {
            sAgent = rawString[0];
            sContent = rawString[1];
        }

        datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_LINE);
        datastone.put(DataManager.FIELD_AGENT, sAgent);
        datastone.put(DataManager.FIELD_TARGET, "Me");
        datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
        datastone.put(DataManager.FIELD_CONTENT, "길이: " + sContent);

        return datastone;
    }

    private static Datastone runKakaoStory(String[] rawString, boolean isCompatMode) {
        Datastone datastone = new Datastone();

        String sAgent = "";
        String sContent = "";

        if (isCompatMode) {
            Log.e("NotificationPickaxe", "Not supported");
            return null;
        } else {
            if(rawString[2].contains("님과")) {
                sAgent = rawString[2].split("님과")[0];
                sContent = "null";
            } else if (rawString[2].contains("님이")) {
                sAgent = rawString[2].split("님이")[0];
                sContent = "null";
            } else {
                return null;
            }
        }

        datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_KAKAOSTORY);
        datastone.put(DataManager.FIELD_AGENT, sAgent);
        datastone.put(DataManager.FIELD_TARGET, "Null");
        datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
        datastone.put(DataManager.FIELD_CONTENT, "길이: " + sContent);

        return datastone;
    }

    private static Datastone runBand(String[] rawString, boolean isCompatMode) {
        Datastone datastone = new Datastone();
        Log.e("NotificationPickaxe", rawString[0]);
        Log.e("NotificationPickaxe", rawString[1] + "|" + rawString[2] + "|" + rawString[3]);

        String sAgent = "";
        String sContent = "";

        if (isCompatMode) {
            Log.e("NotificationPickaxe", "Not supported");
            return null;
        } else {
            if(rawString[2].contains("님과")) {
                sAgent = rawString[2].split("님과")[0];
                sContent = "null";
            } else if (rawString[2].contains("님이")) {
                sAgent = rawString[2].split("님이")[0];
                int iIndexSTR = rawString[2].indexOf("\"");
                if (iIndexSTR != -1) {                                  //일반 페이스북 담벼락
                    String str = rawString[2].substring(iIndexSTR);
                    sContent = str.length() + "";
                    Log.e("NotificationPickaxe", "Band -> " + str);
                } else  {
                    sContent = "null";
                }
            } else {
                return null;
            }
        }

        datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_BAND);
        datastone.put(DataManager.FIELD_AGENT, sAgent);
        datastone.put(DataManager.FIELD_TARGET, "Null");
        datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
        datastone.put(DataManager.FIELD_CONTENT, "길이: " + sContent);

        return datastone;
    }
}
