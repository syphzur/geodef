package pl.polsl.bol.krzysztof.backend.models.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER("ROLE_USER"), ROLE_ADMIN("ROLE_ADMIN");

    public final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return this.name;
    }

    public Role getEnumFromString(final String str) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(str)) {
                return role;
            }
        }
        return null;
    }
}
