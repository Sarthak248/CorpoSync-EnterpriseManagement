package com.sarthak.project.enterpriseMgmtSys.entity;

import java.util.Collection; 
import java.util.List;
import javax.persistence.Column; 
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id; 
import javax.persistence.Table; 
import org.springframework.security.core.GrantedAuthority; 
import org.springframework.security.core.userdetails.UserDetails;

@Entity 
@Table(name = "users") 
public class User implements UserDetails { 
   private static final long serialVersionUID = 1L;

   @Id 
   @GeneratedValue(strategy = GenerationType.IDENTITY) 
   @Column(name = "userId", nullable = false)
   private int userId;
   
   @Column(name = "username", nullable = false)
   private String username;
   
   @Column(name = "email", nullable = false)
   private String email;
   
   @Column(name = "password", nullable = false)
   private String password; 
   
   @Column(name = "account_non_locked", nullable = false)
   private boolean accountNonLocked; 
   
   @Column(name = "createdOn", nullable = false)
   private String createdOn; 
   
   @Column(name = "updatedOn")
   private String updatedOn; 
   
   public User() { 
   } 
   public User(String username, String password, boolean accountNonLocked) { 
      this.username = username; 
      this.password = password; 
      this.accountNonLocked = accountNonLocked; 
   } 
   @Override 
   public Collection<? extends GrantedAuthority> getAuthorities() { 
      return List.of(() -> "read"); 
   }
   public int getUserId() {    
      return userId; 
   } 
   public void setUserId(int userId) { 
      this.userId = userId; 
   } 
   @Override
   public String getPassword() {    
      return password; 
   } 
   public void setPassword(String password) { 
      this.password = password; 
   }
   public String getEmail() {    
      return email; 
   } 
   public void setEmail(String email) { 
      this.email = email; 
   }
   @Override 
   public String getUsername() { 
      return username; 
   } 
   public void setUsername(String username) { 
      this.username = username; 
   } 
   @Override 
   public boolean isAccountNonExpired() { 
      return true; 
   } 
   @Override
   public boolean isAccountNonLocked() { 
      return accountNonLocked; 
   } 
   @Override public boolean isCredentialsNonExpired() { 
      return true; 
   } 
   @Override public boolean isEnabled() { 
   return true; 
   } 
   
   public void setAccountNonLocked(Boolean accountNonLocked) { 
      this.accountNonLocked = accountNonLocked; 
   } 
   public boolean getAccountNonLocked() { 
      return accountNonLocked; 
   }
   
   public void setCreatedOn(String createdOn) { 
      this.createdOn = createdOn; 
   } 
   public String getCreatedOn() { 
      return createdOn; 
   }
   
   public void setUpdatedOn(String updatedOn) { 
      this.updatedOn = updatedOn; 
   } 
   public String getUpdatedOn() { 
      return updatedOn; 
   }
}
