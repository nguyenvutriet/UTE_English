package com.example.englishapp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestDataRepository {
    private static final Map<Integer, ReadingTest> tests = new HashMap<>();

    static {
        // Test 1: River Thames Tours
        tests.put(1, new ReadingTest(
                "River Thames Tours",
                "River Thames Tours\n" +
                "Thank you for reserving a River Thames tour with us. We are eager to welcome you aboard. Each tour lasts 3 hours. Your tour includes a luncheon served at 1:00 p.m. Please consult our Web site for a menu. Should you have any dietary restrictions and like to request a special meal, please contact our customer experience manager, Martin Torma, at least 48 hours prior to your tour.\n\n" +
                "This reservation also entitles you to a 10 percent discount on a walking tour by Edgerton Walking Tours—just provide your confirmation code when booking.\n\n" +
                "Name: Lewis Califf\n" +
                "Purchase Date: 18 April\n" +
                "Confirmation Code: H102057\n" +
                "Tour Start: 1 May, 11:30 a.m.\n" +
                "Quantity: 4\n" +
                "Total: £180.00\n" +
                "Payment: Credit card ending in 1037\n\n" +
                "Please note: Boarding ends 10 minutes before departure time. Tours cannot be rescheduled.",
                Arrays.asList(
                        new ReadingTest.Question(
                                "1. What is indicated about the river tour?",
                                Arrays.asList("A. It is one hour long.", "B. It comes with a meal.", "C. It can be rescheduled.", "D. It sells out quickly."),
                                1, // B
                                "Your tour includes a luncheon served at 1:00 p.m."
                        ),
                        new ReadingTest.Question(
                                "2. How many tickets did Mr. Califf purchase?",
                                Arrays.asList("A. 1", "B. 3", "C. 4", "D. 7"),
                                2, // C
                                "Quantity: 4"
                        ),
                        new ReadingTest.Question(
                                "3. How can customers receive a discount on a walking tour?",
                                Arrays.asList("A. By making a reservation online", "B. By paying with a credit card", "C. By requesting a coupon from the captain", "D. By mentioning a confirmation code"),
                                3, // D
                                "Provides you to a 10 percent discount on a walking tour by Edgerton Walking Tours—just provide your confirmation code when booking."
                        )
                )
        ));

        // Test 2: Sarah's Catering
        tests.put(2, new ReadingTest(
                "Sarah's Catering",
                "Sarah's Catering—What You Serve Matters\n" +
                "Sarah's Catering is a family-owned-and-operated company. The company was founded ten years ago with a mission to provide the highest quality catering services in our community. We work closely with local growers and use only the freshest ingredients. Our menu items can be adapted to the client's taste or dietary needs. For example, we can prepare vegetarian, vegan, and gluten-free options.\n\n" +
                "We provide catering for birthday parties, wedding receptions, corporate meetings, business holiday parties, and many other types of events. From planning the menu and preparing your food to engaging servers and cleanup staff for the event, Sarah's Catering has it covered.\n\n" +
                "Sarah's Catering can cater lunches in your office for a minimum of twenty people. We offer delicious options to make your group's meal a satisfying experience.\n\n" +
                "We're here to serve you! Ordering is fast and simple. Visit www.sarahscatering.com/quote to request a cost estimate for your next event.\n\n" +
                "What people are saying\n" +
                "\"Sarah's Catering was very easy to work with, and the food was delicious! Everyone in the office commented on how good the food was.\" — Glen Liu, Perkins Real Estate\n" +
                "\"All the food was perfect, and the staff was the best.\" — Annie Pierce, Kania Marketing, Inc.",
                Arrays.asList(
                        new ReadingTest.Question(
                                "1. What is indicated about Sarah's Catering?",
                                Arrays.asList("A. It uses locally sourced products.", "B. It is twenty years old.", "C. It specializes mainly in weddings.", "D. It has an on-site dining room."),
                                0, // A
                                "We work closely with local growers and use only the freshest ingredients."
                        ),
                        new ReadingTest.Question(
                                "2. The word \"taste\" in paragraph 1, line 4, is closest in meaning to",
                                Arrays.asList("A. preference", "B. sample", "C. experience", "D. flavor"),
                                0, // A
                                "Taste here refers to the client's preference or dietary needs."
                        ),
                        new ReadingTest.Question(
                                "3. What is mentioned as a service provided by Sarah's Catering?",
                                Arrays.asList("A. Entertainment planning", "B. Cooking demonstrations", "C. Cleanup after meals", "D. Rentals of tables and chairs"),
                                2, // C
                                "From planning the menu and preparing your food to engaging servers and cleanup staff for the event..."
                        ),
                        new ReadingTest.Question(
                                "4. Who most likely is Mr. Liu?",
                                Arrays.asList("A. An employee of Sarah's Catering", "B. A professional event manager", "C. A customer of Sarah's Catering", "D. An assistant at a marketing firm"),
                                2, // C
                                "He is quoted in \"What people are saying\" and used their service for his office."
                        )
                )
        ));

        // Test 3: Medillo Shoes
        tests.put(3, new ReadingTest(
                "Medillo Shoes",
                "Medillo Shoes Celebrates Twenty Years in Cape Town!\n" +
                "246 Breda Place, Wynberg, Cape Town 7800\n" +
                "021 555 0149 | www.medilloshoes.co.za\n\n" +
                "Does your job require you to stand all day long? Get the support you need! At Medillo Shoes, we specialise in comfortable, supportive footwear that is stylish and suitable for any business or medical setting.\n\n" +
                "Visit us on 10 May to receive 20 percent off your purchase of one or more pairs of shoes during this anniversary event. Should you need assistance finding the best shoes for your professional needs, our footwear specialists will be on hand to help. Schedule a free consultation at www.medilloshoes.co.za to avoid a long wait.",
                Arrays.asList(
                        new ReadingTest.Question(
                                "1. What will happen at Medillo Shoes on May 10?",
                                Arrays.asList("A. All shoes will be discounted.", "B. Shop assistants will be hired.", "C. A shoe style will be discontinued.", "D. Operational hours will be extended."),
                                0, // A
                                "Visit us on 10 May to receive 20 percent off your purchase of one or more pairs of shoes"
                        ),
                        new ReadingTest.Question(
                                "2. What is indicated about Medillo Shoes?",
                                Arrays.asList("A. It has been in business for ten years.", "B. It specializes in athletic footwear.", "C. It is located next to a medical center.", "D. It allows customers to make appointments."),
                                3, // D
                                "Schedule a free consultation at www.medilloshoes.co.za to avoid a long wait."
                        )
                )
        ));
    }

    public static ReadingTest getTestById(int id) {
        return tests.get(id);
    }
}
