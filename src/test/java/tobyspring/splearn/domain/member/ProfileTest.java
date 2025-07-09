package tobyspring.splearn.domain.member;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileTest {

    @Test
    void profile() {
        new Profile("seungil");
        new Profile("bang");
        new Profile("0507");
        new Profile("");
    }

    @Test
    void profileFail() {
        assertThatThrownBy(() -> new Profile(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile("a".repeat(16))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile("프로필")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void url() {
        var profile = new Profile("seungil");

        assertThat(profile.url()).isEqualTo("@seungil");
    }

}