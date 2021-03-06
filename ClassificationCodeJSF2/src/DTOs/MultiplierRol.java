package DTOs;
// Generated Jun 19, 2012 2:52:27 PM by Hibernate Tools 3.2.1.GA


import java.math.BigDecimal;

/**
 * MultiplierRol generated by hbm2java
 */
public class MultiplierRol  implements java.io.Serializable {


     private BigDecimal id;
     private String multiplier;
     private String unit;
     private String value;
     private String type;

    public MultiplierRol() {
    }

	
    public MultiplierRol(BigDecimal id) {
        this.id = id;
    }
    public MultiplierRol(BigDecimal id, String multiplier, String unit, String value,String type) {
       this.id = id;
       this.multiplier = multiplier;
       this.unit = unit;
       this.value = value;
       this.type=type;
    }
   
    public BigDecimal getId() {
        return this.id;
    }
    
    public void setId(BigDecimal id) {
        this.id = id;
    }
    public String getMultiplier() {
        return this.multiplier;
    }
    
    public void setMultiplier(String multiplier) {
        this.multiplier = multiplier;
    }
    public String getUnit() {
        return this.unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }




}


