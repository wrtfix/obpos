//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.payment;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.format.Formats;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.util.RoundUtils;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

/**
 *
 * @author juanchisan
 */
public class JPaymentMagcardPosnet extends javax.swing.JPanel implements JPaymentInterface {
    
    private JPaymentNotifier m_notifier;
    private Vector<JPaymentElementMagCard> tarjetas;
    private double m_dPaid;
    private double m_dTotal;  
    private boolean cuponAutorizado = false;
    
    private String m_sTarjetaActual;
    private Integer m_iCuotasActual;
    private Double m_dInteresActual;
    private Double m_dTotalCInteres;
    
    
    /** Constructor En base a JPaymentCash*/
    public JPaymentMagcardPosnet(JPaymentNotifier notifier, DataLogicSystem dlSystem) {
        
        m_notifier = notifier;
        
        initComponents();  
        
        m_jTendered.addPropertyChangeListener("Edition", new RecalculateState());
        m_jTendered.addEditorKeys(m_jKeys);
        //se carga el vector<JPaymentElementMagCard> con lo que se encuentra en el XML
        String code = dlSystem.getResourceAsXML("payment.Tarjetas");
        if (code != null) {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
                script.put("payment", new ScriptPaymentMagcard(dlSystem));    
                script.eval(code);
            } catch (ScriptException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotexecute"), e);
                msg.show(this);
            }
        }        
        //Cargo el comboBox JCBTarjeta con las tarjetas
        for(JPaymentElementMagCard t : tarjetas){
           jCBTarjeta.addItem(t.getNombre());
        }
    }
        
    public void activate(CustomerInfoExt customerext, double dTotal, String transID) {

        m_dTotal = dTotal;
        m_jTendered.reset();
        m_jTendered.activate();
        
        printState();        
    }
    /*        ver donde lo pongo
        if (!isCuponAutorizado()){
              MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotexecute"), e);
              msg.show(this);
        }
     */
    
    // [VER] que sentido tiene hacer esto o es como el cheque
    public PaymentInfo executePayment() {

        if ((m_dPaid - m_dTotal >= 0.0)&&(cuponAutorizado)) {
            // pago completo
            Double m_dTotalSInteres = m_dPaid;
            return new PaymentInfoMagcardPosnet(m_sTarjetaActual, m_iCuotasActual, m_dTotalCInteres, m_dInteresActual, m_dTotalSInteres, jTFAutorization.getText());
        
        } else {
            // pago parcial  [ver como seria]
            return new PaymentInfoCash(m_dPaid, m_dPaid);
        }        
    }
    public Component getComponent() {
        return this;
    }
    /*
     * Metodo para obligar a cargar el numero de cupon en una transaccion con tarjeta 
     */
    public boolean isCuponAutorizado(){
        return cuponAutorizado;
    }
    
    private void printState() {

        Double value = m_jTendered.getDoubleValue();
        if (value == null) {
            m_dPaid = m_dTotal;
        } else {
            m_dPaid = value;
        } 

        m_jMoney.setText(Formats.CURRENCY.formatValue(new Double(m_dPaid)));
        
        int iCompare = RoundUtils.compare(m_dPaid, m_dTotal);
        
        // if iCompare > 0 then the payment is not valid
        m_notifier.setStatus(m_dPaid > 0.0 && iCompare <= 0, iCompare == 0);
    }
    
    private class RecalculateState implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent evt) {
//            System.out.print("Aca entra a propertyChange");
            printState();
        }
    }    
    
    /* 
     * Clase encargada de cargar el script(XML)a la estructura de objetos
     */
    public class ScriptPaymentMagcard {
        
        private DataLogicSystem dlSystem;

        public ScriptPaymentMagcard(DataLogicSystem dlSystem) {
            this.dlSystem = dlSystem;
            tarjetas = new Vector<JPaymentElementMagCard>();
        }
       
        // Materializacion del XML en el vector<JPaymentElementMagcard>
        public void addTarjeta(String nombreTarjeta,int cuota, double interes) {
            
            for(JPaymentElementMagCard t : tarjetas){
                if(t.getNombre().equals(nombreTarjeta)){
                    t.addCuota(cuota, interes);
                    return;
                }
            }
            JPaymentElementMagCard t = new JPaymentElementMagCard(nombreTarjeta);
            t.addCuota(cuota, interes);
            tarjetas.add(t);
        }        
    }    
    
    //Metodo que devuelve las cuotas de la estructura de JPaymentElementMagCard
    private HashMap<Integer,Double> getCuotas(String nombreTarjeta){
         for(JPaymentElementMagCard t : tarjetas){
            if(t.getNombre().equals(nombreTarjeta)){
                return t.getCuotas();                
            }
         }
         return null;
    }
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        m_jMoney = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jCBPagos = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTFInteres = new javax.swing.JTextField();
        jTFTotalAPagar = new javax.swing.JTextField();
        jTFCuotasDe = new javax.swing.JTextField();
        jCBTarjeta = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jTFAutorization = new javax.swing.JTextField();
        jPanelMagcard = new javax.swing.JPanel();
        jPanelEditorKey = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jKeys = new com.openbravo.editor.JEditorKeys();
        jPanel3 = new javax.swing.JPanel();
        m_jTendered = new com.openbravo.editor.JEditorCurrencyPositive();

        setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel4.setPreferredSize(new java.awt.Dimension(0, 100));
        jPanel4.setLayout(null);

        jLabel8.setText(AppLocal.getIntString("Label.InputCash")); // NOI18N
        jPanel4.add(jLabel8);
        jLabel8.setBounds(20, 20, 100, 14);

        m_jMoney.setBackground(new java.awt.Color(153, 153, 255));
        m_jMoney.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jMoney.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jMoney.setOpaque(true);
        m_jMoney.setPreferredSize(new java.awt.Dimension(150, 25));
        jPanel4.add(m_jMoney);
        m_jMoney.setBounds(120, 20, 150, 25);

        jLabel1.setText("Tarjeta");
        jPanel4.add(jLabel1);
        jLabel1.setBounds(60, 100, 80, 14);

        jCBPagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBPagosActionPerformed(evt);
            }
        });
        jPanel4.add(jCBPagos);
        jCBPagos.setBounds(180, 130, 130, 20);

        jLabel2.setText("Cantidad de pagos");
        jPanel4.add(jLabel2);
        jLabel2.setBounds(60, 130, 110, 14);

        jLabel4.setText("Interes");
        jPanel4.add(jLabel4);
        jLabel4.setBounds(110, 170, 60, 14);

        jLabel5.setText("Total a pagar");
        jPanel4.add(jLabel5);
        jLabel5.setBounds(110, 200, 64, 14);

        jLabel7.setText("Cuotas de");
        jPanel4.add(jLabel7);
        jLabel7.setBounds(110, 230, 49, 14);

        jTFInteres.setEditable(false);
        jPanel4.add(jTFInteres);
        jTFInteres.setBounds(180, 170, 130, 20);

        jTFTotalAPagar.setEditable(false);
        jPanel4.add(jTFTotalAPagar);
        jTFTotalAPagar.setBounds(180, 200, 130, 20);

        jTFCuotasDe.setEditable(false);
        jPanel4.add(jTFCuotasDe);
        jTFCuotasDe.setBounds(180, 230, 130, 20);

        jCBTarjeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTarjetaActionPerformed(evt);
            }
        });
        jPanel4.add(jCBTarjeta);
        jCBTarjeta.setBounds(150, 100, 160, 20);

        jLabel3.setText("Número de Cupón");
        jPanel4.add(jLabel3);
        jLabel3.setBounds(70, 280, 86, 14);

        jTFAutorization.setName(""); // NOI18N
        jTFAutorization.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTFAutorizationFocusLost(evt);
            }
        });
        jPanel4.add(jTFAutorization);
        jTFAutorization.setBounds(180, 280, 130, 20);
        jPanel4.add(jPanelMagcard);
        jPanelMagcard.setBounds(30, 90, 300, 220);

        jPanel5.add(jPanel4, java.awt.BorderLayout.CENTER);

        add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanelEditorKey.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jPanel1.add(m_jKeys);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel3.add(m_jTendered, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3);

        jPanelEditorKey.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(jPanelEditorKey, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void jCBTarjetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTarjetaActionPerformed
        //Carga el comboBox con las cuotas de la tarjeta seleccionada    
        jCBPagos.removeAllItems();
        this.m_sTarjetaActual = jCBTarjeta.getSelectedItem().toString();
        HashMap<Integer,Double> cuotas = this.getCuotas(this.m_sTarjetaActual);
        for(Entry<Integer,Double> e : cuotas.entrySet())
            jCBPagos.addItem(e.getKey());
    }//GEN-LAST:event_jCBTarjetaActionPerformed
            
    private void jCBPagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBPagosActionPerformed
        //Carga el jTFInteres, Calcula el jTFCuotasDe y jTFTotalAPagar
        if(jCBPagos.getItemCount()>0){
            HashMap<Integer,Double> cuota = this.getCuotas(this.m_sTarjetaActual);
            this.m_iCuotasActual=Integer.parseInt(jCBPagos.getSelectedItem().toString());
           
            this.m_dInteresActual = cuota.get(this.m_iCuotasActual);
            jTFInteres.setText(this.m_dInteresActual.toString());
            
            this.m_dTotalCInteres = m_dPaid * this.m_dInteresActual;
            jTFTotalAPagar.setText(Formats.CURRENCY.formatValue(new Double(m_dTotalCInteres)));
            //Tener en cuenta que este valor es el que se va a tener que facturar despues
            Double cuotasDe = m_dTotalCInteres/this.m_iCuotasActual;
            jTFCuotasDe.setText(Formats.CURRENCY.formatValue(new Double(cuotasDe)));
        }
    }//GEN-LAST:event_jCBPagosActionPerformed

    private void jTFAutorizationFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFAutorizationFocusLost
        //verifica que se cargo el numero de cupon
        if(!jTFAutorization.getText().isEmpty())
            this.cuponAutorizado = true;
    }//GEN-LAST:event_jTFAutorizationFocusLost
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jCBPagos;
    private javax.swing.JComboBox jCBTarjeta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelEditorKey;
    private javax.swing.JPanel jPanelMagcard;
    private javax.swing.JTextField jTFAutorization;
    private javax.swing.JTextField jTFCuotasDe;
    private javax.swing.JTextField jTFInteres;
    private javax.swing.JTextField jTFTotalAPagar;
    private com.openbravo.editor.JEditorKeys m_jKeys;
    private javax.swing.JLabel m_jMoney;
    private com.openbravo.editor.JEditorCurrencyPositive m_jTendered;
    // End of variables declaration//GEN-END:variables
    
}
