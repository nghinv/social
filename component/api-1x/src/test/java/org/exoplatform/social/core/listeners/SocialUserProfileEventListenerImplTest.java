/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package org.exoplatform.social.core.listeners;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.test.AbstractAPI1xTest;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Feb 6, 2012  
 */
public class SocialUserProfileEventListenerImplTest extends AbstractAPI1xTest {
  private final Log  LOG = ExoLogger.getLogger(SocialUserProfileEventListenerImplTest.class);

  private IdentityManager identityManager;
  

  private List<Identity>  tearDownIdentityList;

  private OrganizationService organizationService;
  
  private Identity paul;
  private Identity raul;
  private boolean alreadyAddedPlugins = false;

  public void setUp() throws Exception {
    super.setUp();
    identityManager = (IdentityManager) getContainer().getComponentInstanceOfType(IdentityManager.class);
    organizationService = (OrganizationService) getContainer().getComponentInstanceOfType(OrganizationService.class);
    fakePlugins();

    paul = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "paul", true);
    raul = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "raul", true);
    tearDownIdentityList = new ArrayList<Identity>();
    tearDownIdentityList.add(paul);
    tearDownIdentityList.add(raul);
  }
  
  private void fakePlugins() throws Exception {
    if (alreadyAddedPlugins == false) {
      organizationService.addListenerPlugin(new SocialUserEventListenerImpl());
      organizationService.addListenerPlugin(new SocialUserProfileEventListenerImpl());
      alreadyAddedPlugins = true;
    }
    
  }

  public void tearDown() throws Exception {
    
    for (Identity identity : tearDownIdentityList) {
      identityManager.deleteIdentity(identity);
    }
    super.tearDown();
  }
  
  /**
   * This testcase what will use for unit testing with scenario to 
   * synchronous profile from Social to Portal's Organization
   * @throws Exception
   */
  public void testSynchronizeFromSocialToPortal() throws Exception {
    String paulRemoteId = "paul";
    Identity paulIdentity = populateProfile(paul);
    Assert.assertNotNull(paulIdentity);
    //
    UserProfile pProfile = organizationService.getUserProfileHandler().findUserProfileByName(paulRemoteId);
    Assert.assertNotNull(pProfile);
    
    //
    Assert.assertEquals(paulIdentity.getProfile().getPosition(), pProfile.getAttribute(UserProfile.PERSONAL_INFO_KEYS[7]));//user.jobtitle
    
    //update firstName
    {
      Identity identity1 = updateProfileKeyValue(paulIdentity, Profile.FIRST_NAME, "thanh");
      User pUser1 = organizationService.getUserHandler().findUserByName(paulRemoteId);
      //
      Assert.assertEquals(identity1.getProfile().getProperty(Profile.FIRST_NAME), pUser1.getFirstName());
    }
    
    //update lastName
    {
      Identity identity1 = updateProfileKeyValue(paulIdentity, Profile.LAST_NAME, "vu");
      User pUser1 = organizationService.getUserHandler().findUserByName(paulRemoteId);
      //
      Assert.assertEquals(identity1.getProfile().getProperty(Profile.LAST_NAME), pUser1.getLastName());
    }
    
    //update leader
    {
      Identity identity1 = updateProfilePosition(paulIdentity, "leader");
      UserProfile pProfile1 = organizationService.getUserProfileHandler().findUserProfileByName(paulRemoteId);
      //
      Assert.assertEquals(identity1.getProfile().getPosition(), pProfile1.getAttribute(UserProfile.PERSONAL_INFO_KEYS[7]));//user.jobtitle

    }
    
    //update gender
    {
      Identity identity2 = updateProfileGender(paulIdentity, "male");
      UserProfile pProfile2 = organizationService.getUserProfileHandler().findUserProfileByName(paulRemoteId);
      //user.gender
      Assert.assertEquals(identity2.getProfile().getProperty(Profile.GENDER), pProfile2.getAttribute(UserProfile.PERSONAL_INFO_KEYS[4]));

    }
  }
  
  /**
   * This TestCase what will use for unit testing with scenario to 
   * synchronous profile from Portal's Organization to Social
   * @throws Exception
   */
  public void testSynchronizeFromProtalToSocial() throws Exception {
    String raulRemoteId = "raul";
    Identity raulIdentity = populateProfile(raul);
    Assert.assertNotNull(raulIdentity);
    
    User pUser = organizationService.getUserHandler().findUserByName(raulRemoteId);
    Assert.assertNotNull(pUser);
    //
    Profile sProfile = identityManager.getProfile(raulIdentity);
    Assert.assertNotNull(sProfile);
    
    Assert.assertEquals(sProfile.getProperty(Profile.FIRST_NAME), pUser.getFirstName());
    Assert.assertEquals(sProfile.getProperty(Profile.LAST_NAME), pUser.getLastName());
    
  }
  /**
   * Populates the list of identities by specifying the number of items and to indicate if they are added to
   * the tear-down list.
   *
   * @param numberOfItems
   * @param addedToTearDownList
   */
  private Identity populateProfile(Identity identity) {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    Profile profile = identity.getProfile();
    Assert.assertNotNull(profile);
    
    //BASIC_INFO
    profile.setProperty(Profile.FIRST_NAME, "FirstName");
    profile.setProperty(Profile.LAST_NAME, "LastName");
    profile.setProperty(Profile.FULL_NAME, "FirstName" + " " +  "LastName");
    identityManager.updateProfile(profile);
    
    //CONTACT INFO
    profile.setProperty(Profile.GENDER, "fmale");
    identityManager.updateProfile(profile);
    

    //POSITION
    profile.setProperty(Profile.POSITION, "developer");
    identityManager.updateProfile(profile);

    RequestLifeCycle.end();
    identity.setProfile(profile);
    
    return identity;

  }
  
  private Identity updateProfilePosition(Identity identity, String position) {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    Profile profile = identity.getProfile();
    Assert.assertNotNull(profile);
    //
    profile.setProperty(Profile.POSITION, position);
    identityManager.updateProfile(profile);
    
    //profile.setProperty(Profile.CONTACT_PHONES, new String[]{"098939179"});
    //profile.setProperty(Profile.CONTACT_URLS, new String[]{"http://exoplatform.com"});

    RequestLifeCycle.end();
    identity.setProfile(profile);
    return identity;

  }
  
  private Identity updateProfileGender(Identity identity, String gender) {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    Profile profile = identity.getProfile();
    Assert.assertNotNull(profile);
        
    profile.setProperty(Profile.GENDER, gender);
    identityManager.updateProfile(profile);
    
    RequestLifeCycle.end();
    identity.setProfile(profile);
    return identity;

  }
  
  private Identity updateProfileKeyValue(Identity identity, String key, String value) {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    Profile profile = identity.getProfile();
    Assert.assertNotNull(profile);
        
    profile.setProperty(key, value);
    identityManager.updateProfile(profile);
    
    RequestLifeCycle.end();
    identity.setProfile(profile);
    return identity;

  }
}
