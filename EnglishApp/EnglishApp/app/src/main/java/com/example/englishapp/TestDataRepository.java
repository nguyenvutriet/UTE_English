package com.example.englishapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDataRepository {
    private static final Map<Integer, ReadingTest> tests = new HashMap<>();
    private static int nextGeneratedTestId = 1000;

    static {
        // Test 1: River Thames Tours
        tests.put(1, new ReadingTest(
                "River Thames Tours",
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
                                1,
                                "Your tour includes a luncheon served at 1:00 p.m."
                        ),
                        new ReadingTest.Question(
                                "2. How many tickets did Mr. Califf purchase?",
                                Arrays.asList("A. 1", "B. 3", "C. 4", "D. 7"),
                                2,
                                "Quantity: 4"
                        ),
                        new ReadingTest.Question(
                                "3. How can customers receive a discount on a walking tour?",
                                Arrays.asList("A. By making a reservation online", "B. By paying with a credit card", "C. By requesting a coupon from the captain", "D. By mentioning a confirmation code"),
                                3,
                                "Mention the confirmation code when booking the walking tour."
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
                                0,
                                "They work with local growers and use fresh ingredients."
                        ),
                        new ReadingTest.Question(
                                "2. The word \"taste\" in paragraph 1, line 4, is closest in meaning to",
                                Arrays.asList("A. preference", "B. sample", "C. experience", "D. flavor"),
                                0,
                                "Taste here means personal preference."
                        ),
                        new ReadingTest.Question(
                                "3. What is mentioned as a service provided by Sarah's Catering?",
                                Arrays.asList("A. Entertainment planning", "B. Cooking demonstrations", "C. Cleanup after meals", "D. Rentals of tables and chairs"),
                                2,
                                "They provide servers and cleanup staff."
                        ),
                        new ReadingTest.Question(
                                "4. Who most likely is Mr. Liu?",
                                Arrays.asList("A. An employee of Sarah's Catering", "B. A professional event manager", "C. A customer of Sarah's Catering", "D. An assistant at a marketing firm"),
                                2,
                                "He is a testimonial customer."
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
                                0,
                                "There is a 20 percent off anniversary event."
                        ),
                        new ReadingTest.Question(
                                "2. What is indicated about Medillo Shoes?",
                                Arrays.asList("A. It has been in business for ten years.", "B. It specializes in athletic footwear.", "C. It is located next to a medical center.", "D. It allows customers to make appointments."),
                                3,
                                "Customers can schedule a free consultation online."
                        )
                )
        ));
    }

    public static ReadingTest getTestById(int id) {
        return tests.get(id);
    }

    public static int createRandomFillBlankTest() {
        List<ReadingTest.Question> questionBank = buildFillBlankQuestionBank();
        Collections.shuffle(questionBank);

        int questionCount = Math.min(10, questionBank.size());
        List<ReadingTest.Question> selectedQuestions = new ArrayList<>();

        for (int i = 0; i < questionCount; i++) {
            ReadingTest.Question source = questionBank.get(i);
            selectedQuestions.add(new ReadingTest.Question(
                    "Câu " + (i + 1) + ": " + source.getQuestionText(),
                    source.getOptions(),
                    source.getCorrectOptionIndex(),
                    source.getExplanation()
            ));
        }

        int generatedId = nextGeneratedTestId++;
        ReadingTest generatedTest = new ReadingTest(
                "Điền khuyết","Hãy chọn đáp án đúng nhất theo ngữ cảnh để hoàn thành các câu sau:",
                selectedQuestions
        );
        tests.put(generatedId, generatedTest);
        return generatedId;
    }

    private static List<ReadingTest.Question> buildFillBlankQuestionBank() {
        List<ReadingTest.Question> list = new ArrayList<>();

        list.add(q("She ______ to school every day.", "A. go", "B. goes", "C. going", "D. gone", 1, "She là ngôi thứ ba số ít ở hiện tại đơn nên động từ thêm -s."));
        list.add(q("I ______ a teacher.", "A. am", "B. is", "C. are", "D. be", 0, "Chủ ngữ I đi với am."));
        list.add(q("They ______ football on Sunday.", "A. plays", "B. play", "C. playing", "D. played", 1, "They dùng động từ nguyên mẫu: play."));
        list.add(q("He ______ English very well.", "A. speak", "B. speaks", "C. speaking", "D. spoke", 1, "He ở hiện tại đơn nên động từ thêm -s."));
        list.add(q("We ______ in a big house.", "A. live", "B. lives", "C. living", "D. lived", 0, "Chủ ngữ we dùng động từ nguyên mẫu."));
        list.add(q("This is ______ book.", "A. I", "B. me", "C. my", "D. mine", 2, "Cần tính từ sở hữu trước danh từ: my book."));
        list.add(q("She has ______ apple.", "A. a", "B. an", "C. the", "D. no article", 1, "apple bắt đầu bằng nguyên âm nên dùng an."));
        list.add(q("There ______ a cat on the table.", "A. is", "B. are", "C. am", "D. be", 0, "Danh từ số ít 'a cat' nên dùng is."));
        list.add(q("We ______ TV now.", "A. watch", "B. watches", "C. are watching", "D. watched", 2, "Now cho thấy thì hiện tại tiếp diễn."));
        list.add(q("I ______ coffee every morning.", "A. drink", "B. drinks", "C. drinking", "D. drank", 0, "Thói quen hiện tại với I dùng drink."));

        list.add(q("She is ______ than me.", "A. tall", "B. taller", "C. tallest", "D. more tall", 1, "Có 'than' nên dùng so sánh hơn: taller."));
        list.add(q("This is the ______ movie I have ever seen.", "A. good", "B. better", "C. best", "D. well", 2, "Có 'the' nên dùng so sánh nhất: best."));
        list.add(q("He ______ to school yesterday.", "A. go", "B. goes", "C. went", "D. going", 2, "Yesterday là quá khứ đơn nên dùng went."));
        list.add(q("I am interested ______ music.", "A. in", "B. on", "C. at", "D. for", 0, "Cụm cố định: interested in."));
        list.add(q("She ______ dinner when I arrived.", "A. cooks", "B. cooked", "C. was cooking", "D. is cooking", 2, "Hành động đang xảy ra khi hành động khác xen vào: was cooking."));
        list.add(q("We have lived here ______ 5 years.", "A. since", "B. for", "C. from", "D. at", 1, "For dùng với khoảng thời gian."));
        list.add(q("He is good ______ math.", "A. in", "B. at", "C. on", "D. for", 1, "Cụm cố định: good at."));
        list.add(q("If it rains, we ______ at home.", "A. stay", "B. stayed", "C. will stay", "D. staying", 2, "Điều kiện loại 1: If + hiện tại, will + V."));
        list.add(q("She can ______ English.", "A. speaks", "B. speaking", "C. speak", "D. spoke", 2, "Sau modal verb 'can' dùng V nguyên mẫu."));
        list.add(q("They ______ finished their homework.", "A. has", "B. have", "C. had", "D. having", 1, "They đi với have trong hiện tại hoàn thành."));

        list.add(q("If I ______ rich, I would travel the world.", "A. am", "B. was", "C. were", "D. be", 2, "Điều kiện loại 2 với I thường dùng were."));
        list.add(q("She suggested ______ a break.", "A. take", "B. taking", "C. to take", "D. took", 1, "Suggest + V-ing."));
        list.add(q("The book ______ I bought is interesting.", "A. who", "B. which", "C. where", "D. when", 1, "Đại từ quan hệ cho vật: which."));
        list.add(q("He is the man ______ helped me.", "A. which", "B. where", "C. who", "D. when", 2, "Đại từ quan hệ cho người: who."));
        list.add(q("I look forward to ______ you.", "A. see", "B. seeing", "C. saw", "D. seen", 1, "To ở đây là giới từ nên dùng V-ing."));
        list.add(q("She ______ her homework before dinner.", "A. finish", "B. finished", "C. has finished", "D. finishing", 2, "Hiện tại hoàn thành: has finished."));
        list.add(q("Neither Tom nor his friends ______ coming.", "A. is", "B. are", "C. was", "D. be", 1, "Động từ chia theo chủ ngữ gần nhất: friends → are."));
        list.add(q("By the time I arrived, they ______.", "A. leave", "B. left", "C. had left", "D. leaving", 2, "By the time + quá khứ đơn thường đi với quá khứ hoàn thành."));
        list.add(q("He denied ______ the money.", "A. take", "B. taking", "C. took", "D. to take", 1, "Deny + V-ing."));
        list.add(q("The more you practice, the ______ you become.", "A. good", "B. better", "C. best", "D. well", 1, "Cấu trúc so sánh kép: the more..., the better...."));

        return list;
    }

    private static ReadingTest.Question q(String question, String a, String b, String c, String d, int correctIndex, String explanation) {
        return new ReadingTest.Question(
                question,
                Arrays.asList(a, b, c, d),
                correctIndex,
                explanation
        );
    }

    public static int createRandomSentenceArrangeTest() {
        List<SentenceArrangeQuestion> questionBank = buildSentenceArrangeQuestionBank();
        Collections.shuffle(questionBank);

        int questionCount = Math.min(10, questionBank.size());
        List<ReadingTest.Question> selectedQuestions = new ArrayList<>();

        for (int i = 0; i < questionCount; i++) {
            SentenceArrangeQuestion source = questionBank.get(i);
            selectedQuestions.add(new ReadingTest.Question(
                    "Câu " + (i + 1) + ": Sắp xếp thứ tự từ sau thành câu đúng: " + String.join(", ", source.getWords()),
                    source.getOptions(),
                    source.getCorrectOptionIndex(),
                    "Đáp án đúng: " + source.getCorrectAnswer()
            ));
        }

        int generatedId = nextGeneratedTestId++;
        ReadingTest generatedTest = new ReadingTest(
                "Sắp xếp câu - Đề ngẫu nhiên",
                "Mỗi lần hệ thống sẽ tạo ngẫu nhiên 10 câu từ bộ 30 câu. Chọn thứ tự từ đúng cho từng câu.",
                selectedQuestions
        );
        tests.put(generatedId, generatedTest);
        return generatedId;
    }

    public static List<SentenceArrangeQuestion> buildSentenceArrangeQuestionBank() {
        List<SentenceArrangeQuestion> list = new ArrayList<>();

        list.add(sq("I / every day / go / to school", Arrays.asList("A. I go to school every day", "B. Go I to school every day", "C. Every day I go to school", "D. I every day go to school"), 0, "I go to school every day"));
        list.add(sq("she / likes / music / listening to", Arrays.asList("A. She likes music listening to", "B. She likes listening to music", "C. Likes she listening to music", "D. Music she likes listening to"), 1, "She likes listening to music"));
        list.add(sq("play / they / football / Sunday / on", Arrays.asList("A. They play on Sunday football", "B. On Sunday they play football", "C. They play football on Sunday", "D. They on Sunday play football"), 2, "They play football on Sunday"));
        list.add(sq("is / my / teacher / kind / very", Arrays.asList("A. My teacher is very kind", "B. Is my teacher very kind", "C. My very teacher is kind", "D. Kind is my teacher very"), 0, "My teacher is very kind"));
        list.add(sq("reading / I / a book / am", Arrays.asList("A. I am reading a book", "B. Am I reading a book", "C. A book I am reading", "D. Reading I am a book"), 0, "I am reading a book"));
        list.add(sq("he / TV / watching / is", Arrays.asList("A. He is watching TV", "B. Is he TV watching", "C. TV he is watching", "D. Watching he is TV"), 0, "He is watching TV"));
        list.add(sq("we / dinner / having / are", Arrays.asList("A. We are having dinner", "B. Are we dinner having", "C. Dinner we are having", "D. Having are we dinner"), 0, "We are having dinner"));
        list.add(sq("likes / she / coffee / drinking", Arrays.asList("A. She likes drinking coffee", "B. Likes she coffee drinking", "C. Coffee she likes drinking", "D. She drinking likes coffee"), 0, "She likes drinking coffee"));
        list.add(sq("brother / my / student / is / a", Arrays.asList("A. My brother is a student", "B. Is my brother a student", "C. A student my brother is", "D. My student is a brother"), 0, "My brother is a student"));
        list.add(sq("go / they / to / park / the", Arrays.asList("A. They go to the park", "B. Go they to the park", "C. To the park they go", "D. The park they go to"), 0, "They go to the park"));

        list.add(sq("yesterday / I / went / to / market / the", Arrays.asList("A. I went to the market yesterday", "B. Yesterday I to the market went", "C. I to the market went yesterday", "D. To the market I went yesterday"), 0, "I went to the market yesterday"));
        list.add(sq("she / when / called / I / was / cooking", Arrays.asList("A. She called when I was cooking", "B. When she called I was cooking", "C. I was cooking when she called", "D. When I was cooking she called"), 2, "I was cooking when she called"));
        list.add(sq("have / I / finished / homework / my", Arrays.asList("A. I have finished my homework", "B. Have I finished my homework", "C. My homework I have finished", "D. Finished I have my homework"), 0, "I have finished my homework"));
        list.add(sq("will / we / travel / next / week", Arrays.asList("A. We will travel next week", "B. Will we travel next week", "C. We next week will travel", "D. Travel we will next week"), 0, "We will travel next week"));
        list.add(sq("is / interesting / this / very / book", Arrays.asList("A. This book is very interesting", "B. Is this very interesting book", "C. Very interesting this book is", "D. This very is interesting book"), 0, "This book is very interesting"));
        list.add(sq("English / learning / she / enjoys", Arrays.asList("A. She enjoys learning English", "B. Enjoys she learning English", "C. Learning English she enjoys", "D. English she enjoys learning"), 0, "She enjoys learning English"));
        list.add(sq("playing / he / games / likes / video", Arrays.asList("A. He likes playing video games", "B. Likes he playing video games", "C. Video games he likes playing", "D. He playing likes video games"), 0, "He likes playing video games"));
        list.add(sq("I / if / free / go / will / am / I", Arrays.asList("A. If I am free, I will go", "B. If I will go, I am free", "C. I am free if I will go", "D. Will I go if I am free"), 0, "If I am free, I will go"));
        list.add(sq("to / want / I / doctor / be / a", Arrays.asList("A. I want to be a doctor", "B. Want I to be a doctor", "C. To be a doctor I want", "D. A doctor want I to be"), 0, "I want to be a doctor"));
        list.add(sq("she / has / lived / here / years / for / 5", Arrays.asList("A. She has lived here for 5 years", "B. Has she lived here for 5 years", "C. For 5 years she has lived here", "D. 5 years she has lived here for"), 0, "She has lived here for 5 years"));

        list.add(sq("if / I / were / I / rich / travel / would", Arrays.asList("A. If I were rich, I would travel", "B. If I would travel, I were rich", "C. I were rich if I would travel", "D. Would I travel if I were rich"), 0, "If I were rich, I would travel"));
        list.add(sq("book / the / bought / I / yesterday / interesting / is", Arrays.asList("A. The book I bought yesterday is interesting", "B. The book is interesting I bought yesterday", "C. Is the book I bought yesterday interesting", "D. I bought yesterday the interesting book"), 0, "The book I bought yesterday is interesting"));
        list.add(sq("person / the / helped / me / who / is / kind", Arrays.asList("A. The person who helped me is kind", "B. The person is kind who helped me", "C. Who helped me the person is kind", "D. Helped the person is kind who me"), 0, "The person who helped me is kind"));
        list.add(sq("before / left / I / they / arrived / had", Arrays.asList("A. They had left before I arrived", "B. Before I arrived they had left", "C. Had they left before I arrived", "D. I arrived before they had left"), 0, "They had left before I arrived"));
        list.add(sq("forward / I / to / seeing / you / look", Arrays.asList("A. I look forward to seeing you", "B. Look I forward to seeing you", "C. Forward I look to seeing you", "D. I to seeing you look forward"), 0, "I look forward to seeing you"));
        list.add(sq("more / practice / the / you / better / become / you / the", Arrays.asList("A. The more you practice, the better you become", "B. The better you practice, the more you become", "C. You practice more, you become better", "D. Practice the more, become the better"), 0, "The more you practice, the better you become"));
        list.add(sq("suggested / she / going / cinema / the / to", Arrays.asList("A. She suggested going to the cinema", "B. Suggested she going to the cinema", "C. She going suggested to the cinema", "D. Going to the cinema she suggested"), 0, "She suggested going to the cinema"));
        list.add(sq("denied / he / taking / money / the", Arrays.asList("A. He denied taking the money", "B. Denied he taking the money", "C. He taking denied the money", "D. Taking the money he denied"), 0, "He denied taking the money"));
        list.add(sq("by / time / arrived / I / the / had / started / meeting", Arrays.asList("A. By the time I arrived, the meeting had started", "B. The meeting had started by the time I arrived", "C. By I arrived the time, the meeting started", "D. The meeting started I arrived by the time"), 0, "By the time I arrived, the meeting had started"));
        list.add(sq("neither / nor / friends / his / coming / are / Tom", Arrays.asList("A. Neither Tom nor his friends are coming", "B. Tom neither nor his friends are coming", "C. His friends nor Tom neither coming are", "D. Are coming Tom neither nor his friends"), 0, "Neither Tom nor his friends are coming"));

        return list;
    }

    private static SentenceArrangeQuestion sq(String words, List<String> options, int correctIndex, String correctAnswer) {
        return new SentenceArrangeQuestion(
                correctAnswer.split("\\s+"),
                options,
                correctIndex,
                correctAnswer
        );
    }

    public static class SentenceArrangeQuestion {
        private String[] words;
        private List<String> options;
        private int correctOptionIndex;
        private String correctAnswer;

        public SentenceArrangeQuestion(String[] words, List<String> options, int correctOptionIndex, String correctAnswer) {
            this.words = words;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
            this.correctAnswer = correctAnswer;
        }

        public String[] getWords() { return words; }
        public List<String> getOptions() { return options; }
        public int getCorrectOptionIndex() { return correctOptionIndex; }
        public String getCorrectAnswer() { return correctAnswer; }
    }

    public static String getVietnameseMeaning(String correctAnswer) {
        switch (correctAnswer) {
            case "I go to school every day":
                return "Tôi đi học mỗi ngày.";
            case "She likes listening to music":
                return "Cô ấy thích nghe nhạc.";
            case "They play football on Sunday":
                return "Họ chơi bóng đá vào Chủ nhật.";
            case "My teacher is very kind":
                return "Giáo viên của tôi rất tốt bụng.";
            case "I am reading a book":
                return "Tôi đang đọc sách.";
            case "He is watching TV":
                return "Anh ấy đang xem TV.";
            case "We are having dinner":
                return "Chúng tôi đang ăn tối.";
            case "She likes drinking coffee":
                return "Cô ấy thích uống cà phê.";
            case "My brother is a student":
                return "Anh/em trai tôi là học sinh.";
            case "They go to the park":
                return "Họ đi đến công viên.";
            case "I went to the market yesterday":
                return "Hôm qua tôi đã đi chợ.";
            case "I was cooking when she called":
                return "Tôi đang nấu ăn thì cô ấy gọi.";
            case "I have finished my homework":
                return "Tôi đã hoàn thành bài tập về nhà.";
            case "We will travel next week":
                return "Chúng tôi sẽ đi du lịch vào tuần tới.";
            case "This book is very interesting":
                return "Quyển sách này rất thú vị.";
            case "She enjoys learning English":
                return "Cô ấy thích học tiếng Anh.";
            case "He likes playing video games":
                return "Anh ấy thích chơi game.";
            case "If I am free, I will go":
                return "Nếu tôi rảnh, tôi sẽ đi.";
            case "I want to be a doctor":
                return "Tôi muốn trở thành bác sĩ.";
            case "She has lived here for 5 years":
                return "Cô ấy đã sống ở đây 5 năm.";
            case "If I were rich, I would travel":
                return "Nếu tôi giàu, tôi sẽ đi du lịch.";
            case "The book I bought yesterday is interesting":
                return "Quyển sách tôi mua hôm qua rất thú vị.";
            case "The person who helped me is kind":
                return "Người đã giúp tôi rất tốt bụng.";
            case "They had left before I arrived":
                return "Họ đã rời đi trước khi tôi đến.";
            case "I look forward to seeing you":
                return "Tôi mong gặp bạn.";
            case "The more you practice, the better you become":
                return "Bạn càng luyện tập, bạn càng giỏi hơn.";
            case "She suggested going to the cinema":
                return "Cô ấy đề nghị đi xem phim.";
            case "He denied taking the money":
                return "Anh ấy phủ nhận đã lấy tiền.";
            case "By the time I arrived, the meeting had started":
                return "Khi tôi đến, cuộc họp đã bắt đầu.";
            case "Neither Tom nor his friends are coming":
                return "Cả Tom và bạn của anh ấy đều không đến.";
            default:
                return "Sắp xếp các từ để tạo câu đúng.";
        }
    }

    public static List<ListeningQuestion> buildPictureDescriptionQuestions() {
        List<ListeningQuestion> list = new ArrayList<>();

        list.add(new ListeningQuestion(
                R.drawable.u1_c1,
                R.raw.u1_c1,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một người đàn ông đang đóng một cánh cổng kim loại.",
                        "Một người đàn ông đang đi xuống một lối đi lát đá.",
                        "Một số chậu cây được đặt dọc theo lối đi.",
                        "Một số lá cờ đang treo từ các ban công."
                ),
                1,
                "Một người đàn ông đang đóng một cánh cổng kim loại.",
                "A man is closing a metal gate.",
                "(A) A man is closing a metal gate.\n" +
                        "(B) A man is walking down a stone path.\n" +
                        "(C) Some potted plants line a walkway.\n" +
                        "(D) Some flags are hanging from balconies."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u1_c2,
                R.raw.u1_c2,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một số túi mua sắm đã được đặt trên sân ga.",
                        "Một số người đang xếp hàng để mua vé tàu.",
                        "Một người phụ nữ đang kéo hành lý phía sau mình.",
                        "Một người đàn ông đang tựa vào lan can."
                ),
                3,
                "Một người phụ nữ đang kéo hành lý phía sau mình.",
                "A woman is pulling her luggage behind her.",
                "(A) Some shopping bags have been placed on a train platform.\n" +
                        "(B) Some people are lined up to buy train tickets.\n" +
                        "(C) A woman is pulling her luggage behind her.\n" +
                        "(D) A man is leaning against a railing."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u1_c3,
                R.raw.u1_c3,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một số tấm ván gỗ đang được dựa vào một bức tường.",
                        "Một chiếc xe đang đỗ cạnh một tòa nhà.",
                        "Một số viên đá đã được xếp chồng lên một tấm kê hàng.",
                        "Một kiện hàng đã được để gần một cánh cửa."
                ),
                1,
                "Một số tấm ván gỗ đang được dựa vào một bức tường.",
                "Some wooden planks are leaning against a wall.",
                "(A) Some wooden planks are leaning against a wall.\n" +
                        "(B) A vehicle is parked next to a building.\n" +
                        "(C) Some stones have been stacked on a pallet.\n" +
                        "(D) A package has been left near a door."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u1_c4,
                R.raw.u1_c4,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một người đàn ông đang giơ điện thoại của mình lên.",
                        "Một người đàn ông đang ghim một tờ giấy lên bảng thông báo.",
                        "Một người đàn ông đang thả một vật vào thùng.",
                        "Một người đàn ông đang lau rửa một số cửa sổ."
                ),
                0,
                "Một người đàn ông đang thả một vật vào thùng.",
                "A man is dropping an item into a bin.",
                "(A) A man is holding up his phone.\n" +
                        "(B) A man is pinning a note to a bulletin board.\n" +
                        "(C) A man is dropping an item into a bin.\n" +
                        "(D) A man is washing some windows."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u1_c5,
                R.raw.u1_c5,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Cô ấy đang buộc dây tạp dề phía sau lưng.",
                        "Cô ấy đang với lấy đồ ăn trên kệ.",
                        "Cô ấy đang làm việc tại quầy.",
                        "Cô ấy đang đặt đồ ăn vào tủ lạnh."
                ),
                2,
                "Cô ấy đang với lấy đồ ăn trên kệ.",
                "She's reaching for food on a shelf.",
                "(A) She's tying the back of her apron.\n" +
                        "(B) She's reaching for food on a shelf.\n" +
                        "(C) She's working at a counter.\n" +
                        "(D) She's putting food into a refrigerator."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u1_c6,
                R.raw.u1_c6,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Anh ấy đang chuẩn bị một bữa tiệc buffet.",
                        "Anh ấy đang đặt đĩa lên các bàn ăn.",
                        "Anh ấy đang tưới một số cây trong khu vườn.",
                        "Anh ấy đang mang thức ăn lên cầu thang."
                ),
                0,
                "Anh ấy đang đặt đĩa lên các bàn ăn.",
                "He's putting plates on dining tables.",
                "(A) He's setting up a buffet.\n" +
                        "(B) He's putting plates on dining tables.\n" +
                        "(C) He's watering some plants in a garden.\n" +
                        "(D) He's carrying food up a stairway."
        ));

        return list;
    }

    public static List<ListeningQuestion> buildPictureDescriptionQuestionsTest2() {
        List<ListeningQuestion> list = new ArrayList<>();

        list.add(new ListeningQuestion(
                R.drawable.u2_c1,
                R.raw.u2_c1,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một người phụ nữ đang đứng cạnh chiếc bàn.",
                        "Một người phụ nữ đang đi lên những bậc thang.",
                        "Một người phụ nữ đang bước vào tòa nhà.",
                        "Một người phụ nữ đang nhìn vào tấm bản đồ."
                ),
                3,
                "Một người phụ nữ đang đứng cạnh chiếc bàn.",
                "A woman is standing near a desk.",
                "(A) A woman is standing near a desk.\n" +
                        "(B) A woman is climbing some stairs.\n" +
                        "(C) A woman is entering a building.\n" +
                        "(D) A woman is looking at a map."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u2_c2,
                R.raw.u2_c2,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Cô ấy đang bơi trên sông.",
                        "Cô ấy đang đi bộ gần biển.",
                        "Những chiếc ghế được xếp bên bờ biển.",
                        "Chiếc mũ đang nằm trên cát."
                ),
                2,
                "Cô ấy đang đi bộ gần biển.",
                "She's jogging near the ocean.",
                "(A) She's swimming in the water.\n" +
                        "(B) She's jogging near the ocean.\n" +
                        "(C) Chairs are set up on the beach.\n" +
                        "(D) A hat is lying on the sand."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u2_c3,
                R.raw.u2_c3,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Cô ấy đang với lấy một món đồ từ tủ trưng bày.",
                        "Cô ấy đang giữ tay vào xe đẩy mua hàng.",
                        "Một vài sản phẩm đang được sắp xếp trên các kệ.",
                        "Một vài hàng hóa bị rơi trên sàn."
                ),
                1,
                "Một vài sản phẩm đang được sắp xếp trên các kệ.",
                "Some goods are being arranged on shelves.",
                "(A) She's reaching for an item from a display case.\n" +
                        "(B) She's holding onto a shopping cart.\n" +
                        "(C) Some goods are being arranged on shelves.\n" +
                        "(D) Some merchandise has fallen on the floor."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u2_c4,
                R.raw.u2_c4,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Chiếc hàng rào đang được sơn trong công viên.",
                        "Một trong những người phụ nữ đang mặc lên người chiếc áo khoác.",
                        "Một vài người đang làm việc tại khu vườn.",
                        "Một vài người đang nhìn lên cái cây."
                ),
                2,
                "Một vài người đang làm việc tại khu vườn.",
                "Some people are working in a garden.",
                "(A) A fence is being painted in a park.\n" +
                        "(B) One of the women is putting on a jacket.\n" +
                        "(C) Some people are working in a garden.\n" +
                        "(D) Some people are looking up at the trees."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u2_c5,
                R.raw.u2_c5,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một chiếc thảm đang được cuộn lại.",
                        "Một vài tấm rèm được đóng.",
                        "Một vài chiếc gối được xếp chồng lên sàn nhà.",
                        "Một vài tài liệu được đặt trước ghế sofa."
                ),
                3,
                "Một vài tài liệu được đặt trước ghế sofa.",
                "Some reading materials have been placed in front of a sofa.",
                "(A) A carpet is being rolled up.\n" +
                        "(B) Some curtains have been closed.\n" +
                        "(C) Some cushions are piled on the floor.\n" +
                        "(D) Some reading materials have been placed in front of a sofa."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u2_c6,
                R.raw.u2_c6,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một vài nhạc sĩ đang biểu diễn trên hiên nhà.",
                        "Một vài bậc thang được sửa chữa.",
                        "Một vài giá nhạc đang được gập lại.",
                        "Một vài nhạc cụ đã được đặt trong hộp của chúng."
                ),
                0,
                "Một vài giá nhạc đang được gập lại.",
                "Some music stands are being folded up.",
                "(A) Some musicians are performing on a porch.\n" +
                        "(B) Some steps are being repaired.\n" +
                        "(C) Some music stands are being folded up.\n" +
                        "(D) Some instruments have been placed in their cases."
        ));

        return list;
    }

    public static List<ListeningQuestion> buildPictureDescriptionQuestionsTest3() {
        List<ListeningQuestion> list = new ArrayList<>();

        list.add(new ListeningQuestion(
                R.drawable.u3_c1,
                R.raw.u3_c1,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Anh ấy đang sửa một ngăn tủ hồ sơ.",
                        "Anh ấy đang xắn tay áo lên.",
                        "Anh ấy đang đóng một chiếc máy tính xách tay.",
                        "Anh ấy đang uống từ một chiếc cốc."
                ),
                3,
                "Anh ấy đang uống từ một chiếc cốc.",
                "He’s drinking from a mug.",
                "(A) He’s fixing a file drawer.\n" +
                        "(B) He’s rolling up his sleeves.\n" +
                        "(C) He’s closing a laptop computer.\n" +
                        "(D) He’s drinking from a mug."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u3_c2,
                R.raw.u3_c2,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một số bụi cây đang bị phủ tuyết.",
                        "Một số bông hoa đang được trồng.",
                        "Một người đang đi trên đường.",
                        "Một người đang lau một số cửa sổ."
                ),
                0,
                "Một người đang lau một số cửa sổ.",
                "A person is cleaning some windows.",
                "(A) Some bushes are covered with snow.\n" +
                        "(B) Some flowers are being planted.\n" +
                        "(C) A person is walking in the road.\n" +
                        "(D) A person is cleaning some windows."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u3_c3,
                R.raw.u3_c3,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một người đàn ông đang thay lốp xe.",
                        "Một người đàn ông đang mở cửa xe.",
                        "Một người đàn ông đang đổ nhiên liệu vào xe của mình.",
                        "Một người đàn ông đang trải một tấm bản đồ lên trên nóc xe của mình."
                ),
                3,
                "Một người đàn ông đang trải một tấm bản đồ lên trên nóc xe của mình.",
                "A man is spreading out a map on top of his car.",
                "(A) A man is changing a tire on his car.\n" +
                        "(B) A man is opening a car door.\n" +
                        "(C) A man is putting fuel into his car.\n" +
                        "(D) A man is spreading out a map on top of his car."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u3_c4,
                R.raw.u3_c4,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Họ đang rời khỏi một nhà hàng.",
                        "Họ đang ngồi cạnh nhau.",
                        "Một trong những người phụ nữ đang nhìn vào túi xách của mình.",
                        "Một trong những người phụ nữ đang gấp một chiếc khăn quàng."
                ),
                1,
                "Một trong những người phụ nữ đang nhìn vào túi xách của mình.",
                "One of the women is looking in her handbag.",
                "(A) They’re leaving a restaurant.\n" +
                        "(B) They’re seated next to each other.\n" +
                        "(C) One of the women is looking in her handbag.\n" +
                        "(D) One of the women is folding a scarf."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u3_c5,
                R.raw.u3_c5,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một số loại hành lý đang được trưng bày.",
                        "Một chiếc đèn và một vài tờ giấy đang ở trên bàn.",
                        "Một vài chiếc hộp được sắp xếp bên dưới một số chiếc đèn.",
                        "Một số sợi dây đã được cuộn lại trên sàn."
                ),
                2,
                "Một vài chiếc hộp được sắp xếp bên dưới một số chiếc đèn.",
                "Some boxes are arranged under some lamps.",
                "(A) A selection of luggage is on display.\n" +
                        "(B) A lamp and some papers are on a desk.\n" +
                        "(C) Some boxes are arranged under some lamps.\n" +
                        "(D) Some wire has been rolled up on the floor."
        ));

        list.add(new ListeningQuestion(
                R.drawable.u3_c6,
                R.raw.u3_c6,
                "Hình nào phù hợp với mô tả?",
                Arrays.asList(
                        "Một người đi xe đạp đang đạp xe ngang qua một người đi bộ.",
                        "Một chiếc lều được dựng cạnh một cái hồ.",
                        "Một số người đang nghỉ ngơi trên một bức tường đá.",
                        "Một số người đang bơi trong hồ."
                ),
                0,
                "Một người đi xe đạp đang đạp xe ngang qua một người đi bộ.",
                "A cyclist is riding past a pedestrian.",
                "(A) A cyclist is riding past a pedestrian.\n" +
                        "(B) A tent is set up next to a lake.\n" +
                        "(C) Some people are resting on a stone wall.\n" +
                        "(D) Some people are swimming in a lake."
        ));

        return list;
    }
}
