package com.group4.user.data.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoleTest {

    @ParameterizedTest
    @EnumSource(Role.class)
    void testRoleEnumValues(Role role) {
        // Ensure the enum values are not null
        assertNotNull(role);
    }

    @Test
    void testRoleValueOf() {
        // Test specific enum value retrieval
        Role adminRole = Role.valueOf("ADMIN");
        assertNotNull(adminRole);
        assertEquals(Role.ADMIN, adminRole);
    }
}