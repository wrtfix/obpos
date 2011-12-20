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
   // protected Double m_dTotal;
    //Tarjeta y cantidad de cuotas 
    protected String m_sMagcardName;
    protected Integer m_iCuotas;
    //coeficiente del interes generado por las cuotas de la tarjeta
    protected Double m_dInteres;
    //Resultado de la inclucion del interes en el Total
    protected Double m_dTotalCInteres;
    //ID transaccion
    protected Double m_dTotalSInteres;
    //Autorizacion (numero de cupon)
    protected String m_sAuthorization;    
    // no se usa
    protected String m_sTransactionID;
    

    
/* Creates a new instance of PaymentInfoMagcardPosnet */
     public PaymentInfoMagcardPosnet(String sMagcardName, Integer iCuotas, Double dTotalCInteres, Double dInteres, Double dTotalSInteres,String sAuthorization){
        
        m_sMagcardName = sMagcardName; //se carga del combolist
        m_iCuotas = iCuotas;  //combolist
        m_dTotalCInteres = dTotalCInteres; //se calcula de la planilla
        m_dInteres = dInteres; //s saca del xml
        m_dTotalSInteres = dTotalSInteres;
        m_sAuthorization = sAuthorization;//se carga del label
        m_sTransactionID = sAuthorization;//supongo que es la utorizaciono numero de cupon
        
//       m_dTotal = dTotal;
//       m_sErrorMessage = null;
//       m_sReturnMessage = null;
    }
   
    @Override
    public String getName() {
        return "Tarjeta-Posnet";
    }
//ver si va dTotalCInteres o esto
    @Override
    public double getTotal() {
        return m_dTotalCInteres;
    }

    @Override
    public PaymentInfo copyPayment(){
        return new PaymentInfoMagcardPosnet(m_sMagcardName , m_iCuotas, m_dTotalCInteres, m_dInteres, m_dTotalSInteres,m_sAuthorization);
    }

    @Override
    public String getTransactionID() {
        return m_sAuthorization;
    }
}
