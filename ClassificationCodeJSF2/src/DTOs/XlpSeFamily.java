package DTOs;
// Generated May 29, 2012 11:19:30 AM by Hibernate Tools 3.2.1.GA


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * XlpSeFamily generated by hbm2java
 */
public class XlpSeFamily  implements java.io.Serializable {


     private BigDecimal seriesId;
     private String seriesName;
     private BigDecimal manId;
     private String processingDate;
     private Set xlpSeComponents = new HashSet(0);

    public XlpSeFamily() {
    }

	
    public XlpSeFamily(BigDecimal seriesId) {
        this.seriesId = seriesId;
    }
    public XlpSeFamily(BigDecimal seriesId, String seriesName, BigDecimal manId, String processingDate, Set xlpSeComponents) {
       this.seriesId = seriesId;
       this.seriesName = seriesName;
       this.manId = manId;
       this.processingDate = processingDate;
       this.xlpSeComponents = xlpSeComponents;
    }
   
    public BigDecimal getSeriesId() {
        return this.seriesId;
    }
    
    public void setSeriesId(BigDecimal seriesId) {
        this.seriesId = seriesId;
    }
    public String getSeriesName() {
        return this.seriesName;
    }
    
    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
    public BigDecimal getManId() {
        return this.manId;
    }
    
    public void setManId(BigDecimal manId) {
        this.manId = manId;
    }
    public String getProcessingDate() {
        return this.processingDate;
    }
    
    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
    }
    public Set getXlpSeComponents() {
        return this.xlpSeComponents;
    }
    
    public void setXlpSeComponents(Set xlpSeComponents) {
        this.xlpSeComponents = xlpSeComponents;
    }




}


