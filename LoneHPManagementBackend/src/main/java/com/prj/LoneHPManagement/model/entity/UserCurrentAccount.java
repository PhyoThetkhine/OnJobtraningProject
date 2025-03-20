package com.prj.LoneHPManagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_current_account")
public class UserCurrentAccount extends CurrentAccountBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
    @Column(name = "is_freeze", nullable = false)
    @JsonIgnore
    private int isFreeze;
    @JsonProperty("isFreeze")
    public String getIsFreezeDescription() {
        ConstraintEnum constraint = ConstraintEnum.fromCode(isFreeze);
        return constraint != null ? constraint.getDescription() : "unknown";
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
