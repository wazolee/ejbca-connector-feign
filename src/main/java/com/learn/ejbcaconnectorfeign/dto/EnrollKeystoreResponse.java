package com.learn.ejbcaconnectorfeign.dto;

import lombok.Data;

@Data
public class EnrollKeystoreResponse {
    private String keystore;
    private String certificate;
}