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
package org.exoplatform.social.notification;

import org.exoplatform.commons.api.notification.service.template.TemplateContext;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

public class ActivityTemplateBuilder {
  private TemplateContext context;
  private final ExoSocialActivity activity;
  
  public ActivityTemplateBuilder(TemplateContext context, ExoSocialActivity activity) {
    this.context = context;
    this.activity = activity;
  }

/*
  DEFAULT_ACTIVITY
  USER_PROFILE_ACTIVITY
  USER_ACTIVITIES_FOR_RELATIONSHIP
  SPACE_ACTIVITY
  LINK_ACTIVITY
  files:spaces
  contents:spaces
  cs-calendar:spaces
  ks-forum:spaces
  ks-answer:spaces
  ks-poll:spaces
  ks-wiki:spaces
*/

  public enum ACTIVITY_TYPE {
    DEDAULT() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        context.put("ACTIVITY", activity.getTitle());
        //
        
      }
      @Override
      public String getType() {
        return "DEFAULT_ACTIVITY";
      }
    },
    USER_PROFILE() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      
      @Override
      public String getType() {
        return "USER_PROFILE_ACTIVITY";
      }
    },
    USER_ACTIVITIES_FOR_RELATIONSHIP() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      @Override
      public String getType() {
        return "USER_ACTIVITIES_FOR_RELATIONSHIP";
      }
    },
    SPACE_ACTIVITY() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      @Override
      public String getType() {
        return "SPACE_ACTIVITY";
      }
    },
    LINK_ACTIVITY() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      @Override
      public String getType() {
        return "LINK_ACTIVITY";
      }
    },
    FILES_SPACES() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      @Override
      public String getType() {
        return "files:spaces";
      }
    },
    CONTENTS_SPACES() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      @Override
      public String getType() {
        return "contents:spaces";
      }
    },
    CALENDAR_SPACES() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      @Override
      public String getType() {
        return "cs-calendar:spaces";
      }
    },
    FORUM_SPACES() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      @Override
      public String getType() {
        return "ks-forum:spaces";
      }
    },
    ANSWER_SPACES() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      @Override
      public String getType() {
        return "ks-answer:spaces";
      }
    },
    POLL_SPACES() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      @Override
      public String getType() {
        return "ks-poll:spaces";
      }
    },
    WIKi_SPACES() {
      @Override
      public void serialize(TemplateContext context, ExoSocialActivity activity) {
        context.put("CHILD_ID", getType());
        //
        
      }
      @Override
      public String getType() {
        return "ks-wiki:spaces";
      }
    };
    //
    public abstract void serialize(TemplateContext context, ExoSocialActivity activity);
    public abstract String getType();
  }
  
  public TemplateContext serialize() {
    for (ACTIVITY_TYPE activityType : ACTIVITY_TYPE.values()) {
      if (activityType.getType().equals(activity.getType())) {
        activityType.serialize(context, activity);
        return context;
      }
    }
    // if activity not define, use default template.
    ACTIVITY_TYPE.DEDAULT.serialize(context, activity);
    return context;
  }
  
}
