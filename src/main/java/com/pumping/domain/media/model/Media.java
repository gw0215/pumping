package com.pumping.domain.media.model;

import com.pumping.domain.board.model.Board;
import jakarta.persistence.*;

@Entity
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    private String fileName;

    private String fileType;

    @Lob
    private byte[] data;  // Blob 데이터 (이진 데이터)

    public Media(Board board, String fileName, String fileType, byte[] data) {
        this.board = board;
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }
}
