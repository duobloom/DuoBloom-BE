package POT.DuoBloom.user.service;


import POT.DuoBloom.user.UserRepository;
import POT.DuoBloom.user.dto.PasswordDto;
import POT.DuoBloom.user.dto.SignupUserDto;
import POT.DuoBloom.user.dto.UserProfileDto;
import POT.DuoBloom.user.dto.UserProfileEditDto;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다: " + userId));
    }

    // 회원가입 로직
    @Transactional
    public void signup(SignupUserDto signupUserDto) {

        if (userRepository.existsByEmail(signupUserDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.updateNickName(signupUserDto.getNickname());
        user.updateEmail(signupUserDto.getEmail());
        user.updateBirth(signupUserDto.getBirth());
        user.updateSex(signupUserDto.getSex());

        // balance를 초기값 0으로 설정
        user.updateBalance(0);

        String encodedPassword = passwordEncoder.encode(signupUserDto.getPassword());
        user.updatePassword(encodedPassword);

        userRepository.save(user);
    }



    // 로그인 인증 로직
    @Transactional(readOnly = true)
    public Long authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 유저가 없습니다."));

        // 입력된 비밀번호와 해시된 비밀번호 비교
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 인증 성공 시 유저 ID 반환
        return user.getUserId();
    }

    // 아이디로 프로필 정보 조회
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 아이디를 찾을 수 없음: " + userId));
    }

    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 아이디를 찾을 수 없음: " + userId));
        return new UserProfileDto(
                user.getNickname(),
                user.getEmail(),
                user.getSex(),
                user.getBirth(),
                user.getBalance()
        );
    }

    // 프로필 수정
    @Transactional
    public void updateUserProfile(Long userId, UserProfileEditDto userProfileEditDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 아이디를 찾을 수 없음: " + userId));

        user.updateNickName(userProfileEditDto.getNickname());
    }

    // 현재 비밀번호 확인
    @Transactional(readOnly = true)
    public boolean verifyPassword(Long userId, PasswordDto passwordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 입력된 비밀번호와 해시된 비밀번호 비교
        return passwordEncoder.matches(passwordDto.getPassword(), user.getPassword());
    }


    // 비밀번호 수정
    @Transactional
    public void updatePassword(Long userId, PasswordDto passwordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 입력받은 비밀번호를 암호화
        String encodedPassword = passwordEncoder.encode(passwordDto.getPassword());

        // 암호화된 비밀번호로 업데이트
        user.updatePassword(encodedPassword);

        // 업데이트된 유저 정보를 저장
        userRepository.save(user);
    }




}
