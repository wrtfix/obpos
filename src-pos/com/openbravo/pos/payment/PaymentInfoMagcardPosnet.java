/*El sentido de esta clase es Retener la informacion proporcionada por el Posnet
 *para poder realizar la transaccion de venta con tarjeta en Argentina.
 * Tendria el mismo funcionamiento que la original clase PaymentInfoMagcard
 */
package com.openbravo.pos.payment;

/**
 * @author Juanchisan
 */

public class PaymentInfoMagcardPosnet extends PaymentInfo {
    // monto total
    protected double m_dTotal;
    //Tarjeta y cantidad de cuotas 
    protected String m_sMagcardName;
    protected String m_sCuotas;
    //coeficiente del interes generado por las cuotas de la tarjeta
    protected double m_dInteres;
    //Resultado de la inclucion del interes en el Total
    protected double m_dTotalCInteres;
    //ID transaccion
    protected String m_sTransactionID;
    //Autorizacion (numero de cupon)
    protected String m_sAuthorization;    
    //
    protected String m_sErrorMessage;
    protected String m_sReturnMessage;
    
/* Creates a new instance of PaymentInfoMagcardPosnet */
     public PaymentInfoMagcardPosnet(String sMagcardName, String sCuotas, double dTotalCInteres, double dInteres,String sTransactionID, double dTotal) {
        
        m_sMagcardName = sMagcardName; //se carga del combolist
        m_sCuotas = sCuotas;  //combolist
        m_dTotalCInteres = dTotalCInteres; //se calcula de la planilla
        m_dInteres = dInteres; //s saca del xml
             
        m_sTransactionID = sTransactionID; // generado por el sistema
        m_dTotal = dTotal;
        
        m_sAuthorization = null;//se carga del label
        m_sErrorMessage = null;
        m_sReturnMessage = null;
    }
   
    @Override
    public String getName() {
        return "Posnet";
    }
//ver si va dTotalCInteres o esto
    @Override
    public double getTotal() {
        return m_dTotal;
    }

    @Override
    public PaymentInfo copyPayment(){
        PaymentInfoMagcardPosnet p= new PaymentInfoMagcardPosnet( m_sMagcardName, m_sCuotas, m_dTotalCInteres, m_dInteres, m_sTransactionID, m_dTotal);
        p.m_sAuthorization = this.m_sAuthorization;
        p.m_sErrorMessage = this.m_sErrorMessage;
        return p;
    }

    @Override
    public String getTransactionID() {
        return m_sTransactionID;
    }
}
