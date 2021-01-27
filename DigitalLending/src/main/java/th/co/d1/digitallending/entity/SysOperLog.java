/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.entity;

import java.io.Serializable;
import java.math.BigInteger;
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

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 04-03-2020 2:03:12 PM
 */
@Entity
@Table(name = "t_sys_oper_log")
@NamedQueries({
    @NamedQuery(name = "SysOperLog.findAll", query = "SELECT s FROM SysOperLog s")})
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "trn_id")
    private String trnId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "source")
    private String source;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "product_id")
    private String productId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "product_code")
    private String productCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "product_version_id")
    private String productVersionId;
    @Size(max = 2147483647)
    @Column(name = "product_component_id")
    private String productComponentId;
    @Size(max = 128)
    @Column(name = "task_category")
    private String taskCategory;
    @Size(max = 255)
    @Column(name = "keywords")
    private String keywords;
    @Column(name = "trn_status")
    private Integer trnStatus;
    @Column(name = "trn_sub_status")
    private Integer trnSubStatus;
    @Size(max = 2147483647)
    @Column(name = "failure_reason")
    private String failureReason;
    @Size(max = 255)
    @Column(name = "source_device")
    private String sourceDevice;
    @Size(max = 100)
    @Column(name = "source_device_id")
    private String sourceDeviceId;
    @Size(max = 100)
    @Column(name = "source_cif_id")
    private String sourceCifId;
    @Size(max = 100)
    @Column(name = "account_name")
    private String accountName;
    @Column(name = "business_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date businessDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "ref_no")
    private String refNo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;
    @Size(max = 128)
    @Column(name = "prod_channel")
    private String prodChannel;
    @Size(max = 2147483647)
    @Column(name = "step_data")
    private String stepData;
    @Size(max = 128)
    @Column(name = "case_id")
    private String caseId;
    @Size(max = 128)
    @Column(name = "group_product")
    private String groupProduct;
    @Size(max = 128)
    @Column(name = "txn_no")
    private String txnNo;
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
    //@CreationTimestamp
    @Column(name = "create_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;
    @Size(max = 100)
    @Column(name = "create_by")
    private String createBy;
    @Column(name = "state_time")
    private BigInteger stateTime;
    @JoinColumn(name = "state_code", referencedColumnName = "uuid")
    @ManyToOne
    private ShelfLookup stateCode;

    public SysOperLog() {
    }

    public SysOperLog(String uuid) {
        this.uuid = uuid;
    }

    public SysOperLog(String uuid, String trnId, String source, String productId, String productCode, String productVersionId, String refNo, String paymentMethod, int status) {
        this.uuid = uuid;
        this.trnId = trnId;
        this.source = source;
        this.productId = productId;
        this.productCode = productCode;
        this.productVersionId = productVersionId;
        this.refNo = refNo;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTrnId() {
        return trnId;
    }

    public void setTrnId(String trnId) {
        this.trnId = trnId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductVersionId() {
        return productVersionId;
    }

    public void setProductVersionId(String productVersionId) {
        this.productVersionId = productVersionId;
    }

    public String getProductComponentId() {
        return productComponentId;
    }

    public void setProductComponentId(String productComponentId) {
        this.productComponentId = productComponentId;
    }

    public String getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(String taskCategory) {
        this.taskCategory = taskCategory;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getTrnStatus() {
        return trnStatus;
    }

    public void setTrnStatus(Integer trnStatus) {
        this.trnStatus = trnStatus;
    }

    public Integer getTrnSubStatus() {
        return trnSubStatus;
    }

    public void setTrnSubStatus(Integer trnSubStatus) {
        this.trnSubStatus = trnSubStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getSourceDevice() {
        return sourceDevice;
    }

    public void setSourceDevice(String sourceDevice) {
        this.sourceDevice = sourceDevice;
    }

    public String getSourceDeviceId() {
        return sourceDeviceId;
    }

    public void setSourceDeviceId(String sourceDeviceId) {
        this.sourceDeviceId = sourceDeviceId;
    }

    public String getSourceCifId() {
        return sourceCifId;
    }

    public void setSourceCifId(String sourceCifId) {
        this.sourceCifId = sourceCifId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getProdChannel() {
        return prodChannel;
    }

    public void setProdChannel(String prodChannel) {
        this.prodChannel = prodChannel;
    }

    public String getStepData() {
        return stepData;
    }

    public void setStepData(String stepData) {
        this.stepData = stepData;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getGroupProduct() {
        return groupProduct;
    }

    public void setGroupProduct(String groupProduct) {
        this.groupProduct = groupProduct;
    }

    public String getTxnNo() {
        return txnNo;
    }

    public void setTxnNo(String txnNo) {
        this.txnNo = txnNo;
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

    public BigInteger getStateTime() {
        return stateTime;
    }

    public void setStateTime(BigInteger stateTime) {
        this.stateTime = stateTime;
    }

    public ShelfLookup getStateCode() {
        return stateCode;
    }

    public void setStateCode(ShelfLookup stateCode) {
        this.stateCode = stateCode;
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
        if (!(object instanceof SysOperLog)) {
            return false;
        }
        SysOperLog other = (SysOperLog) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SysOperLog{" + "uuid=" + uuid + ", trnId=" + trnId + ", source=" + source + ", productId=" + productId + ", productCode=" + productCode + ", productVersionId=" + productVersionId + ", productComponentId=" + productComponentId + ", taskCategory=" + taskCategory + ", keywords=" + keywords + ", trnStatus=" + trnStatus + ", trnSubStatus=" + trnSubStatus + ", failureReason=" + failureReason + ", sourceDevice=" + sourceDevice + ", sourceDeviceId=" + sourceDeviceId + ", sourceCifId=" + sourceCifId + ", accountName=" + accountName + ", businessDate=" + businessDate + ", refNo=" + refNo + ", paymentMethod=" + paymentMethod + ", paymentDate=" + paymentDate + ", prodChannel=" + prodChannel + ", stepData=" + stepData + ", caseId=" + caseId + ", groupProduct=" + groupProduct + ", txnNo=" + txnNo + ", attr1=" + attr1 + ", attr2=" + attr2 + ", attr3=" + attr3 + ", attr4=" + attr4 + ", attr5=" + attr5 + ", attr6=" + attr6 + ", attr7=" + attr7 + ", attr8=" + attr8 + ", attr9=" + attr9 + ", attr10=" + attr10 + ", status=" + status + ", createAt=" + createAt + ", createBy=" + createBy + ", stateTime=" + stateTime + ", stateCode=" + stateCode + '}';
    }

}
