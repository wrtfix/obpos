package com.openbravo.pos.payment;

/**
 *
 * @author Juanchisan
 */
public class PaymentGatewayArg implements PaymentGateway {

    @Override
    public void execute(PaymentInfoMagcard payinfo) {
        payinfo.paymentOK("gay", payinfo.getTransactionID() , "");
    }
    /*Aca iria en vez del "OK" el numero de cupon (m_sAutorization)
     * y tendria que ver donde cargo las cuotas, la tarjeta 
     */
}
/*
 * Ver si sirvede algo estoo
 */