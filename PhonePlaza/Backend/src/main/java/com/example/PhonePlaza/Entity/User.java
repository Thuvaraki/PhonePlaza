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

    private Date createdAt;

    private Date updatedAt;

    @PrePersist
    public void  onSave(){
        Date currentDateTime=new Date();
        this.createdAt=currentDateTime;
        this.updatedAt=currentDateTime;
    }

    @PostPersist
    public void onUpdate(){
        Date currentDateTime=new Date();
        this.updatedAt=currentDateTime;
    }
}
