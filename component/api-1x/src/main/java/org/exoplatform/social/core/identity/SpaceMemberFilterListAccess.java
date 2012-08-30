/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.core.identity;

import java.util.List;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.social.common.ListAccessValidator;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.profile.ProfileFilter;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.api.IdentityStorage;
import org.exoplatform.social.core.storage.api.MembershipType;

/**
 * ListAccess is used in loading connection with the input providerId, existing Identity and ProfileFilter, .
 * With this list we can manage the size of returned list by offset and limit.
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          thanhvc@exoplatform.com
 * Aug 22, 2011  
 */
public class SpaceMemberFilterListAccess implements ListAccess<Identity> {
  
  private IdentityStorage identityStorage; 
  
  /**
   * Identity use for load.
   */
  Space space;

  /**
   * The type of connection list access.
   */
  MembershipType type = MembershipType.MANAGER;
  
  /**
   * providerId for profile filter.
   */
  String providerId;
  
  /**
   * profile filter for searching connections.
   */
  ProfileFilter profileFilter;
  
  /**
   * The connection list access Type Enum.
   */
  public enum Type {
    /** Gets a list of identities who invited to connect to the provided identity and profile filter */
    MEMBER,
    /** Gets a list of identities who are the leader of  */
    MANAGER
  }
  
  /**
   * The constructor.
   * 
   * @param relationshipStorage Storage object of Relationship.
   * @param identity Identity to get connection. 
   */
  public SpaceMemberFilterListAccess(IdentityStorage identityStorage, Space space, ProfileFilter filter) {
    this.identityStorage = identityStorage;
    this.profileFilter = filter;
    this.space = space;
  }
  
  /**
   * The constructor.
   * 
   * @param relationshipStorage
   * @param identity
   * @param type
   * @since 1.2.3
   */
  public SpaceMemberFilterListAccess(IdentityStorage identityStorage, Space space,ProfileFilter filter, Type type) {
    this(identityStorage, space, filter);
    setType(type);
  }
  
  /**
   * {@inheritDoc}
   */
  public Identity[] load(int offset, int limit) throws Exception, IllegalArgumentException {
    ListAccessValidator.validateIndex(offset, limit, getSize());
    List<Identity> identities = null;
    identities = identityStorage.getSpaceMemberIdentitiesByProfileFilter(space, profileFilter, type, offset, limit);    
    return identities.toArray(new Identity[identities.size()]);
  }

  /**
   * {@inheritDoc}
   */
  public int getSize() throws Exception {
      return identityStorage.getSpaceMemberIdentitiesByProfileFilter(space, profileFilter, type, 0, 0).size();
  }

  /**
   * Gets the type of connection list access.
   * 
   * @return
   * @since 1.2.3
   */
  public Type getType() {
    switch (type) {
      case MANAGER:
        return Type.MANAGER;
      case MEMBER:
        return Type.MEMBER;
      default :
        throw new NullPointerException();
    }
  }

  /**
   * Sets the type of connection list access.
   * 
   * @param type
   * @since 1.2.3
   */
  public void setType(Type type) {
    switch (type) {
      case MANAGER:
        this.type = MembershipType.MANAGER;
        break;
      case MEMBER:
        this.type = MembershipType.MEMBER;
        break;
    }
  }
}

