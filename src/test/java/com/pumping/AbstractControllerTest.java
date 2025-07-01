package com.pumping;

import com.pumping.domain.board.controller.BoardController;
import com.pumping.domain.board.service.BoardService;
import com.pumping.domain.comment.controller.CommentController;
import com.pumping.domain.comment.service.CommentService;
import com.pumping.domain.emailverification.controller.EmailVerificationController;
import com.pumping.domain.emailverification.service.EmailVerificationService;
import com.pumping.domain.inbody.controller.InBodyController;
import com.pumping.domain.inbody.service.InBodyService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = {BoardController.class, CommentController.class, InBodyController.class, EmailVerificationController.class})
public class AbstractControllerTest {

    @MockitoBean
    protected BoardService boardService;

    @MockitoBean
    protected CommentService commentService;

    @MockitoBean
    protected InBodyService inBodyService;

    @MockitoBean
    protected EmailVerificationService emailVerificationService;

}
