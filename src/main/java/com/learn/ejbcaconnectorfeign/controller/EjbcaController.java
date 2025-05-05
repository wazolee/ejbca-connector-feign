package com.learn.ejbcaconnectorfeign.controller;

import com.learn.ejbcaconnectorfeign.dto.EnrollKeystoreRequest;
import com.learn.ejbcaconnectorfeign.dto.EnrollKeystoreResponse;
import com.learn.ejbcaconnectorfeign.service.EjbcaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ejbca")
public class EjbcaController {

    private final EjbcaService ejbcaService;

    public EjbcaController(EjbcaService ejbcaService) {
        this.ejbcaService = ejbcaService;
    }

    @PostMapping("/enroll-keystore")
    public Mono<EnrollKeystoreResponse> enrollKeystore(@RequestBody EnrollKeystoreRequest request) {
        return ejbcaService.enrollKeystore(request);
    }
}