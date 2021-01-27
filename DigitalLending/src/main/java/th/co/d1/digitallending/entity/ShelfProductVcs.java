/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package th.co.d1.digitallending.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "t_shelf_product_vcs")
@NamedQueries({
    @NamedQuery(name = "ShelfProductVcs.findAll", query = "SELECT s FROM ShelfProductVcs s")})
public class ShelfProductVcs implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "tem_uuid")
    private String temUuid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ver_comp")
    private int verComp;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ver_tem")
    private int verTem;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ver_prod")
    private int verProd;
    @Column(name = "effective_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "state")
    private String state;
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
    @Column(name = "comp_status")
    private Integer compStatus;
    @JoinColumn(name = "comp_uuid", referencedColumnName = "uuid")
    @ManyToOne
    private ShelfComp compUuid;
    @JoinColumn(name = "prod_uuid", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private ShelfProduct prodUuid;
    @JoinColumn(name = "theme_uuid", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private ShelfTheme themeUuid;
    @OneToMany(mappedBy = "trnUuid")
    private List<ShelfProductDtl> shelfProductDtlList;

    public ShelfProductVcs() {
    }

    public ShelfProductVcs(String uuid) {
        this.uuid = uuid;
    }

    public ShelfProductVcs(String uuid, String temUuid, int verComp, int verTem, int verProd, String state, int status, String createBy) {
        this.uuid = uuid;
        this.temUuid = temUuid;
        this.verComp = verComp;
        this.verTem = verTem;
        this.verProd = verProd;
        this.state = state;
        this.status = status;
        this.createBy = createBy;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTemUuid() {
        return temUuid;
    }

    public void setTemUuid(String temUuid) {
        this.temUuid = temUuid;
    }

    public int getVerComp() {
        return verComp;
    }

    public void setVerComp(int verComp) {
        this.verComp = verComp;
    }

    public int getVerTem() {
        return verTem;
    }

    public void setVerTem(int verTem) {
        this.verTem = verTem;
    }

    public int getVerProd() {
        return verProd;
    }

    public void setVerProd(int verProd) {
        this.verProd = verProd;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public Integer getCompStatus() {
        return compStatus;
    }

    public void setCompStatus(Integer compStatus) {
        this.compStatus = compStatus;
    }

    public ShelfComp getCompUuid() {
        return compUuid;
    }

    public void setCompUuid(ShelfComp compUuid) {
        this.compUuid = compUuid;
    }

    public ShelfProduct getProdUuid() {
        return prodUuid;
    }

    public void setProdUuid(ShelfProduct prodUuid) {
        this.prodUuid = prodUuid;
    }

    public ShelfTheme getThemeUuid() {
        return themeUuid;
    }

    public void setThemeUuid(ShelfTheme themeUuid) {
        this.themeUuid = themeUuid;
    }

    public List<ShelfProductDtl> getShelfProductDtlList() {
        return shelfProductDtlList;
    }

    public void setShelfProductDtlList(List<ShelfProductDtl> shelfProductDtlList) {
        this.shelfProductDtlList = shelfProductDtlList;
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
        if (!(object instanceof ShelfProductVcs)) {
            return false;
        }
        ShelfProductVcs other = (ShelfProductVcs) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "th.co.d1.digitallending.entity.ShelfProductVcs[ uuid=" + uuid + " ]";
    }

}
