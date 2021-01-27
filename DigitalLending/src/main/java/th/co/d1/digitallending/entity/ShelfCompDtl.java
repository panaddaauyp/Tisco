/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package th.co.d1.digitallending.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author Chalermpol
 */
@Entity
@Table(name = "t_shelf_comp_dtl")
@NamedQueries({
    @NamedQuery(name = "ShelfCompDtl.findAll", query = "SELECT s FROM ShelfCompDtl s")})
public class ShelfCompDtl implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "uuid")
    private String uuid;
    @Size(max = 2147483647)
    @Column(name = "label_text")
    private String labelText;
    @Size(max = 2147483647)
    @Column(name = "data_value")
    private String dataValue;
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    @Size(max = 2147483647)
    @Column(name = "attr1")
    private String attr1;
    @Size(max = 2147483647)
    @Column(name = "attr2")
    private String attr2;
    @Size(max = 2147483647)
    @Column(name = "attr3")
    private String attr3;
    @Size(max = 2147483647)
    @Column(name = "attr4")
    private String attr4;
    @Size(max = 2147483647)
    @Column(name = "attr5")
    private String attr5;
    @Size(max = 2147483647)
    @Column(name = "attr6")
    private String attr6;
    @Size(max = 2147483647)
    @Column(name = "attr7")
    private String attr7;
    @Size(max = 2147483647)
    @Column(name = "attr8")
    private String attr8;
    @Size(max = 2147483647)
    @Column(name = "attr9")
    private String attr9;
    @Size(max = 2147483647)
    @Column(name = "attr10")
    private String attr10;
    @Column(name = "seq")
    private Integer seq;
    @Column(name = "parent")
    private Integer parent;
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private int status;
    @CreationTimestamp
    @Column(name = "create_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "create_by")
    private String createBy;
    @UpdateTimestamp
    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;
    @Size(max = 100)
    @Column(name = "update_by")
    private String updateBy;
    @Size(max = 2147483647)
    @Column(name = "ele_id")
    private String eleId;
    @Column(name = "require")
    private Boolean require;
    @Size(max = 2147483647)
    @Column(name = "validation")
    private String validation;
    @Size(max = 2147483647)
    @Column(name = "pattern")
    private String pattern;
    @Size(max = 2147483647)
    @Column(name = "table_column")
    private String tableColumn;
    @JoinColumn(name = "comp_uuid", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private ShelfComp compUuid;
    @JoinColumn(name = "lk_uuid", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private ShelfLookup lkUuid;

    public ShelfCompDtl() {
    }

    public ShelfCompDtl(String uuid) {
        this.uuid = uuid;
    }

    public ShelfCompDtl(String uuid, int status, String createBy) {
        this.uuid = uuid;
        this.status = status;
        this.createBy = createBy;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getEleId() {
        return eleId;
    }

    public void setEleId(String eleId) {
        this.eleId = eleId;
    }

    public Boolean getRequire() {
        return require;
    }

    public void setRequire(Boolean require) {
        this.require = require;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getTableColumn() {
        return tableColumn;
    }

    public void setTableColumn(String tableColumn) {
        this.tableColumn = tableColumn;
    }

    public ShelfComp getCompUuid() {
        return compUuid;
    }

    public void setCompUuid(ShelfComp compUuid) {
        this.compUuid = compUuid;
    }

    public ShelfLookup getLkUuid() {
        return lkUuid;
    }

    public void setLkUuid(ShelfLookup lkUuid) {
        this.lkUuid = lkUuid;
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
        if (!(object instanceof ShelfCompDtl)) {
            return false;
        }
        ShelfCompDtl other = (ShelfCompDtl) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "th.co.d1.digitallending.entity.ShelfCompDtl[ uuid=" + uuid + " ]";
    }

}
