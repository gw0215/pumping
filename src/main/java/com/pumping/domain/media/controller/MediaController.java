package com.pumping.domain.media.controller;

import com.pumping.domain.media.model.Media;
import com.pumping.domain.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/boards/{boardId}/media")
    @ResponseStatus(HttpStatus.CREATED)
    public void upload(
            @PathVariable("boardId") Long boardId,
            @RequestParam("file") MultipartFile file
    ) {
        mediaService.save(boardId, file);
    }

    @GetMapping(value = "/media/{mediaId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] findById(
            @PathVariable("mediaId") Long mediaId
    ) {
        return mediaService.findById(mediaId);
    }

}
