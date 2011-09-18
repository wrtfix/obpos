/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openbravo.pos.payment;
import java.util.Hashtable;
import java.util.Vector;
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
    public Hashtable<Integer,Double> getCuotas(){
        return this.cuotas;
    }
    public String getNombre(){
        return this.nombre;
    }
    public boolean equals(JPaymennteElementMagCard t){
        return this.nombre.equals(t.getNombre());
    }
}
