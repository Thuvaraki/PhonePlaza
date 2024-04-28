package com.example.PhonePlaza.DTO;

import lombok.Data;


@Data
public class ViewProfileResponseDTO {
    private Long userId;

    private String userName;

    private  String email;
}
