package com.learn.ejbcaconnectorfeign.dto;

import lombok.Data;

@Data
public class EnrollKeystoreRequest {
    private String username;
    private String password;
    private String keyAlg;
    private String keySpec;
}