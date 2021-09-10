package org.javacord.core.event.server.role;

import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.server.role.UserRoleRemoveEvent;
import org.javacord.core.entity.user.Member;

/**
 * The implementation of {@link UserRoleRemoveEvent}.
 */
public class UserRoleRemoveEventImpl extends UserRoleEventImpl implements UserRoleRemoveEvent {

    /**
     * Creates a new member role remove event.
     *
     * @param role The role of the event.
     * @param member The member of the event.
     */
    public UserRoleRemoveEventImpl(final Role role, final Member member) {
        super(role, member);
    }

}
