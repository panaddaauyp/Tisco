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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author Poomsakul Senakul
 */
@Entity
@Table(name = "t_sys_role")
@NamedQueries({
    @NamedQuery(name = "SysRole.findAll", query = "SELECT s FROM SysRole s")})
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "uuid")
    private String uuid;
    @Size(max = 256)
    @Column(name = "role_id")
    private String roleId;
    @Size(max = 256)
    @Column(name = "role_code")
    private String roleCode;
    @Size(max = 256)
    @Column(name = "role_name_th")
    private String roleNameTh;
    @Size(max = 256)
    @Column(name = "role_name_en")
    private String roleNameEn;
    @Size(max = 2147483647)
    @Column(name = "pemission")
    private String pemission;
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
    @Column(name = "status")
    private Integer status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "create_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "create_by")
    private String createBy;

    public SysRole() {
    }

    public SysRole(String uuid) {
        this.uuid = uuid;
    }

    public SysRole(String uuid, Date createAt, String createBy) {
        this.uuid = uuid;
        this.createAt = createAt;
        this.createBy = createBy;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleNameTh() {
        return roleNameTh;
    }

    public void setRoleNameTh(String roleNameTh) {
        this.roleNameTh = roleNameTh;
    }

    public String getRoleNameEn() {
        return roleNameEn;
    }

    public void setRoleNameEn(String roleNameEn) {
        this.roleNameEn = roleNameEn;
    }

    public String getPemission() {
        return pemission;
    }

    public void setPemission(String pemission) {
        this.pemission = pemission;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SysRole)) {
            return false;
        }
        SysRole other = (SysRole) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "th.co.d1.digitallending.entity.SysRole[ uuid=" + uuid + " ]";
    }
    
}
