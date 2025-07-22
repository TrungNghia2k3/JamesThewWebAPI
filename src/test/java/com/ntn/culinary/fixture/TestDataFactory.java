package com.ntn.culinary.fixture;

import com.ntn.culinary.model.*;
import com.ntn.culinary.request.AnnouncementRequest;
import com.ntn.culinary.request.ContestEntryRequest;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestDataFactory {
    public static Announcement createAnnouncement() {
        Announcement a = new Announcement();
        a.setId(1);
        a.setTitle("Test Announcement");
        a.setAnnouncementDate(Date.valueOf("2023-01-01"));
        a.setDescription("Description");
        a.setContestId(42);
        return a;
    }

    public static List<Announcement> createAnnouncementsList() {
        List<Announcement> announcements = new ArrayList<>();

        Announcement a1 = new Announcement();
        a1.setId(1);
        a1.setTitle("Announcement 1");
        a1.setAnnouncementDate(Date.valueOf("2023-01-01"));
        a1.setDescription("Description 1");
        a1.setContestId(10);

        Announcement a2 = new Announcement();
        a2.setId(2);
        a2.setTitle("Announcement 2");
        a2.setAnnouncementDate(Date.valueOf("2023-02-01"));
        a2.setDescription("Description 2");
        a2.setContestId(20);

        Announcement a3 = new Announcement();
        a3.setId(3);
        a3.setTitle("Announcement 3");
        a3.setAnnouncementDate(Date.valueOf("2023-03-01"));
        a3.setDescription("Description 3");
        a3.setContestId(30);

        announcements.add(a1);
        announcements.add(a2);
        announcements.add(a3);

        return announcements;
    }

    public static Contest createContest() {
        Contest c = new Contest();
        c.setId(42);
        c.setHeadline("Contest Headline");
        return c;
    }

    public static AnnounceWinner createWinner() {
        AnnounceWinner w = new AnnounceWinner();
        w.setId(100);
        w.setContestEntryId(200);
        w.setRanking("1");
        return w;
    }

    public static List<AnnounceWinner> createWinnersList() {
        List<AnnounceWinner> winners = new ArrayList<>();

        AnnounceWinner winner = new AnnounceWinner();
        winner.setContestEntryId(100);
        winner.setRanking("1");

        AnnounceWinner winner2 = new AnnounceWinner();
        winner2.setContestEntryId(200);
        winner2.setRanking("2");

        winners.add(winner);
        winners.add(winner2);

        return winners;
    }

    public static ContestEntry createContestEntry() {
        ContestEntry e = new ContestEntry();
        e.setId(200);
        e.setName("Entry Title");
        return e;
    }

    public static User createUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("Test User 1");
        user.setPassword("Test Password 1");
        user.setEmail("Test Email 1");
        user.setFirstName("Test First Name 1");
        user.setLastName("Test Last Name 1");
        user.setPhone("Test Phone 1");
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setActive(true);
        user.setAvatar("Test Avatar 1");
        user.setLocation("Test Location 1");
        user.setSchool("Test School 1");
        user.setHighlights("Test Highlights 1");
        user.setExperience("Test Experience 1");
        user.setEducation("Test Education 1");
        user.setSocialLinks("Test Social Links 1");
        return user;

    }

    public static List<User> createUsersList() {
        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setId(1);
        user1.setUsername("Test User 1");
        user1.setPassword("Test Password 1");
        user1.setEmail("Test Email 1");
        user1.setFirstName("Test First Name 1");
        user1.setLastName("Test Last Name 1");
        user1.setPhone("Test Phone 1");

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("Test User 2");
        user2.setPassword("Test Password 2");
        user2.setEmail("Test Email 2");
        user2.setFirstName("Test First Name 2");
        user2.setLastName("Test Last Name 2");
        user2.setPhone("Test Phone 2");

        User user3 = new User();
        user3.setId(3);
        user3.setUsername("Test User 3");
        user3.setPassword("Test Password 3");
        user3.setEmail("Test Email 3");
        user3.setFirstName("Test First Name 3");
        user3.setLastName("Test Last Name 3");
        user3.setPhone("Test Phone 3");

        users.add(user1);
        users.add(user2);
        users.add(user3);

        return users;
    }

    public static AnnouncementRequest createAnnouncementRequest() {
        AnnouncementRequest request = new AnnouncementRequest();
        request.setId(1);
        request.setTitle("New Announcement");
        request.setAnnouncementDate(Date.valueOf("2023-01-01"));
        request.setDescription("Description");
        request.setContestId(1);

        List<AnnounceWinner> winners = new ArrayList<>();

        AnnounceWinner winner = new AnnounceWinner();
        winner.setContestEntryId(100);
        winner.setRanking("1");

        AnnounceWinner winner2 = new AnnounceWinner();
        winner2.setContestEntryId(200);
        winner2.setRanking("2");

        winners.add(winner);
        winners.add(winner2);

        request.setWinners(winners);

        return request;
    }

    public static List<AnnouncementRequest> createAnnouncementRequestList() {
        List<AnnouncementRequest> requests = new ArrayList<>();

        AnnouncementRequest r1 = new AnnouncementRequest();
        r1.setContestId(1);
        r1.setTitle("Announcement Request 1");
        r1.setDescription("Description 1");
        r1.setAnnouncementDate(Date.valueOf("2023-01-01"));

        AnnouncementRequest r2 = new AnnouncementRequest();
        r2.setContestId(2);
        r2.setTitle("Announcement Request 2");
        r2.setDescription("Description 2");
        r2.setAnnouncementDate(Date.valueOf("2023-02-01"));

        requests.add(r1);
        requests.add(r2);

        return requests;
    }

    public static ContestEntryRequest createContestEntryRequest() {
        ContestEntryRequest request = new ContestEntryRequest();
        request.setUserId(1);
        request.setContestId(2);
        request.setName("My Entry");
        request.setCategory("Dessert");
        request.setArea("Europe");
        request.setInstructions("Instructions text");
        request.setIngredients("Ingredients text");
        request.setShortDescription("Short desc");
        request.setPrepareTime("10 min");
        request.setCookingTime("20 min");
        request.setYield("2 servings");
        return request;
    }

    public static List<Recipe> createRecipeList() {
        Recipe recipe = new Recipe();
        recipe.setId(1);
        recipe.setName("Pho Bo");
        recipe.setCategory("Vietnamese");
        recipe.setArea("Asia");
        recipe.setInstructions("Cook it well.");
        recipe.setImage("pho.jpg");
        recipe.setIngredients("Beef, noodle");
        recipe.setPublishedOn(Date.valueOf("2023-01-01"));
        recipe.setRecipedBy(123);
        recipe.setPrepareTime("10");
        recipe.setCookingTime("30");
        recipe.setYield("2");
        recipe.setShortDescription("Delicious beef noodle soup.");
        recipe.setAccessType("FREE");
        return List.of(recipe);
    }

    public static List<Comment> createCommentList() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setRecipeId(1);
        comment.setContent("Great recipe!");
        comment.setDate(Timestamp.valueOf(LocalDateTime.now()));
        return List.of(comment);
    }

    public static Nutrition createNutrition() {
        Nutrition nutrition = new Nutrition();
        nutrition.setId(1);
        nutrition.setRecipeId(1);
        nutrition.setCalories("Calories");
        nutrition.setCarbohydrate("500");
        nutrition.setProtein("500");
        return nutrition;
    }

    public static List<Nutrition> createNutritionList() {
        Nutrition nutrition = new Nutrition();
        nutrition.setId(1);
        nutrition.setRecipeId(1);
        nutrition.setCalories("Calories");
        nutrition.setCarbohydrate("500");
        nutrition.setProtein("500");
        return List.of(nutrition);
    }

    public static List<DetailedInstructions> createInstructions() {
        DetailedInstructions ins = new DetailedInstructions();
        ins.setId(1);
        ins.setRecipeId(1);
        ins.setName("Boil Beef");
        ins.setText("Boil the beef.");
        ins.setImage("step1.jpg");
        return List.of(ins);
    }
}

