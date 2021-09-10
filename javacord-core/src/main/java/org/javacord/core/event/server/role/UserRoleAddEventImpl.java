package org.javacord.core.event.server.role;

import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.core.entity.user.Member;

/**
 * The implementation of {@link UserRoleAddEvent}.
 */
public class UserRoleAddEventImpl extends UserRoleEventImpl implements UserRoleAddEvent {

    /**
     * Creates a new user role add event.
     *
     * @param role The role of the event.
     * @param member The member of the event.
     */
    public UserRoleAddEventImpl(final Role role, final Member member) {
        super(role, member);
    }

}
