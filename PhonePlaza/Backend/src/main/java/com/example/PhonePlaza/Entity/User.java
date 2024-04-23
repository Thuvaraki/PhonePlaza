package com.example.PhonePlaza.Entity;

import com.example.PhonePlaza.Common.Constant;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    private String userName;

    @NotBlank
    private  String email;

    @NotBlank
    private String password;


    private String userType = Constant.USER_TYPE.USER;

    private String verificationCode;

    private Date verificationCodeExpiryTime;

    private boolean verified; //field is a boolean, it's implicitly set to false by default

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    public void  onSave(){
        Date currentDateTime=new Date();
        this.createdAt=currentDateTime;
        this.updatedAt=currentDateTime;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = new Date();
    }
}
