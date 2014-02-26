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
import java.util.List;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.MessageInfo;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.node.NTFInforkey;
import org.exoplatform.commons.api.notification.plugin.AbstractNotificationPlugin;
import org.exoplatform.commons.api.notification.plugin.NotificationPluginUtils;
import org.exoplatform.commons.api.notification.service.setting.UserSettingService;
import org.exoplatform.commons.api.notification.service.storage.NotificationDataStorage;
import org.exoplatform.commons.api.notification.service.template.TemplateContext;
import org.exoplatform.commons.notification.template.TemplateUtils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.notification.LinkProviderUtils;
import org.exoplatform.social.notification.Utils;

public class NewUserPlugin extends AbstractNotificationPlugin {

  public static final String ID = "NewUserPlugin";
  public NewUserPlugin(InitParams initParams) {
    super(initParams);
  }
  
  @Override
  public String getId() {
    return ID;
  }

  @Override
  public NotificationInfo makeNotification(NotificationContext ctx) {
    Profile profile = ctx.value(SocialNotificationUtils.PROFILE);
    String remoteId = profile.getIdentity().getRemoteId();
    try {
      UserSettingService userSettingService = CommonsUtils.getService(UserSettingService.class);
      //
      userSettingService.addMixin(remoteId);
      //

      return NotificationInfo.instance()
                              .key(getId())
                              .with(SocialNotificationUtils.REMOTE_ID.getKey(), remoteId)
                              .setSendAll(true)
                              .setFrom(remoteId)
                              .end();
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public MessageInfo makeMessage(NotificationContext ctx) {
    MessageInfo messageInfo = new MessageInfo();
    String toUser = ctx.value(NotificationPluginUtils.SENDTO);
    String language = NotificationPluginUtils.getLanguage(toUser);
    NotificationInfo notification = ctx.getNotificationInfo();
    TemplateContext templateContext = new TemplateContext(getId(), language);
    
    SocialNotificationUtils.addFooterAndFirstName(toUser, templateContext);

    String remoteId = notification.getValueOwnerParameter(SocialNotificationUtils.REMOTE_ID.getKey());
    Identity identity = Utils.getIdentityManager().getOrCreateIdentity(OrganizationIdentityProvider.NAME, remoteId, true);
    Profile userProfile = identity.getProfile();
    
    templateContext.put("USER", userProfile.getFullName());
    templateContext.put("PORTAL_NAME", NotificationPluginUtils.getBrandingPortalName());
    templateContext.put("PORTAL_HOME", NotificationPluginUtils.getPortalHome(NotificationPluginUtils.getBrandingPortalName()));
    String subject = TemplateUtils.processSubject(templateContext);
    
    templateContext.put("PROFILE_URL", LinkProviderUtils.getRedirectUrl("user", identity.getRemoteId()));
    templateContext.put("AVATAR", LinkProviderUtils.getUserAvatarUrl(userProfile));
    templateContext.put("CONNECT_ACTION_URL", LinkProviderUtils.getInviteToConnectUrl(identity.getRemoteId(), toUser));
    String body = TemplateUtils.processGroovy(templateContext);
    
    return messageInfo.subject(subject).body(body).end();
  }

  @Override
  public boolean makeDigest(NotificationContext ctx, Writer writer) {
    try {
      List<NTFInforkey> infoKeys = ctx.getNotificationInfos();
      NotificationDataStorage dataStorage = CommonsUtils.getService(NotificationDataStorage.class);
      String toUser = ctx.value(NotificationPluginUtils.SENDTO);
      String language = NotificationPluginUtils.getLanguage(toUser);
      TemplateContext templateContext = new TemplateContext(getId(), language);
      
      int count = infoKeys.size();
      String[] keys = {"USER", "USER_LIST", "LAST3_USERS"};
      String key = "";
      StringBuilder value = new StringBuilder();
      writer.append("<li style=\"margin: 0 0 13px 14px; font-size: 13px; line-height: 18px; font-family: HelveticaNeue, Helvetica, Arial, sans-serif;\">");
      for (int i = 0; i < count && i < 3; i++) {
        String remoteId = dataStorage.get(infoKeys.get(i).getUUID()).getValueOwnerParameter(SocialNotificationUtils.REMOTE_ID.getKey());
        Identity identity = Utils.getIdentityManager().getOrCreateIdentity(OrganizationIdentityProvider.NAME, remoteId, true);
        //
        if (i > 1 && count == 3) {
          key = keys[i - 1];
        } else {
          key = keys[i];
        }
        value.append(SocialNotificationUtils.buildRedirecUrl("user", identity.getRemoteId(), identity.getProfile().getFullName()));
        if (count > (i + 1) && i < 2) {
          value.append(", ");
        }
      }
      templateContext.put(key, value.toString());
      if(count > 3) {
        templateContext.put("COUNT", SocialNotificationUtils.buildRedirecUrl("connections", toUser, String.valueOf((count - 3))));
      }
      
      String portalName = System.getProperty("exo.notifications.portalname", "eXo");
      String portalLink = SocialNotificationUtils.buildRedirecUrl("portal_home", portalName, portalName);
      
      templateContext.put("PORTAL_NAME", portalName);
      templateContext.put("PORTAL_HOME", portalLink);
      String digester = TemplateUtils.processDigest(templateContext.digestType(count));
      writer.append(digester);
      writer.append("</li>");
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
