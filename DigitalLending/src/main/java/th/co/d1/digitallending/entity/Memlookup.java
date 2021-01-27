/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Poomsakul Senakul
 */
@Entity
@Table(name = "MEMLOOKUP")
@NamedQueries({
    @NamedQuery(name = "Memlookup.findAll", query = "SELECT m FROM Memlookup m")})
public class Memlookup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "UUID")
    private String uuid;
    @Size(max = 255)
    @Column(name = "LOOKUPCODE")
    private String lookupcode;
    @Size(max = 255)
    @Column(name = "LOOKUPNAMETH")
    private String lookupnameth;
    @Size(max = 255)
    @Column(name = "LOOKUPNAMEEN")
    private String lookupnameen;
    @Size(max = 255)
    @Column(name = "LOOKUPTYPE")
    private String lookuptype;
    @Size(max = 255)
    @Column(name = "LOOKUPVALUE")
    private String lookupvalue;
    @Size(max = 2147483647)
    @Column(name = "ATTR1")
    private String attr1;
    @Size(max = 2147483647)
    @Column(name = "ATTR2")
    private String attr2;
    @Size(max = 2147483647)
    @Column(name = "ATTR3")
    private String attr3;
    @Size(max = 2147483647)
    @Column(name = "ATTR4")
    private String attr4;
    @Size(max = 2147483647)
    @Column(name = "ATTR5")
    private String attr5;
    @Size(max = 2147483647)
    @Column(name = "ATTR6")
    private String attr6;
    @Size(max = 2147483647)
    @Column(name = "ATTR7")
    private String attr7;
    @Size(max = 2147483647)
    @Column(name = "ATTR8")
    private String attr8;
    @Size(max = 2147483647)
    @Column(name = "ATTR9")
    private String attr9;
    @Size(max = 2147483647)
    @Column(name = "ATTR10")
    private String attr10;
    @Column(name = "FLAGEDIT")
    private Boolean flagedit;
    @Column(name = "FLAGCREATE")
    private Boolean flagcreate;
    @Column(name = "STATUS")
    private Integer status;
    @Size(max = 2147483647)
    @Column(name = "DESCRIPTION")
    private String description;

    public Memlookup() {
    }

    public Memlookup(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLookupcode() {
        return lookupcode;
    }

    public void setLookupcode(String lookupcode) {
        this.lookupcode = lookupcode;
    }

    public String getLookupnameth() {
        return lookupnameth;
    }

    public void setLookupnameth(String lookupnameth) {
        this.lookupnameth = lookupnameth;
    }

    public String getLookupnameen() {
        return lookupnameen;
    }

    public void setLookupnameen(String lookupnameen) {
        this.lookupnameen = lookupnameen;
    }

    public String getLookuptype() {
        return lookuptype;
    }

    public void setLookuptype(String lookuptype) {
        this.lookuptype = lookuptype;
    }

    public String getLookupvalue() {
        return lookupvalue;
    }

    public void setLookupvalue(String lookupvalue) {
        this.lookupvalue = lookupvalue;
    }

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }

    public String getAttr3() {
        return attr3;
    }

    public void setAttr3(String attr3) {
        this.attr3 = attr3;
    }

    public String getAttr4() {
        return attr4;
    }

    public void setAttr4(String attr4) {
        this.attr4 = attr4;
    }

    public String getAttr5() {
        return attr5;
    }

    public void setAttr5(String attr5) {
        this.attr5 = attr5;
    }

    public String getAttr6() {
        return attr6;
    }

    public void setAttr6(String attr6) {
        this.attr6 = attr6;
    }

    public String getAttr7() {
        return attr7;
    }

    public void setAttr7(String attr7) {
        this.attr7 = attr7;
    }

    public String getAttr8() {
        return attr8;
    }

    public void setAttr8(String attr8) {
        this.attr8 = attr8;
    }

    public String getAttr9() {
        return attr9;
    }

    public void setAttr9(String attr9) {
        this.attr9 = attr9;
    }

    public String getAttr10() {
        return attr10;
    }

    public void setAttr10(String attr10) {
        this.attr10 = attr10;
    }

    public Boolean getFlagedit() {
        return flagedit;
    }

    public void setFlagedit(Boolean flagedit) {
        this.flagedit = flagedit;
    }

    public Boolean getFlagcreate() {
        return flagcreate;
    }

    public void setFlagcreate(Boolean flagcreate) {
        this.flagcreate = flagcreate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Memlookup)) {
            return false;
        }
        Memlookup other = (Memlookup) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "th.co.d1.digitallending.entity.Memlookup[ uuid=" + uuid + " ]";
    }
    
}
