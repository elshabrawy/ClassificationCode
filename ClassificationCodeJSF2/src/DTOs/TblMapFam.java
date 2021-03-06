package DTOs;
// Generated May 29, 2012 11:19:30 AM by Hibernate Tools 3.2.1.GA


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * TblMapFam generated by hbm2java
 */
public class TblMapFam  implements java.io.Serializable {


     private BigDecimal famId;
     private String family;
     private Set xlpSeComponents = new HashSet(0);

    public TblMapFam() {
    }

	
    public TblMapFam(BigDecimal famId, String family) {
        this.famId = famId;
        this.family = family;
    }
    public TblMapFam(BigDecimal famId, String family, Set xlpSeComponents) {
       this.famId = famId;
       this.family = family;
       this.xlpSeComponents = xlpSeComponents;
    }
   
    public BigDecimal getFamId() {
        return this.famId;
    }
    
    public void setFamId(BigDecimal famId) {
        this.famId = famId;
    }
    public String getFamily() {
        return this.family;
    }
    
    public void setFamily(String family) {
        this.family = family;
    }
    public Set getXlpSeComponents() {
        return this.xlpSeComponents;
    }
    
    public void setXlpSeComponents(Set xlpSeComponents) {
        this.xlpSeComponents = xlpSeComponents;
    }




}


