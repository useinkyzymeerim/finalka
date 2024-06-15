package com.finalka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuDetailsDto {
    private Long id;
    private String nameOfMenu;

    private String createdBy;
    private Timestamp createdAt;
}
