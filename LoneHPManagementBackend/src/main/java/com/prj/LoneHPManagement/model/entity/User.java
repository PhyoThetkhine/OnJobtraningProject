package com.prj.LoneHPManagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User extends UserCIFBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "user_code", nullable = false, unique = true, length = 50)
    private String userCode;
    @Column(name = "password", nullable = false, unique = true, length = 200)
    private String password;
    @Column(name = "status", nullable = false)
    @JsonIgnore
    private int status;
    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // This getter will expose a descriptive status in the JSON response.
    @JsonProperty("status")
    public String getStatusDescription() {
        ConstraintEnum constraint = ConstraintEnum.fromCode(status);
        return constraint != null ? constraint.getDescription() : "unknown";
    }
    @PrePersist
    protected void prePersist() {
        if (status == 0) status = ConstraintEnum.ACTIVE.getCode(); // Default status
    }
}
