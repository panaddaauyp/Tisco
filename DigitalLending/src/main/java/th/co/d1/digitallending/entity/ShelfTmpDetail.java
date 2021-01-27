/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author Poomsakul Senakul
 */
@Entity
@Table(name = "t_shelf_tmp_detail")
@NamedQueries({
    @NamedQuery(name = "ShelfTmpDetail.findAll", query = "SELECT s FROM ShelfTmpDetail s")})
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ShelfTmpDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "uuid")
    private String uuid;
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
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "seq_no")
    private int seqNo;
    //    @Lob
    @Type(type = "jsonb")
    @Column(name = "value", columnDefinition = "jsonb")
    private Object value;
    @Column(name = "flag_enable")
    private Boolean flagEnable;
    @JoinColumn(name = "comp_uuid", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private ShelfComp compUuid;
    @JoinColumn(name = "att_uuid", referencedColumnName = "uuid")
    @ManyToOne
    private ShelfTmpAttach attUuid;
    @JoinColumn(name = "vcs_uuid", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private ShelfTmpVcs vcsUuid;
    @JoinColumn(name = "lookup_uuid", referencedColumnName = "uuid")
    @ManyToOne
    private SysLookup lookupUuid;

    public ShelfTmpDetail() {
    }

    public ShelfTmpDetail(String uuid) {
        this.uuid = uuid;
    }

    public ShelfTmpDetail(String uuid, int status, String createBy, int seqNo) {
        this.uuid = uuid;
        this.status = status;
        this.createBy = createBy;
        this.seqNo = seqNo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Boolean getFlagEnable() {
        return flagEnable;
    }

    public void setFlagEnable(Boolean flagEnable) {
        this.flagEnable = flagEnable;
    }

    public ShelfComp getCompUuid() {
        return compUuid;
    }

    public void setCompUuid(ShelfComp compUuid) {
        this.compUuid = compUuid;
    }

    public ShelfTmpAttach getAttUuid() {
        return attUuid;
    }

    public void setAttUuid(ShelfTmpAttach attUuid) {
        this.attUuid = attUuid;
    }

    public ShelfTmpVcs getVcsUuid() {
        return vcsUuid;
    }

    public void setVcsUuid(ShelfTmpVcs vcsUuid) {
        this.vcsUuid = vcsUuid;
    }

    public SysLookup getLookupUuid() {
        return lookupUuid;
    }

    public void setLookupUuid(SysLookup lookupUuid) {
        this.lookupUuid = lookupUuid;
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
        if (!(object instanceof ShelfTmpDetail)) {
            return false;
        }
        ShelfTmpDetail other = (ShelfTmpDetail) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "th.co.d1.digitallending.entity.ShelfTmpDetail[ uuid=" + uuid + " ]";
    }
    
}
