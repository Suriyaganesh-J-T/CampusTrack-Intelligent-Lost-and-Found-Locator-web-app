package com.campus.campus_backend.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PostDto {
    private String type;
    private String itemName;
    private String itemType;
    private String itemModel;
    private String place;
    private LocalDate dateReported;
}
