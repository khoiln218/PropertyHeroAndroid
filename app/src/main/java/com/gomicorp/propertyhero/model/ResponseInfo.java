package com.gomicorp.propertyhero.model;

import java.io.Serializable;

/**
 * Created by CTO-HELLOSOFT on 3/29/2016.
 */
public class ResponseInfo implements Serializable {

    private boolean success;
    private long value;

    public ResponseInfo() {
    }

    public ResponseInfo(boolean success, long value) {
        this.success = success;
        this.value = value;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}

