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
@Table(name = "t_shelf_product_dtl")
@NamedQueries({
    @NamedQuery(name = "ShelfProductDtl.findAll", query = "SELECT s FROM ShelfProductDtl s")})
public class ShelfProductDtl implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "uuid")
    private String uuid;
    @Size(min = 1, max = 128)
    @Column(name = "lk_uuid")
    private String lkUuid;
    @Size(max = 2147483647)
    @Column(name = "lk_code")
    private String lkCode;
    @Size(max = 2147483647)
    @Column(name = "lk_label")
    private String lkLabel;
    @Size(max = 2147483647)
    @Column(name = "lk_value")
    private String lkValue;
    @Column(name = "lk_require")
    private Boolean lkRequire;
    @Size(max = 2147483647)
    @Column(name = "lk_validation")
    private String lkValidation;
    @Size(max = 2147483647)
    @Column(name = "lk_description")
    private String lkDescription;
    @Size(max = 2147483647)
    @Column(name = "business_dept")
    private String businessDept;
    @Size(max = 2147483647)
    @Column(name = "company")
    private String company;
    @Size(max = 2147483647)
    @Column(name = "prod_type")
    private String prodType;
    @Size(max = 2147483647)
    @Column(name = "prod_url")
    private String prodUrl;
    @Column(name = "active_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activeDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Size(max = 2147483647)
    @Column(name = "prod_day")
    private String prodDay;
    @Size(max = 2147483647)
    @Column(name = "prod_time")
    private String prodTime;
    @Size(max = 2147483647)
    @Column(name = "campaign_id")
    private String campaignId;
    @Size(max = 2147483647)
    @Column(name = "campaign_name")
    private String campaignName;
    @Size(max = 2147483647)
    @Column(name = "link_channel")
    private String linkChannel;
    @Size(max = 2147483647)
    @Column(name = "product_channel")
    private String productChannel;
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
    @Column(name = "dtl_status")
    private Integer dtlStatus;
    @JoinColumn(name = "trn_uuid", referencedColumnName = "uuid")
    @ManyToOne(optional = false)
    private ShelfProductVcs trnUuid;

    public ShelfProductDtl() {
    }

    public ShelfProductDtl(String uuid) {
        this.uuid = uuid;
    }

    public ShelfProductDtl(String uuid, String lkUuid, int status, String createBy) {
        this.uuid = uuid;
        this.lkUuid = lkUuid;
        this.status = status;
        this.createBy = createBy;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLkUuid() {
        return lkUuid;
    }

    public void setLkUuid(String lkUuid) {
        this.lkUuid = lkUuid;
    }

    public String getLkCode() {
        return lkCode;
    }

    public void setLkCode(String lkCode) {
        this.lkCode = lkCode;
    }

    public String getLkLabel() {
        return lkLabel;
    }

    public void setLkLabel(String lkLabel) {
        this.lkLabel = lkLabel;
    }

    public String getLkValue() {
        return lkValue;
    }

    public void setLkValue(String lkValue) {
        this.lkValue = lkValue;
    }

    public Boolean getLkRequire() {
        return lkRequire;
    }

    public void setLkRequire(Boolean lkRequire) {
        this.lkRequire = lkRequire;
    }

    public String getLkValidation() {
        return lkValidation;
    }

    public void setLkValidation(String lkValidation) {
        this.lkValidation = lkValidation;
    }

    public String getLkDescription() {
        return lkDescription;
    }

    public void setLkDescription(String lkDescription) {
        this.lkDescription = lkDescription;
    }

    public String getBusinessDept() {
        return businessDept;
    }

    public void setBusinessDept(String businessDept) {
        this.businessDept = businessDept;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProdType() {
        return prodType;
    }

    public void setProdType(String prodType) {
        this.prodType = prodType;
    }

    public String getProdUrl() {
        return prodUrl;
    }

    public void setProdUrl(String prodUrl) {
        this.prodUrl = prodUrl;
    }

    public Date getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getProdDay() {
        return prodDay;
    }

    public void setProdDay(String prodDay) {
        this.prodDay = prodDay;
    }

    public String getProdTime() {
        return prodTime;
    }

    public void setProdTime(String prodTime) {
        this.prodTime = prodTime;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getLinkChannel() {
        return linkChannel;
    }

    public void setLinkChannel(String linkChannel) {
        this.linkChannel = linkChannel;
    }

    public String getProductChannel() {
        return productChannel;
    }

    public void setProductChannel(String productChannel) {
        this.productChannel = productChannel;
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

    public Integer getDtlStatus() {
        return dtlStatus;
    }

    public void setDtlStatus(Integer dtlStatus) {
        this.dtlStatus = dtlStatus;
    }

    public ShelfProductVcs getTrnUuid() {
        return trnUuid;
    }

    public void setTrnUuid(ShelfProductVcs trnUuid) {
        this.trnUuid = trnUuid;
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
        if (!(object instanceof ShelfProductDtl)) {
            return false;
        }
        ShelfProductDtl other = (ShelfProductDtl) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "th.co.d1.digitallending.entity.ShelfProductDtl[ uuid=" + uuid + " ]";
    }

}
