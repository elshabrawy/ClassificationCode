package DTOs;
// Generated May 29, 2012 11:19:30 AM by Hibernate Tools 3.2.1.GA


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * TblMapGen generated by hbm2java
 */
public class TblMapGen  implements java.io.Serializable {


     private BigDecimal genId;
     private String generic;
     private Set xlpSeComponents = new HashSet(0);

    public TblMapGen() {
    }

	
    public TblMapGen(BigDecimal genId, String generic) {
        this.genId = genId;
        this.generic = generic;
    }
    public TblMapGen(BigDecimal genId, String generic, Set xlpSeComponents) {
       this.genId = genId;
       this.generic = generic;
       this.xlpSeComponents = xlpSeComponents;
    }
   
    public BigDecimal getGenId() {
        return this.genId;
    }
    
    public void setGenId(BigDecimal genId) {
        this.genId = genId;
    }
    public String getGeneric() {
        return this.generic;
    }
    
    public void setGeneric(String generic) {
        this.generic = generic;
    }
    public Set getXlpSeComponents() {
        return this.xlpSeComponents;
    }
    
    public void setXlpSeComponents(Set xlpSeComponents) {
        this.xlpSeComponents = xlpSeComponents;
    }




}


