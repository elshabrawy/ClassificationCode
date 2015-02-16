package DTOs;
// Generated May 29, 2012 11:19:30 AM by Hibernate Tools 3.2.1.GA


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * MessageAdmin generated by hbm2java
 */
public class MessageAdmin  implements java.io.Serializable {


     private BigDecimal msgId;
     private String msgDesc;
     private Set xlpSeComponents = new HashSet(0);

    public MessageAdmin() {
    }

	
    public MessageAdmin(BigDecimal msgId, String msgDesc) {
        this.msgId = msgId;
        this.msgDesc = msgDesc;
    }
    public MessageAdmin(BigDecimal msgId, String msgDesc, Set xlpSeComponents) {
       this.msgId = msgId;
       this.msgDesc = msgDesc;
       this.xlpSeComponents = xlpSeComponents;
    }
   
    public BigDecimal getMsgId() {
        return this.msgId;
    }
    
    public void setMsgId(BigDecimal msgId) {
        this.msgId = msgId;
    }
    public String getMsgDesc() {
        return this.msgDesc;
    }
    
    public void setMsgDesc(String msgDesc) {
        this.msgDesc = msgDesc;
    }
    public Set getXlpSeComponents() {
        return this.xlpSeComponents;
    }
    
    public void setXlpSeComponents(Set xlpSeComponents) {
        this.xlpSeComponents = xlpSeComponents;
    }




}


