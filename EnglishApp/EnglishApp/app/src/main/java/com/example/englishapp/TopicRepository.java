package com.example.englishapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TopicRepository {

    public static final String TOPIC_ANIMALS = "animals";
    public static final String TOPIC_PLANTS = "plants";
    public static final String TOPIC_CLOTHES = "clothes";
    public static final String TOPIC_KITCHEN = "kitchen";
    public static final String TOPIC_FOOD = "food";
    public static final String TOPIC_FESTIVAL = "festival";
    public static final String TOPIC_FRUIT = "fruit";
    public static final String TOPIC_VEHICLE = "vehicle";
    public static final String TOPIC_TECH = "technology";

    private static final Map<String, List<TopicWord>> WORDS_BY_TOPIC = createWords();
    private static final List<Topic> TOPICS = createTopics();

    private TopicRepository() {
    }

    public static List<Topic> getTopics() {
        List<Topic> copy = new ArrayList<>();
        for (Topic topic : TOPICS) {
            copy.add(copyTopic(topic));
        }
        return copy;
    }

    public static Topic getTopicById(String topicId) {
        for (Topic topic : TOPICS) {
            if (topic.id.equals(topicId)) {
                return copyTopic(topic);
            }
        }
        return null;
    }

    public static List<TopicWord> getWordsForTopic(String topicId) {
        List<TopicWord> words = WORDS_BY_TOPIC.get(topicId);
        return words == null ? new ArrayList<>() : new ArrayList<>(words);
    }

    private static Topic copyTopic(Topic topic) {
        return new Topic(topic.id, topic.name, topic.image, topic.total, topic.learned);
    }

    private static List<Topic> createTopics() {
        List<Topic> topics = new ArrayList<>();
        topics.add(topic(TOPIC_ANIMALS, "Động vật", R.drawable.animal));
        topics.add(topic(TOPIC_PLANTS, "Cây cối", R.drawable.tree));
        topics.add(topic(TOPIC_CLOTHES, "Quần áo", R.drawable.clothes));
        topics.add(topic(TOPIC_KITCHEN, "Vật dụng bếp", R.drawable.kitchen));
        topics.add(topic(TOPIC_FOOD, "Đồ ăn", R.drawable.food));
        topics.add(topic(TOPIC_FESTIVAL, "Lễ hội", R.drawable.festival));
        topics.add(topic(TOPIC_FRUIT, "Trái cây", R.drawable.fruit));
        topics.add(topic(TOPIC_VEHICLE, "Phương tiện", R.drawable.vehicle));
        topics.add(topic(TOPIC_TECH, "Công nghệ", R.drawable.tech));
        return topics;
    }

    private static Topic topic(String id, String name, int imageRes) {
        return new Topic(id, name, imageRes, getWordsForTopic(id).size(), 0);
    }

    private static Map<String, List<TopicWord>> createWords() {
        Map<String, List<TopicWord>> map = new LinkedHashMap<>();

        map.put(TOPIC_ANIMALS, Arrays.asList(
                word(TOPIC_ANIMALS, "cat", "noun", "/kæt/", "/kæt/", "con mèo", "The cat is sleeping on the sofa.", R.drawable.cat),
                word(TOPIC_ANIMALS, "dog", "noun", "/dɒɡ/", "/dɔːɡ/", "con chó", "My dog runs to the door every morning.", R.drawable.dog),
                word(TOPIC_ANIMALS, "elephant", "noun", "/ˈel.ɪ.fənt/", "/ˈel.ə.fənt/", "con voi", "The elephant has a long trunk.", R.drawable.elephant),
                word(TOPIC_ANIMALS, "rabbit", "noun", "/ˈræb.ɪt/", "/ˈræb.ɪt/", "con thỏ", "A white rabbit is eating a carrot.", R.drawable.rabbit),
                word(TOPIC_ANIMALS, "tiger", "noun", "/ˈtaɪ.ɡə(r)/", "/ˈtaɪ.ɡɚ/", "con hổ", "The tiger looks strong and fast.", R.drawable.tiger),

                word(TOPIC_ANIMALS, "lion", "noun", "/ˈlaɪ.ən/", "/ˈlaɪ.ən/", "sư tử", "The lion is the king of the jungle.", R.drawable.lion),
                word(TOPIC_ANIMALS, "bear", "noun", "/beə(r)/", "/ber/", "con gấu", "The bear is looking for food.", R.drawable.bear),
                word(TOPIC_ANIMALS, "monkey", "noun", "/ˈmʌŋ.ki/", "/ˈmʌŋ.ki/", "con khỉ", "The monkey climbs the tree quickly.", R.drawable.monkey),
                word(TOPIC_ANIMALS, "horse", "noun", "/hɔːs/", "/hɔːrs/", "con ngựa", "The horse runs very fast.", R.drawable.horse),
                word(TOPIC_ANIMALS, "bird", "noun", "/bɜːd/", "/bɝːd/", "con chim", "The bird is flying in the sky.", R.drawable.bird)
        ));

        map.put(TOPIC_PLANTS, Arrays.asList(
                word(TOPIC_PLANTS, "tree", "noun", "/triː/", "/triː/", "cây", "That tree gives us cool shade.", R.drawable.tree2),
                word(TOPIC_PLANTS, "flower", "noun", "/ˈflaʊ.ə(r)/", "/ˈflaʊ.ɚ/", "hoa", "This flower smells very sweet.", R.drawable.flower),
                word(TOPIC_PLANTS, "leaf", "noun", "/liːf/", "/liːf/", "lá cây", "A green leaf fell to the ground.", R.drawable.leaf),
                word(TOPIC_PLANTS, "root", "noun", "/ruːt/", "/ruːt/", "rễ cây", "The root takes water from the soil.", R.drawable.root),
                word(TOPIC_PLANTS, "seed", "noun", "/siːd/", "/siːd/", "hạt giống", "Plant the seed in wet soil.", R.drawable.seed)
        ));

        map.put(TOPIC_CLOTHES, Arrays.asList(
                word(TOPIC_CLOTHES, "shirt", "noun", "/ʃɜːt/", "/ʃɝːt/", "áo sơ mi", "He wears a blue shirt to work.", R.drawable.shirt),
                word(TOPIC_CLOTHES, "dress", "noun", "/dres/", "/dres/", "váy", "She bought a new dress for the party.", R.drawable.dress),
                word(TOPIC_CLOTHES, "jacket", "noun", "/ˈdʒæk.ɪt/", "/ˈdʒæk.ɪt/", "áo khoác", "Take your jacket because it is cold.", R.drawable.jacket),
                word(TOPIC_CLOTHES, "jeans", "noun", "/dʒiːnz/", "/dʒiːnz/", "quần jeans", "These jeans are very comfortable.", R.drawable.jeans),
                word(TOPIC_CLOTHES, "shoes", "noun", "/ʃuːz/", "/ʃuːz/", "giày", "Her shoes match the bag.", R.drawable.shoes)
        ));

        map.put(TOPIC_KITCHEN, Arrays.asList(
                word(TOPIC_KITCHEN, "knife", "noun", "/naɪf/", "/naɪf/", "dao", "Use the knife carefully in the kitchen.", R.drawable.knife),
                word(TOPIC_KITCHEN, "spoon", "noun", "/spuːn/", "/spuːn/", "muỗng", "I need a spoon for the soup.", R.drawable.spoon),
                word(TOPIC_KITCHEN, "pan", "noun", "/pæn/", "/pæn/", "chảo", "The eggs are cooking in the pan.", R.drawable.pan),
                word(TOPIC_KITCHEN, "bowl", "noun", "/bəʊl/", "/boʊl/", "bát", "She puts fruit in a glass bowl.", R.drawable.bowl),
                word(TOPIC_KITCHEN, "fridge", "noun", "/frɪdʒ/", "/frɪdʒ/", "tủ lạnh", "The milk is in the fridge.", R.drawable.fridge)
        ));

        map.put(TOPIC_FOOD, Arrays.asList(
                word(TOPIC_FOOD, "rice", "noun", "/raɪs/", "/raɪs/", "cơm", "We eat rice for lunch every day.", R.drawable.rice),
                word(TOPIC_FOOD, "bread", "noun", "/bred/", "/bred/", "bánh mì", "Fresh bread smells delicious.", R.drawable.bread),
                word(TOPIC_FOOD, "soup", "noun", "/suːp/", "/suːp/", "súp", "Hot soup is perfect on rainy days.", R.drawable.soup),
                word(TOPIC_FOOD, "salad", "noun", "/ˈsæl.əd/", "/ˈsæl.əd/", "xà lách trộn", "This salad has tomatoes and lettuce.", R.drawable.salad),
                word(TOPIC_FOOD, "noodles", "noun", "/ˈnuː.dəlz/", "/ˈnuː.dəlz/", "mì sợi", "He likes spicy noodles.", R.drawable.noodles)
        ));

        map.put(TOPIC_FESTIVAL, Arrays.asList(
                word(TOPIC_FESTIVAL, "lantern", "noun", "/ˈlæn.tən/", "/ˈlæn.tɚn/", "đèn lồng", "Children carry colorful lanterns.", R.drawable.lantern),
                word(TOPIC_FESTIVAL, "parade", "noun", "/pəˈreɪd/", "/pəˈreɪd/", "diễu hành", "The parade starts at 8 p.m.", R.drawable.parade),
                word(TOPIC_FESTIVAL, "costume", "noun", "/ˈkɒs.tjuːm/", "/ˈkɑː.stuːm/", "trang phục", "Her costume is bright and beautiful.", R.drawable.costume),
                word(TOPIC_FESTIVAL, "fireworks", "noun", "/ˈfaɪə.wɜːks/", "/ˈfaɪr.wɝːks/", "pháo hoa", "We watched fireworks in the sky.", R.drawable.fireworks),
                word(TOPIC_FESTIVAL, "mooncake", "noun", "/ˈmuːn.keɪk/", "/ˈmuːn.keɪk/", "bánh trung thu", "My family shares mooncake at night.", R.drawable.mooncake)
        ));

        map.put(TOPIC_FRUIT, Arrays.asList(
                word(TOPIC_FRUIT, "apple", "noun", "/ˈæp.əl/", "/ˈæp.əl/", "quả táo", "An apple is on the table.", R.drawable.apple),
                word(TOPIC_FRUIT, "banana", "noun", "/bəˈnɑː.nə/", "/bəˈnæn.ə/", "quả chuối", "Banana is my favorite snack.", R.drawable.banana),
                word(TOPIC_FRUIT, "orange", "noun", "/ˈɒr.ɪndʒ/", "/ˈɔːr.ɪndʒ/", "quả cam", "This orange tastes sweet.", R.drawable.orange),
                word(TOPIC_FRUIT, "mango", "noun", "/ˈmæŋ.ɡəʊ/", "/ˈmæŋ.ɡoʊ/", "quả xoài", "We drink mango juice in summer.", R.drawable.mango),
                word(TOPIC_FRUIT, "grape", "noun", "/ɡreɪp/", "/ɡreɪp/", "quả nho", "Purple grapes are in the basket.", R.drawable.grape)
        ));

        map.put(TOPIC_VEHICLE, Arrays.asList(
                word(TOPIC_VEHICLE, "bicycle", "noun", "/ˈbaɪ.sɪ.kəl/", "/ˈbaɪ.sə.kəl/", "xe đạp", "She rides her bicycle to school.", R.drawable.bicycle),
                word(TOPIC_VEHICLE, "car", "noun", "/kɑː(r)/", "/kɑːr/", "ô tô", "Their car is parked outside.", R.drawable.car),
                word(TOPIC_VEHICLE, "bus", "noun", "/bʌs/", "/bʌs/", "xe buýt", "The bus arrives every ten minutes.", R.drawable.bus),
                word(TOPIC_VEHICLE, "train", "noun", "/treɪn/", "/treɪn/", "tàu hỏa", "We travel by train on holidays.", R.drawable.train),
                word(TOPIC_VEHICLE, "airplane", "noun", "/ˈeə.pleɪn/", "/ˈer.pleɪn/", "máy bay", "The airplane is flying above the clouds.", R.drawable.airplane)
        ));

        map.put(TOPIC_TECH, Arrays.asList(
                word(TOPIC_TECH, "laptop", "noun", "/ˈlæp.tɒp/", "/ˈlæp.tɑːp/", "máy tính xách tay", "My laptop helps me study online.", R.drawable.laptop),
                word(TOPIC_TECH, "keyboard", "noun", "/ˈkiː.bɔːd/", "/ˈkiː.bɔːrd/", "bàn phím", "The keyboard is next to the monitor.", R.drawable.keyboard),
                word(TOPIC_TECH, "mouse", "noun", "/maʊs/", "/maʊs/", "chuột máy tính", "Click the icon with the mouse.", R.drawable.mouse),
                word(TOPIC_TECH, "phone", "noun", "/fəʊn/", "/foʊn/", "điện thoại", "Her phone rings during class.", R.drawable.phone),
                word(TOPIC_TECH, "internet", "noun", "/ˈɪn.tə.net/", "/ˈɪn.t̬ɚ.net/", "mạng internet", "We use the internet to find information.", R.drawable.internet)
        ));

        return map;
    }

    private static TopicWord word(String topicId,
                                  String word,
                                  String type,
                                  String uk,
                                  String us,
                                  String meaning,
                                  String example,
                                  int imageRes) {
        return new TopicWord(topicId, word, type, uk, us, meaning, example, imageRes);
    }
}

