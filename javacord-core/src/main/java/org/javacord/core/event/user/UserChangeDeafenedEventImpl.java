package org.javacord.core.event.user;

import org.javacord.api.event.user.UserChangeDeafenedEvent;
import org.javacord.core.entity.user.Member;

/**
 * The implementation of {@link UserChangeDeafenedEvent}.
 */
public class UserChangeDeafenedEventImpl extends ServerUserEventImpl implements UserChangeDeafenedEvent {

    private final Member newMember;
    private final Member oldMember;

    /**
     * Creates a new user change deafened event.
     *
     * @param newMember The new member.
     * @param oldMember The old member.
     */
    public UserChangeDeafenedEventImpl(final Member newMember, final Member oldMember) {
        super(newMember.getUser(), newMember.getServer());
        this.newMember = newMember;
        this.oldMember = oldMember;
    }

    @Override
    public boolean isNewDeafened() {
        // TODO This is wrong.
        return newMember.isSelfDeafened();
    }

    @Override
    public boolean isOldDeafened() {
        return oldMember.isSelfDeafened();
    }
}
