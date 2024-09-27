package com.group4.authentication.data.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StatusTest {

    @ParameterizedTest
    @EnumSource(Status.class)
    void testStatusEnumValues(Status status) {
        // Ensure the enum values are not null
        assertNotNull(status);
    }

    @Test
    void testStatusValueOf() {
        // Test specific enum value retrieval
        Status activeStatus = Status.valueOf("ACTIVE");
        assertNotNull(activeStatus);
        assertEquals(Status.ACTIVE, activeStatus);
    }
}

