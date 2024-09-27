package com.group4.authentication.data.model;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class AuthUserTest {

    private User mockUser;
    private AuthUser authUser;

    @BeforeEach
    public void setUp() {
        // Create a mock User object
        mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("testuser");
        when(mockUser.getPassword()).thenReturn("password123");
        when(mockUser.getRole()).thenReturn("ROLE_ADMIN");

        // Initialize AuthUser with the mock User
        authUser = new AuthUser(mockUser);
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", authUser.getPassword());
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", authUser.getUsername());
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = authUser.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(authUser.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(authUser.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(authUser.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(authUser.isEnabled());
    }

    @Test
    void testEquals() {
        AuthUser anotherAuthUser = new AuthUser(mockUser);
        assertEquals(authUser, anotherAuthUser);

        // Modify the username and check inequality
        User differentUser = mock(User.class);
        when(differentUser.getUsername()).thenReturn("differentUser");
        AuthUser differentAuthUser = new AuthUser(differentUser);
        assertNotEquals(authUser, differentAuthUser);
    }

    @Test
    void testHashCode() {
        AuthUser anotherAuthUser = new AuthUser(mockUser);
        assertEquals(authUser.hashCode(), anotherAuthUser.hashCode());

        // Modify the username and check different hashcode
        User differentUser = mock(User.class);
        when(differentUser.getUsername()).thenReturn("differentUser");
        AuthUser differentAuthUser = new AuthUser(differentUser);
        assertNotEquals(authUser.hashCode(), differentAuthUser.hashCode());
    }

    @Test
    void testGetUser() {
        assertEquals(mockUser, authUser.getUser());
    }

    @Test
    void testEqualsSameObject() {
        // Test the scenario where the same object is compared (this == o)
        assertEquals(authUser, authUser);
    }

    @Test
    void testEqualsNull() {
        // Test the scenario where null is passed (o == null)
        assertNotEquals(null, authUser);
    }

    @Test
    void testEqualsDifferentClass() {
        // Test the scenario where the object is of a different class (getClass() != o.getClass())
        assertNotEquals(authUser, new Object());
    }
}
