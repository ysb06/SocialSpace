package lab.u2xd.socialspace.servicer.object.error;

/**
 * Created by ysb on 2015-12-06.
 */
public class RadiusOutOfBoundException extends Exception {

    public RadiusOutOfBoundException() {
        super("Social Space에서 반지름은 0보다 크고 1과 같거나 작은 값을 가집니다.");
    }
}
