/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
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
package org.exoplatform.social.notification.plugin;

import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.MessageInfo;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.node.NTFInforkey;
import org.exoplatform.commons.api.notification.plugin.AbstractNotificationPlugin;
import org.exoplatform.commons.api.notification.plugin.NotificationPluginUtils;
import org.exoplatform.commons.api.notification.service.storage.NotificationDataStorage;
import org.exoplatform.commons.api.notification.service.template.TemplateContext;
import org.exoplatform.commons.notification.template.TemplateUtils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.notification.LinkProviderUtils;
import org.exoplatform.social.notification.Utils;

public class SpaceInvitationPlugin extends AbstractNotificationPlugin {
  public static final String ID = "SpaceInvitationPlugin";

  public SpaceInvitationPlugin(InitParams initParams) {
    super(initParams);
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public NotificationInfo makeNotification(NotificationContext ctx) {
    Space space = ctx.value(SocialNotificationUtils.SPACE);
    String userId = ctx.value(SocialNotificationUtils.REMOTE_ID);
    
    return NotificationInfo.instance().key(getId())
           .with(SocialNotificationUtils.PRETTY_NAME.getKey(), space.getPrettyName())
           .with(SocialNotificationUtils.SPACE_ID.getKey(), space.getId())
           .to(userId).end();
  }

  @Override
  public MessageInfo makeMessage(NotificationContext ctx) {
    MessageInfo messageInfo = new MessageInfo();
    String toUser = ctx.value(NotificationPluginUtils.SENDTO);
    String language = NotificationPluginUtils.getLanguage(toUser);
    NotificationInfo notification = ctx.getNotificationInfo();
    TemplateContext templateContext = new TemplateContext(getId(), language);

    SocialNotificationUtils.addFooterAndFirstName(toUser, templateContext);

    String spaceId = notification.getValueOwnerParameter(SocialNotificationUtils.SPACE_ID.getKey());
    Space space = Utils.getSpaceService().getSpaceById(spaceId);
    
    templateContext.put("SPACE", space.getDisplayName());
    templateContext.put("SPACE_URL", LinkProviderUtils.getRedirectUrl("space", space.getId()));
    String subject = TemplateUtils.processSubject(templateContext);
    
    templateContext.put("SPACE_AVATAR", LinkProviderUtils.getSpaceAvatarUrl(space));
    templateContext.put("ACCEPT_SPACE_INVITATION_ACTION_URL", LinkProviderUtils.getAcceptInvitationToJoinSpaceUrl(space.getId(), toUser));
    templateContext.put("REFUSE_SPACE_INVITATION_ACTION_URL", LinkProviderUtils.getIgnoreInvitationToJoinSpaceUrl(space.getId(), toUser));
    String body = TemplateUtils.processGroovy(templateContext);
    
    return messageInfo.subject(subject).body(body).end();
  }

  @Override
  public boolean makeDigest(NotificationContext ctx, Writer writer) {
    try {
      NotificationDataStorage dataStorage = CommonsUtils.getService(NotificationDataStorage.class);
      List<NTFInforkey> infoKeys = ctx.getNotificationInfos();
      String toUser = ctx.value(NotificationPluginUtils.SENDTO);
      String language = NotificationPluginUtils.getLanguage(toUser);
      TemplateContext templateContext = new TemplateContext(getId(), language);
      Map<String, List<String>> receiverMap = new LinkedHashMap<String, List<String>>();

      for (NTFInforkey infoKey : infoKeys) {
        NotificationInfo message = dataStorage.get(infoKey.getUUID());
        SocialNotificationUtils.processInforSendTo(receiverMap, toUser, message.getValueOwnerParameter(SocialNotificationUtils.SPACE_ID.getKey()));
      }
      writer.append(SocialNotificationUtils.getMessageByIds(receiverMap, templateContext, "space"));
    } catch (Exception e) {
      ctx.setException(e);
      return false;
    }
    
    return true;
  }

  @Override
  public boolean isValid(NotificationContext ctx) {
    return true;
  }

}