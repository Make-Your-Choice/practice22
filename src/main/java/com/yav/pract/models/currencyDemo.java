package com.yav.pract.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

@Component
@XmlRootElement(name="info")
@XmlAccessorType(XmlAccessType.NONE)
public class currencyDemo {
    @XmlAttribute
    private int ida;
    @XmlElement
    private String cbId;
    @XmlElement
    private String name;
    @Autowired
    public currencyDemo() {
    }
    public currencyDemo(int ida, String cbId, String name) {
        this.ida = ida;
        this.cbId = cbId;
        this.name = name;
    }
    public int getIda() {
        return ida;
    }
    public void setIda(int ida) {
        this.ida = ida;
    }
    public String getCbId() {
        return cbId;
    }
    public void setCbId(String cbId) {
        this.cbId = cbId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
