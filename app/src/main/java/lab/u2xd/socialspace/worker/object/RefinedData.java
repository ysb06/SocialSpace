package lab.u2xd.socialspace.worker.object;

@Deprecated
/**
 * Created by yim on 2015-10-01.
 */
public class RefinedData {
    public String Type = "";
    public String Agent = "";
    public String Target = "";
    public long Time = 0;
    public String Content = "";

    public RefinedData(String type, String agent, String content) {
        Type = type;
        Agent = agent;
        Target = "Me";
        Time = System.currentTimeMillis();
        Content = content;
    }

    // TODO: 2015-10-02 추후 Datastone을 사용하게 할 것
}
