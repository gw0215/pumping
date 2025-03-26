package com.pumping.domain.media.fixture;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.media.model.Media;

public abstract class MediaFixture {

    private static String FILE_NAME = "filename";

    private static String FILE_TYPE = "filetype";

    private static byte[] IMAGE = new byte[]{1, 2, 3, 4, 5};

    public static Media createMedia(Board board) {
        return new Media(board, FILE_NAME, FILE_TYPE, IMAGE);
    }

}
