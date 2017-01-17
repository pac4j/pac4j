package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.profile.facebook.*;

import java.text.SimpleDateFormat;
import java.util.*;
import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link FacebookClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunFacebookClient extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunFacebookClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup";
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected IndirectClient getClient() {
        final FacebookClient facebookClient = new FacebookClient();
        facebookClient.setKey("1002857006444390");
        facebookClient.setSecret("c352c9668493d3f9ac3f0fa71f04c187");
        facebookClient.setCallbackUrl(PAC4J_URL);
        facebookClient
                .setScope("email,user_about_me,user_actions.books,user_actions.fitness,user_actions.music,user_actions.news,user_actions.video,user_birthday,user_education_history,user_events,user_friends,user_games_activity,user_hometown,user_likes,user_location,user_managed_groups,user_photos,user_posts,user_relationship_details,user_relationships,user_religion_politics,user_status,user_tagged_places,user_videos,user_website,user_work_history");
        facebookClient.setFields(FacebookClient.DEFAULT_FIELDS
                + ",friends,movies,music,books,likes,albums,events,groups,music.listens,picture");
        facebookClient.setLimit(100);
        return facebookClient;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        final FacebookProfile profile = (FacebookProfile) userProfile;
        assertEquals("771361542992890", profile.getId());
        assertEquals(FacebookProfile.class.getName() + CommonProfile.SEPARATOR + "771361542992890",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), FacebookProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, "Jerome", "Testscribeup", "Jerome Testscribeup", null, Gender.MALE,
                Locale.FRANCE, "https://scontent.xx.fbcdn.net/v/t1.0-1/c170.50.621.621/s50x50/550165_168023156660068_12755354_n.jpg?oh=",
                "https://www.facebook.com/app_scoped_user_id/771361542992890/", "New York, New York");
        assertNull(profile.getMiddleName());
        final List<FacebookObject> languages = profile.getLanguages();
        assertTrue(languages.get(0).getName().startsWith("Fr"));
        assertTrue(CommonHelper.isNotBlank(profile.getThirdPartyId()));
        assertEquals(1, profile.getTimezone().intValue());
        assertTrue(profile.getVerified());
        assertEquals("A propos de moi", profile.getAbout());
        assertEquals("03/10/1979", new SimpleDateFormat("MM/dd/yyyy").format(profile.getBirthday()));
        final List<FacebookEducation> educations = profile.getEducation();
        FacebookEducation education = educations.get(0);
        assertEquals("lycée mixte", education.getSchool().getName());
        assertEquals("2000", education.getYear().getName());
        assertEquals("High School", education.getType());
        education = educations.get(1);
        assertEquals("Ingénieur", education.getDegree().getName());
        assertNull(profile.getEmail());
        assertEquals("San Francisco, California", profile.getHometown().getName());
        assertEquals("female", profile.getInterestedIn().get(0));
        assertEquals("New York, New York", profile.getLocationObject().getName());
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
        assertNull(work.getEndDate());
        final List<FacebookObject> friends = profile.getFriends();
        assertEquals(1, friends.size());
        final FacebookObject friend = friends.get(0);
        assertEquals("Jérôme Leleu", friend.getName());
        assertEquals("874202936003234", friend.getId());
        final List<FacebookInfo> movies = profile.getMovies();
        assertEquals(1, movies.size());
        final FacebookInfo movie = movies.get(0);
        assertEquals("Jean-Claude Van Damme", movie.getName());
        assertEquals("21497365045", movie.getId());
        assertEquals(1330030350000L, movie.getCreatedTime().getTime());
        final List<FacebookInfo> musics = profile.getMusic();
        assertEquals(1, musics.size());
        final FacebookInfo music = musics.get(0);
        assertEquals("Hard rock", music.getName());
        assertEquals("112175695466436", music.getId());
        assertEquals(1330030350000L, music.getCreatedTime().getTime());
        final List<FacebookInfo> books = profile.getBooks();
        assertEquals(1, books.size());
        final FacebookInfo book = books.get(0);
        assertEquals("Science fiction", book.getName());
        assertEquals("108157509212483", book.getId());
        assertEquals(null, book.getCategory());
        assertEquals(1330030350000L, book.getCreatedTime().getTime());
        final List<FacebookInfo> likes = profile.getLikes();
        assertEquals(9, likes.size());
        final FacebookInfo like = likes.get(0);
        assertEquals("Boxing", like.getName());
        assertEquals("105648929470083", like.getId());
        assertEquals(1360152791000L, like.getCreatedTime().getTime());
        final List<FacebookPhoto> albums = profile.getAlbums();
        assertEquals(3, albums.size());
        final FacebookPhoto album = albums.get(1);
        assertEquals("168023009993416", album.getId());
        final FacebookObject from = album.getFrom();
        assertNull(from);
        assertEquals("Profile Pictures", album.getName());
        final List<FacebookEvent> events = profile.getEvents();
        assertEquals(2, events.size());
        final FacebookEvent event = events.get(0);
        assertEquals("Couronnement", event.getName());
        assertEquals("301212149963131", event.getId());
        assertEquals("attending", event.getRsvpStatus());
        assertNotNull(event.getStartTime());
        assertNotNull(event.getEndTime());
        final List<FacebookGroup> groups = profile.getGroups();
        final FacebookGroup group = groups.get(0);
        assertEquals("Dev ScribeUP", group.getName());
        assertEquals("167694120024728", group.getId());
        final List<FacebookMusicListen> musicListens = profile.getMusicListens();
        assertNull(musicListens);
        final FacebookPicture picture = profile.getPicture();
        assertFalse(picture.getSilhouette());
        assertEquals(35, profile.getAttributes().size());
    }
}
