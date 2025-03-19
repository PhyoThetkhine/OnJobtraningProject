package com.prj.LoneHPManagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_permission")
public class UserPermission {
   @EmbeddedId
   private UserPermissionPK id;
   @ManyToOne
   @MapsId("userId")
   @JoinColumn(name = "user_id")
   private User user;
   @ManyToOne
   @MapsId("permissionId")
   @JoinColumn(name = "permission_id")
   private Permission permission;
   @Column(name = "isAllowed", nullable = false)
   @JsonIgnore
   private int isAllowed;
   @Column(name = "allowed_date", nullable = false)
   private LocalDateTime allowedDate;
   @ManyToOne
   @JoinColumn(name = "allowed_user_id", nullable = false)
   private User allowedUser;
   @Column(name = "limited_date", nullable = false)
   private LocalDateTime limitedDate;

   public UserPermissionPK getId() {
      return id;
   }

   public void setId(UserPermissionPK id) {
      this.id = id;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public Permission getPermission() {
      return permission;
   }

   public void setPermission(Permission permission) {
      this.permission = permission;
   }

   public int getIsAllowed() {
      return isAllowed;
   }

   public void setIsAllowed(int isAllowed) {
      this.isAllowed = isAllowed;
   }

   public LocalDateTime getAllowedDate() {
      return allowedDate;
   }

   public void setAllowedDate(LocalDateTime allowedDate) {
      this.allowedDate = allowedDate;
   }

   public User getAllowedUser() {
      return allowedUser;
   }

   public void setAllowedUser(User allowedUser) {
      this.allowedUser = allowedUser;
   }

   public LocalDateTime getLimitedDate() {
      return limitedDate;
   }

   public void setLimitedDate(LocalDateTime limitedDate) {
      this.limitedDate = limitedDate;
   }

   @JsonProperty("isAllowed")
   public String getIsAllowedDescription() {
      ConstraintEnum constraint = ConstraintEnum.fromCode(isAllowed);
      return constraint != null ? constraint.getDescription() : "unknown";
   }
   @Embeddable
   public static class UserPermissionPK implements Serializable {
      private Integer userId;
      private Integer permissionId;

      public UserPermissionPK() {
      }

      public UserPermissionPK(Integer userId, Integer permissionId) {
         this.userId = userId;
         this.permissionId = permissionId;
      }

      public Integer getPermissionId() {
         return permissionId;
      }

      public void setPermissionId(Integer permissionId) {
         this.permissionId = permissionId;
      }

      public Integer getUserId() {
         return userId;
      }

      public void setUserId(Integer userId) {
         this.userId = userId;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      UserPermission that = (UserPermission) o;
      return isAllowed == that.isAllowed && Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(permission, that.permission) && Objects.equals(allowedDate, that.allowedDate) && Objects.equals(allowedUser, that.allowedUser) && Objects.equals(limitedDate, that.limitedDate);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, user, permission, isAllowed, allowedDate, allowedUser, limitedDate);
   }
}
