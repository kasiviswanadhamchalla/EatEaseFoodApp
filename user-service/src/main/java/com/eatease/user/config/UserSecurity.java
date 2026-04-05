package com.eatease.user.config;

import com.eatease.user.security.UserPrincipal;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    public boolean isOwner(Long targetUserId, UserPrincipal principal) {
        if (principal == null) return false;
        return principal.id().equals(targetUserId);
    }
}
