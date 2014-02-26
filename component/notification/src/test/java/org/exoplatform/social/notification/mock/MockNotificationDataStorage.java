/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Affero General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.notification.mock;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.model.UserSetting;
import org.exoplatform.commons.api.notification.node.NTFInforkey;
import org.exoplatform.commons.api.notification.node.TreeNode;
import org.exoplatform.commons.api.notification.service.NotificationService;
import org.exoplatform.commons.api.notification.service.storage.NotificationDataStorage;

public class MockNotificationDataStorage implements NotificationDataStorage {
  NotificationService notificationService;
  private List<NotificationInfo> notifications;
  private List<NTFInforkey> infos;
  public MockNotificationDataStorage(NotificationService notificationService) {
    this.notificationService = notificationService;
    notifications = new ArrayList<NotificationInfo>();
    infos = new ArrayList<NTFInforkey>();
  }
  @Override
  public void save(NotificationInfo notification) throws Exception {
    notifications.add(notification);
    infos.add(new NTFInforkey(notification.getName()));
  }

  @Override
  public NotificationInfo get(String id) throws Exception {
    for (NotificationInfo info : notifications) {
      if (info.getName().equals(id)) {
        return info;
      }
    }
    return null;
  }

  @Override
  public void remove(String id) throws Exception {
  }

  @Override
  public TreeNode getByUser(UserSetting setting) {
    return null;
  }
  
  public void addAll(List<NotificationInfo> notifications) throws Exception {
    for (NotificationInfo notification : notifications) {
      save(notification);
    }
  }
  
  public List<NTFInforkey> getInforKeys() {
    return infos;
  }

  public void clear() {
    infos.clear();
    notifications.clear();
  }

}
