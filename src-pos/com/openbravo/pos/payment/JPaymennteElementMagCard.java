/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openbravo.pos.payment;
import java.util.Hashtable;
/**
 *
 * @author alumno
 */
public class JPaymennteElementMagCard {
    private String nombre;
    private Hashtable<Integer,Double> cuotas;
    public JPaymennteElementMagCard(String nombre){
        this.nombre = nombre;
        this.cuotas = new Hashtable<Integer,Double>();
    }
    public void addCuota(Integer cuota, Double interes){
        this.cuotas.put(cuota, interes);
    }
    public Double getCuota(Integer cuota){
        return this.cuotas.get(cuota);
    }
}
