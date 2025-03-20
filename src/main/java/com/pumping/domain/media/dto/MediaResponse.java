package com.pumping.domain.media.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MediaResponse {

    private Long id;
    private String fileName;
    private String fileType;

    public MediaResponse(Long id, String fileName, String fileType) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
    }
}
