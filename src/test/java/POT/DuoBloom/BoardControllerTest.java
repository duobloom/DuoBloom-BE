//package POT.DuoBloom;
//
//import POT.DuoBloom.board.controller.BoardController;
//import POT.DuoBloom.board.dto.BoardRequestDto;
//import POT.DuoBloom.board.dto.BoardResponseDto;
//import POT.DuoBloom.board.entity.Board;
//import POT.DuoBloom.board.service.BoardService;
//import POT.DuoBloom.user.entity.User;
//import POT.DuoBloom.user.service.UserService;
//import jakarta.servlet.http.HttpSession;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import java.time.LocalDateTime;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//public class BoardControllerTest {
//
//    @InjectMocks
//    private BoardController boardController;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private BoardService boardService;
//
//    @Mock
//    private HttpSession session;
//
//    private User user;
//    private User coupleUser;
//    private User nonCoupleUser;
//    private Board board;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // 가상 유저 및 커플 유저 생성
//        user = new User();
//        user.updateUserId(1L);
//        user.updateNickName("toni");
//
//        coupleUser = new User();
//        coupleUser.updateUserId(2L);
//        coupleUser.updateNickName("potato");
//
//        nonCoupleUser = new User();
//        nonCoupleUser.updateUserId(3L);
//        nonCoupleUser.updateNickName("notCouple");
//
//        // user와 coupleUser를 서로 커플로 설정
//        user.setCoupleUser(coupleUser);
//        coupleUser.setCoupleUser(user);
//
//        // 테스트용 게시글 생성
//        board = new Board(user, "Test Title", "Test Content", LocalDateTime.now());
//
//        // 세션에 userId 설정
//        when(session.getAttribute("userId")).thenReturn(user.getUserId());
//        when(userService.findById(user.getUserId())).thenReturn(user);
//    }
//
//    @Test
//    public void testCreateBoardByCoupleUser() {
//        // 커플 유저가 글 작성 가능 테스트
//        BoardRequestDto boardRequestDto = new BoardRequestDto("Test Title", "Test Content");
//
//        when(boardService.createBoard(any(User.class), eq("Test Title"), eq("Test Content"))).thenReturn(board);
//
//        ResponseEntity<BoardResponseDto> response = boardController.createBoard(boardRequestDto, session);
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody().getBoardId()).isEqualTo(board.getBoardId());
//    }
//
//    @Test
//    public void testAccessBoardByCoupleUser() {
//        // 커플 유저가 작성한 글에 커플로 연결된 유저가 접근 가능
//        when(session.getAttribute("userId")).thenReturn(coupleUser.getUserId());
//        when(userService.findById(coupleUser.getUserId())).thenReturn(coupleUser);
//        when(boardService.getBoardById(board.getBoardId())).thenReturn(Optional.of(board));
//        when(boardService.canAccessBoard(coupleUser)).thenReturn(true);
//
//        ResponseEntity<BoardResponseDto> response = boardController.getBoardById(board.getBoardId(), session);
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody().getBoardId()).isEqualTo(board.getBoardId());
//    }
//
//    @Test
//    public void testAccessBoardByNonCoupleUser() {
//        // 커플 관계가 아닌 유저가 글에 접근하려고 하면 예외 발생 예상
//        when(session.getAttribute("userId")).thenReturn(nonCoupleUser.getUserId());
//        when(userService.findById(nonCoupleUser.getUserId())).thenReturn(nonCoupleUser);
//
//        // 비커플 유저가 접근 시 접근 제한 예외 발생
//        when(boardService.getBoardById(board.getBoardId())).thenReturn(Optional.of(board));
//        when(boardService.canAccessBoard(nonCoupleUser)).thenReturn(false);
//
//        assertThrows(IllegalStateException.class, () -> {
//            boardController.getBoardById(board.getBoardId(), session);
//        });
//    }
//}
