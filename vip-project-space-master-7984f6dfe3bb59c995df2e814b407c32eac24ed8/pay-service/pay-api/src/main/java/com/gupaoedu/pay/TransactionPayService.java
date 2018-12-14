package com.gupaoedu.pay;

import com.gupaoedu.pay.dto.PaymentNotifyRequest;
import com.gupaoedu.pay.dto.PaymentNotifyResponse;
import com.gupaoedu.pay.dto.PaymentRequest;
import com.gupaoedu.pay.dto.PaymentResponse;

/**

 */
public interface TransactionPayService {

    /**
     * 执行支付操作
     * @param request
     * @return
     */
    PaymentResponse execPay(PaymentRequest request);


    /**
     * 支付结果通知处理
     * @param request
     * @return
     */
    PaymentNotifyResponse paymentResultNotify(PaymentNotifyRequest request);
}
