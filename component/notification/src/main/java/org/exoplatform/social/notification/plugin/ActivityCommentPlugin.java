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
import java.util.ArrayList;
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
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.notification.LinkProviderUtils;
import org.exoplatform.social.notification.Utils;

public class ActivityCommentPlugin extends AbstractNotificationPlugin {
  
  public ActivityCommentPlugin(InitParams initParams) {
    super(initParams);
  }

  public static final String ID = "ActivityCommentPlugin";

  @Override
  public NotificationInfo makeNotification(NotificationContext ctx) {
    ExoSocialActivity comment = ctx.value(SocialNotificationUtils.ACTIVITY);
    ExoSocialActivity activity = Utils.getActivityManager().getParentActivity(comment);
    //Send notification to all others users who have comment on this activity
    List<String> receivers = new ArrayList<String>();
    Utils.sendToCommeters(receivers, activity.getCommentedIds(), comment.getPosterId());
    Utils.sendToStreamOwner(receivers, activity.getStreamOwner(), comment.getPosterId());
    Utils.sendToActivityPoster(receivers, activity.getPosterId(), comment.getPosterId());

    //
    return NotificationInfo.instance()
           .to(receivers)
           .with(SocialNotificationUtils.ACTIVITY_ID.getKey(), comment.getId())
           .with(SocialNotificationUtils.POSTER.getKey(), Utils.getUserId(comment.getUserId()))
           .key(getId());
  }

  @Override
  public MessageInfo makeMessage(NotificationContext ctx) {
    MessageInfo messageInfo = new MessageInfo();
    String toUser = ctx.value(NotificationPluginUtils.SENDTO);
    String language = NotificationPluginUtils.getLanguage(toUser);
    NotificationInfo notification = ctx.getNotificationInfo();

    String activityId = notification.getValueOwnerParameter(SocialNotificationUtils.ACTIVITY_ID.getKey());
    ExoSocialActivity activity = Utils.getActivityManager().getActivity(activityId);
    ExoSocialActivity parentActivity = Utils.getActivityManager().getParentActivity(activity);
    Identity identity = Utils.getIdentityManager().getIdentity(activity.getPosterId(), true);
    
    TemplateContext templateContext = new TemplateContext(getId(), language);
    templateContext.put("USER", identity.getProfile().getFullName());
    String subject = TemplateUtils.processSubject(templateContext);
    
    SocialNotificationUtils.addFooterAndFirstName(toUser, templateContext);
    templateContext.put("PROFILE_URL", LinkProviderUtils.getRedirectUrl("user", identity.getRemoteId()));
    templateContext.put("COMMENT", activity.getTitle());
    templateContext.put("ACTIVITY", parentActivity.getTitle());
    templateContext.put("REPLY_ACTION_URL", LinkProviderUtils.getRedirectUrl("reply_activity_highlight_comment", parentActivity.getId() + "-" + activity.getId()));
    templateContext.put("VIEW_FULL_DISCUSSION_ACTION_URL", LinkProviderUtils.getRedirectUrl("view_full_activity_highlight_comment", parentActivity.getId() + "-" + activity.getId()));

    String body = TemplateUtils.processGroovy(templateContext);
    
    return messageInfo.subject(subject).body(body).end();
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public boolean makeDigest(NotificationContext ctx, Writer writer) {
    try {
      NotificationDataStorage dataStorage = CommonsUtils.getService(NotificationDataStorage.class);
      List<NTFInforkey> infoKeys = ctx.getNotificationInfos();
      String toUser = ctx.value(NotificationPluginUtils.SENDTO);
      String language = NotificationPluginUtils.getLanguage(toUser);
      TemplateContext templateContext = new TemplateContext(getId(), language);
      SocialNotificationUtils.addFooterAndFirstName(toUser, templateContext);

      // Store the activity id as key, and the list all identities who posted to the activity.
      Map<String, List<String>> receiverMap = new LinkedHashMap<String, List<String>>();

      for (NTFInforkey infoKey : infoKeys) {
        NotificationInfo message = dataStorage.get(infoKey.getUUID());
        String activityId = message.getValueOwnerParameter(SocialNotificationUtils.ACTIVITY_ID.getKey());
        ExoSocialActivity activity = Utils.getActivityManager().getActivity(activityId);
        ExoSocialActivity parentActivity = Utils.getActivityManager().getParentActivity(activity);
        //
        SocialNotificationUtils.processInforSendTo(receiverMap, parentActivity.getId(), message.getValueOwnerParameter("poster"));
      }
      writer.append(SocialNotificationUtils.getMessageByIds(receiverMap, templateContext));
    } catch (Exception e) {
      ctx.setException(e);
      return false;
    }

    return true;
  }

  @Override
  public boolean isValid(NotificationContext ctx) {
    ExoSocialActivity comment = ctx.value(SocialNotificationUtils.ACTIVITY);
    ExoSocialActivity activity = Utils.getActivityManager().getParentActivity(comment);
    
    Identity spaceIdentity = Utils.getIdentityManager().getOrCreateIdentity(SpaceIdentityProvider.NAME, activity.getStreamOwner(), false);
    //if the space is not null and it's not the default activity of space, then it's valid to make notification 
    if (spaceIdentity != null && activity.getPosterId().equals(spaceIdentity.getId())) {
      return false;
    }
    return true;
  }
  
  
}
