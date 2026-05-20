package com.team3imple.barosiksa.domain.member.dto.request;

import com.team3imple.barosiksa.domain.member.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberSignUpRequest(
        @NotBlank(message = "사용자 이름은 필수입니다.")
        @Size(max = 50)
        String username,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 100)
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
                message = "비밀번호는 8~20자 영문, 숫자, 특수문자를 최소 한 가지씩 포함해야 합니다."
        )
        String password,

        Role role
) {
}
