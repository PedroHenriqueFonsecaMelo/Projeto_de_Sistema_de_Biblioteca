package br.umc.demo.dto;

import lombok.Data;

@Data
public class LoanRequest {
    private String leitorId;
    private String bookId;
    private String bibliotecarioId;
}
