package com.gupaoedu.pay.dto;

import com.gupaoedu.pay.commons.AbstractRequest;
import com.gupaoedu.pay.commons.AbstractResponse;

import java.io.Serializable;
import java.util.Map;

/**

 */
public class PaymentNotifyResponse extends AbstractResponse implements Serializable {

    private static final long serialVersionUID = -4767584167298163287L;
    private String result;//返回给服务端的执行结果的报文

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "PaymentNotifyResponse{" +
                "result='" + result + '\'' +
                "} " + super.toString();
    }
}
