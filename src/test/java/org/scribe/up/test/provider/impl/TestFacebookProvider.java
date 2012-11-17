/*
  Copyright 2012 Jerome Leleu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.scribe.up.test.provider.impl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.scribe.up.profile.Gender;
import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.facebook.FacebookEducation;
import org.scribe.up.profile.facebook.FacebookEvent;
import org.scribe.up.profile.facebook.FacebookGroup;
import org.scribe.up.profile.facebook.FacebookInfo;
import org.scribe.up.profile.facebook.FacebookMusicListen;
import org.scribe.up.profile.facebook.FacebookObject;
import org.scribe.up.profile.facebook.FacebookPhoto;
import org.scribe.up.profile.facebook.FacebookPicture;
import org.scribe.up.profile.facebook.FacebookProfile;
import org.scribe.up.profile.facebook.FacebookRelationshipStatus;
import org.scribe.up.profile.facebook.FacebookWork;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.impl.FacebookProvider;
import org.scribe.up.test.util.CommonHelper;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link org.scribe.up.provider.impl.FacebookProvider} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TestFacebookProvider extends TestProvider {
    
    @Override
    protected OAuthProvider getProvider() {
        final FacebookProvider facebookProvider = new FacebookProvider();
        facebookProvider.setKey("291329260930505");
        facebookProvider.setSecret("8ace9cbf90dcecfeb36c285854db55ab");
        facebookProvider.setCallbackUrl("http://www.google.com/");
        facebookProvider
            .setScope("email,user_likes,user_about_me,user_birthday,user_education_history,user_hometown,user_relationship_details,user_location,user_religion_politics,user_relationships,user_work_history,user_website,user_photos,user_events,user_groups,user_actions.music");
        facebookProvider.setFields(FacebookProvider.DEFAULT_FIELDS
                                   + ",friends,movies,music,books,likes,albums,events,groups,music.listens,picture");
        facebookProvider.setLimit(100);
        return facebookProvider;
    }
    
    @Override
    protected String getCallbackUrl(final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("email");
        email.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput password = form.getInputByName("pass");
        password.setValueAttribute("testpwdscribeup");
        final HtmlSubmitInput submit = form.getInputByName("login");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final FacebookProfile profile = (FacebookProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("100003571536393", profile.getId());
        assertEquals(FacebookProfile.class.getSimpleName() + UserProfile.SEPARATOR + "100003571536393",
                     profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), FacebookProfile.class));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", "Jerome", "Testscribeup", "Jerome Testscribeup",
                            "jerome.testscribeup", Gender.MALE, Locale.FRANCE,
                            "http://profile.ak.fbcdn.net/hprofile-ak-ash3/157632_100003571536393_1742338663_q.jpg",
                            "http://www.facebook.com/jerome.testscribeup", "New York, New York");
        assertNull(profile.getMiddleName());
        final List<FacebookObject> languages = profile.getLanguages();
        assertTrue(languages.get(0).getName().startsWith("Fr"));
        assertEquals("mFoMgGkdK90l07Mw9TtR6NgVXsI", profile.getThirdPartyId());
        assertEquals(1, profile.getTimezone().intValue());
        assertEquals(CommonHelper.getFormattedDate(1343375150000L, "yyyy-MM-dd'T'HH:mm:ssz", null), profile
            .getUpdateTime().toString());
        assertNull(profile.getVerified());
        assertEquals("A propos de moi", profile.getBio());
        assertEquals("03/10/1979", profile.getBirthday().toString());
        final List<FacebookEducation> educations = profile.getEducation();
        FacebookEducation education = educations.get(0);
        assertEquals("lycée mixte", education.getSchool().getName());
        assertEquals("2000", education.getYear().getName());
        assertEquals("High School", education.getType());
        education = educations.get(1);
        assertEquals("Ingénieur", education.getDegree().getName());
        assertEquals("testscribeup@gmail.com", profile.getEmail());
        assertEquals("San Francisco, California", (profile.getHometown()).getName());
        assertEquals("female", (profile.getInterestedIn()).get(0));
        assertEquals("New York, New York", (profile.getLocationObject()).getName());
        assertEquals("Sans Opinion (desc)", profile.getPolitical());
        final List<FacebookObject> favoriteAthletes = profile.getFavoriteAthletes();
        assertEquals("Surfing", favoriteAthletes.get(0).getName());
        final List<FacebookObject> favoriteTeams = profile.getFavoriteTeams();
        assertEquals("Handball Féminin de France", favoriteTeams.get(0).getName());
        assertEquals("citation", profile.getQuotes());
        assertEquals(FacebookRelationshipStatus.MARRIED, profile.getRelationshipStatus());
        assertEquals("Athéisme (desc)", profile.getReligion());
        assertNull(profile.getSignificantOther());
        assertEquals("web site", profile.getWebsite());
        final List<FacebookWork> works = profile.getWork();
        final FacebookWork work = works.get(0);
        assertEquals("Employeur", work.getEmployer().getName());
        assertEquals("Paris, France", work.getLocation().getName());
        assertEquals("Architecte Web", work.getPosition().getName());
        assertEquals("Description", work.getDescription());
        assertTrue(work.getStartDate() instanceof Date);
        assertNull(work.getEndDate());
        final List<FacebookObject> friends = profile.getFriends();
        assertEquals(1, friends.size());
        final FacebookObject friend = friends.get(0);
        assertEquals("Jérôme Leleu", friend.getName());
        assertEquals("100002406067613", friend.getId());
        final List<FacebookInfo> movies = profile.getMovies();
        assertEquals(1, movies.size());
        final FacebookInfo movie = movies.get(0);
        assertEquals("Jean-Claude Van Damme", movie.getName());
        assertEquals("21497365045", movie.getId());
        assertEquals("Actor/director", movie.getCategory());
        assertEquals(1330030350000L, movie.getCreatedTime().getTime());
        final List<FacebookInfo> musics = profile.getMusic();
        assertEquals(1, musics.size());
        final FacebookInfo music = musics.get(0);
        assertEquals("Hard rock", music.getName());
        assertEquals("112175695466436", music.getId());
        assertEquals("Musical genre", music.getCategory());
        assertEquals(1330030350000L, music.getCreatedTime().getTime());
        final List<FacebookInfo> books = profile.getBooks();
        assertEquals(1, books.size());
        final FacebookInfo book = books.get(0);
        assertEquals("Science fiction", book.getName());
        assertEquals("108157509212483", book.getId());
        assertEquals("Book genre", book.getCategory());
        assertEquals(1330030350000L, book.getCreatedTime().getTime());
        final List<FacebookInfo> likes = profile.getLikes();
        assertEquals(9, likes.size());
        final FacebookInfo like = likes.get(0);
        assertEquals("Surfing", like.getName());
        assertEquals("112392265454714", like.getId());
        assertEquals("Sport", like.getCategory());
        assertEquals(1330030467000L, like.getCreatedTime().getTime());
        final List<FacebookPhoto> albums = profile.getAlbums();
        assertEquals(3, albums.size());
        final FacebookPhoto album = albums.get(1);
        assertEquals("168023009993416", album.getId());
        final FacebookObject from = album.getFrom();
        assertEquals("100003571536393", from.getId());
        assertEquals("Jerome Testscribeup", from.getName());
        assertEquals("Profile Pictures", album.getName());
        assertEquals("http://www.facebook.com/album.php?fbid=168023009993416&id=100003571536393&aid=34144",
                     album.getLink());
        assertEquals("168023156660068", album.getCoverPhoto());
        assertEquals("everyone", album.getPrivacy());
        assertEquals(1, album.getCount().intValue());
        assertEquals("profile", album.getType());
        assertEquals(1336472634000L, album.getCreatedTime().getTime());
        assertEquals(1336472660000L, album.getUpdatedTime().getTime());
        assertFalse(album.getCanUpload());
        final List<FacebookEvent> events = profile.getEvents();
        assertEquals(1, events.size());
        final FacebookEvent event = events.get(0);
        assertEquals("Couronnement", event.getName());
        assertEquals("301212149963131", event.getId());
        assertTrue(event.getLocation().indexOf("Paris") >= 0);
        assertEquals("attending", event.getRsvpStatus());
        assertNotNull(event.getStartTime());
        assertNotNull(event.getEndTime());
        final List<FacebookGroup> groups = profile.getGroups();
        final FacebookGroup group = groups.get(0);
        assertEquals(1, group.getVersion().intValue());
        assertEquals("Dev ScribeUP", group.getName());
        assertEquals("167694120024728", group.getId());
        assertTrue(group.getAdministrator());
        assertEquals(1, group.getBookmarkOrder().intValue());
        final List<FacebookMusicListen> musicListens = profile.getMusicListens();
        assertEquals(4, musicListens.size());
        final FacebookPicture picture = profile.getPicture();
        assertEquals("http://profile.ak.fbcdn.net/hprofile-ak-ash3/157632_100003571536393_1742338663_q.jpg",
                     picture.getUrl());
        assertFalse(picture.getIsSilhouette());
        assertEquals(37, profile.getAttributes().size());
    }
}
