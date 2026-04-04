package br.umc.demo.dto;

import lombok.Data;

@Data
public class ReservationRequest {
    private String leitorId;
    private String bookId;
}
