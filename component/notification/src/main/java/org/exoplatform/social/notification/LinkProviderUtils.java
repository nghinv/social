package org.exoplatform.social.notification;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;

public class LinkProviderUtils {
  
public static final String RESOURCE_URL = "social/notifications";
  
  public static final String INVITE_TO_CONNECT = RESOURCE_URL + "/inviteToConnect";
  
  public static final String CONFIRM_INVITATION_TO_CONNECT = RESOURCE_URL + "/confirmInvitationToConnect";
  
  public static final String IGNORE_INVITATION_TO_CONNECT = RESOURCE_URL + "/ignoreInvitationToConnect";
  
  public static final String ACCEPT_INVITATION_JOIN_SPACE = RESOURCE_URL + "/acceptInvitationToJoinSpace";
  
  public static final String IGNORE_INVITATION_JOIN_SPACE = RESOURCE_URL + "/ignoreInvitationToJoinSpace";
  
  public static final String VALIDATE_REQUEST_JOIN_SPACE = RESOURCE_URL + "/validateRequestToJoinSpace";
  
  public static final String REPLY_ACTIVITY = RESOURCE_URL + "/replyActivity";
  
  public static final String VIEW_FULL_DISCUSSION = RESOURCE_URL + "/viewFullDiscussion";
  
  public static final String REDIRECT_URL = RESOURCE_URL + "/redirectUrl";

  /**
   * Gets the url to the user's profile page of the receiver
   * @param senderId remoteId of the sender
   * @param receiverId remoteId of the receiver
   * @return
   */
  public static String getInviteToConnectUrl(String senderId, String receiverId) {
    return getRestUrl(INVITE_TO_CONNECT, senderId, receiverId);
  }
  
  /**
   * Gets the url to the user's profile page of the sender
   * @param senderId remoteId of the sender
   * @param receiverId remoteId of the receiver
   * @return
   */
  public static String getConfirmInvitationToConnectUrl(String senderId, String receiverId) {
    return getRestUrl(CONFIRM_INVITATION_TO_CONNECT, senderId, receiverId);
  }
  
  /**
   * Gets the url to the connection's tab of the current user
   * @param senderId remoteId of the sender
   * @param receiverId remoteId of the receiver
   * @return
   */
  public static String getIgnoreInvitationToConnectUrl(String senderId, String receiverId) {
    return getRestUrl(IGNORE_INVITATION_TO_CONNECT, senderId, receiverId);
  }
  
  /**
   * Gets the url to the space's home page
   * @param spaceId
   * @param userId
   * @return
   */
  public static String getAcceptInvitationToJoinSpaceUrl(String spaceId, String userId) {
    return getRestUrl(ACCEPT_INVITATION_JOIN_SPACE, spaceId, userId);
  }
  
  /**
   * Gets the url to the space's home page
   * @param spaceId
   * @param userId remoteId of the user
   * @return
   */
  public static String getIgnoreInvitationToJoinSpaceUrl(String spaceId, String userId) {
    return getRestUrl(IGNORE_INVITATION_JOIN_SPACE, spaceId, userId);
  }
  
  /**
   * Gets the url to the space's members
   * @param spaceId
   * @param userId remoteId of the user
   * @return
   */
  public static String getValidateRequestToJoinSpaceUrl(String spaceId, String userId) {
    return getRestUrl(VALIDATE_REQUEST_JOIN_SPACE, spaceId, userId);
  }
  
  /**
   * Gets the url to the activity with id = activityId of userId
   * @param activityId
   * @param userId remoteId of the user
   * @return
   */
  public static String getReplyActivityUrl(String activityId, String userId) {
    return getRestUrl(REPLY_ACTIVITY, activityId, userId);
  }
  
  /**
   * Gets the url to the activity with id = activityId of userId
   * @param activityId
   * @param userId remoteId of the user
   * @return
   */
  public static String getViewFullDiscussionUrl(String activityId, String userId) {
    return getRestUrl(VIEW_FULL_DISCUSSION, activityId, userId);
  }
  
  /**
   * Gets the associated page of type
   * @param type type of the page : user or space or activity
   * @param objectId can be a space's id or user's id or activity's id
   * @return
   */
  public static String getRedirectUrl(String type, String objectId) {
    return getRestUrl(REDIRECT_URL, type, objectId);
  }

  public static String getRestUrl(String type, String objectId1, String objectId2) {
    String baseUrl = getBaseRestUrl();
    return new StringBuffer(baseUrl).append("/").append(type)
        .append("/").append(objectId1).append("/").append(objectId2).toString();
  }
  
  /** 
   * Get base url of rest service
   * @return base rest url like : http://localhost:8080/rest
   */
  private static String getBaseRestUrl() {
    String domain = System.getProperty("gatein.email.domain.url");
    ExoContainerContext context = CommonsUtils.getService(ExoContainerContext.class);
    return new StringBuffer(domain).append("/").append(context.getRestContextName()).toString();
  }
}
