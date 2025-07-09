package tobyspring.splearn.application.member.provided;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.member.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration.class)
record MemberRegisterTest(MemberRegister memberRegister, EntityManager entityManager) {

    @Test
    void register() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void duplicateEmailFail() {
        memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void activate() {
        Member member = registerMember();

        member = memberRegister.activate(member.getId());

        entityManager.flush();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getDetail().getActivatedAt()).isNotNull();
    }

    @Test
    void deactivate() {
        Member member = registerMember();

        member = memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();

        member = memberRegister.deactivate(member.getId());

        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
    }

    @Test
    void updateInfo() {
        Member member = registerMember();

        memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();

        member = memberRegister.updateInfo(
                member.getId(),
                new MemberInfoUpdateRequest("Lucky", "lucky0507", "자기소개")
        );

        assertThat(member.getDetail().getProfile().address()).isEqualTo("lucky0507");
    }

    @DisplayName("이미 존재하는 프로필 주소로 회원 정보 수정 시 예외 발생")
    @Test
    void updateInfoFail() {
        Member member = registerMember();
        memberRegister.activate(member.getId());
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("Lucky", "lucky0507", "자기소개"));

        Member member2 = registerMember("other@gmail.com");
        memberRegister.activate(member2.getId());
        entityManager.flush();
        entityManager.clear();

        // member2는 기존의 member와 같은 프로필 주소를 사용할 수 없다.
        assertThatThrownBy(() -> memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("Lucky", "lucky0507", "자기소개")
        )).isInstanceOf(DuplicateProfileException.class);
        // 다른 프로필 주소로는 수정이 가능하다.
        memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("Lucky", "lucky1234", "자기소개"));

        // 기존 프로필 주소로 다시 수정할 수 있다.
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("Lucky", "lucky0000", "자기소개"));
        // 프로필 주소를 제거할 수 있다.
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("Lucky", "", "자기소개"));
        // 프로필 주소 중복은 허용하지 않는다.
        assertThatThrownBy(() -> memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("Lucky", "lucky1234", "자기소개")
        )).isInstanceOf(DuplicateProfileException.class);

        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("Lucky", "", "자기소개"));
        memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("Lucky", "", "자기소개"));
    }

    @Test
    void memberRegisterRequestFail() {
        checkValidation(new MemberRegisterRequest("test@gmail.com", "name", "correct_secret"));
        checkValidation(new MemberRegisterRequest("test@gmail.com", "nickname", "secret"));
        checkValidation(new MemberRegisterRequest("test@gmail.com", "nickname----------------------", "correct_secret"));
        checkValidation(new MemberRegisterRequest("testgmail.com", "name", "secret"));
    }

    private Member registerMember() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();
        entityManager.clear();
        return member;
    }

    private Member registerMember(String email) {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest(email));
        entityManager.flush();
        entityManager.clear();
        return member;
    }

    private void checkValidation(MemberRegisterRequest invalid) {
        assertThatThrownBy(() -> memberRegister.register(invalid))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
