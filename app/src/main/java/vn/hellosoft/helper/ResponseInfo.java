package vn.hellosoft.helper;

/**
 * Created by CTO-HELLOSOFT on 4/21/2016.
 */
public class ResponseInfo {
    public boolean success;
    public long data;

    public ResponseInfo() {
    }

    public ResponseInfo(boolean success, long data) {
        this.success = success;
        this.data = data;
    }
}
