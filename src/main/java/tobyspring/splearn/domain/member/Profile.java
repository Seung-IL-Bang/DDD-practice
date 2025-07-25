package tobyspring.splearn.domain.member;

import java.util.regex.Pattern;

public record Profile(String address) {

    private static final Pattern PROFILE_ADDRESS_PATTERN =
            Pattern.compile("[a-z0-9]+");

    public Profile {
        if (address == null || (!address.isBlank() && !PROFILE_ADDRESS_PATTERN.matcher(address).matches())) {
            throw new IllegalArgumentException("프로필 주소 형식이 바르지 않습니다: " + address);
        }

        if (address.length() > 15) throw new IllegalArgumentException("프로필 주소는 15자 이하이어야 합니다: " + address);
    }

    public String url() {
        return "@" + address;
    }
}
