package br.umc.demo.dto.request;

import lombok.Data;

@Data
public class ReservaRequest {
    private String leitorId;
    private String bookId;
}
