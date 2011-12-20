/*
 * Clase que maneja las tarjetas de creditos junto con sus pagos y sus intereses
 */

package com.openbravo.pos.payment;
import java.util.HashMap;
/**
  * @author Juan Ignacio Saenz
 */
public class JPaymentElementMagCard {
    private String nombre;
    private HashMap<Integer,Double> cuotas;
    public JPaymentElementMagCard(String nombre){
        this.nombre = nombre;
        this.cuotas = new HashMap<Integer,Double>();
    }
    public void addCuota(Integer cuota, Double interes){
        this.cuotas.put(cuota, interes);
    }
    public Double getCuota(Integer cuota){
        return this.cuotas.get(cuota);
    }
    public HashMap<Integer,Double> getCuotas(){
        return this.cuotas;
    }
    public String getNombre(){
        return this.nombre;
    }
    public boolean equals(JPaymentElementMagCard t){
        return this.nombre.equals(t.getNombre());
    }
}
