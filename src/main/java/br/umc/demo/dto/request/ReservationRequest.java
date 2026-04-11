package br.umc.demo.dto.request;

import lombok.Data;

@Data
public class ReservationRequest {
    private String leitorId;
    private String bookId;
}
