package tobyspring.splearn.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;
import tobyspring.splearn.domain.AbstractEntity;

import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail extends AbstractEntity {

    @Embedded
    private Profile profile;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    private LocalDateTime activatedAt;

    private LocalDateTime deactivatedAt;

    static MemberDetail create() {
        MemberDetail memberDetail = new MemberDetail();
        memberDetail.registeredAt = LocalDateTime.now();
        return memberDetail;
    }

    void activate() {
        Assert.isTrue(this.activatedAt == null, "이미 활성화된 상태입니다.");

        this.activatedAt = LocalDateTime.now();
    }

    void deactivate() {
        Assert.isTrue(this.deactivatedAt == null, "이미 비활성화된 상태입니다.");

        this.deactivatedAt = LocalDateTime.now();
    }

    void updateInfo(MemberInfoUpdateRequest updateRequest) {
        this.profile = new Profile(updateRequest.profileAddress());
        this.introduction = requireNonNull(updateRequest.introduction());
    }
}
