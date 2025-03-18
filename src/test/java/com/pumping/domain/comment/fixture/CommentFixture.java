package com.pumping.domain.comment.fixture;

import com.pumping.domain.comment.dto.CommentRequest;

public abstract class CommentFixture {

    private static String COMMENT = "comment";

    public static CommentRequest createCommentRequest() {
        return new CommentRequest(COMMENT);
    }

}
