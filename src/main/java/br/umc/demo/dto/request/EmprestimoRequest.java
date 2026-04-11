package br.umc.demo.dto.request;

import lombok.Data;

@Data
public class EmprestimoRequest {
    private String leitorId;
    private String bookId;
    private String bibliotecarioId;
}
