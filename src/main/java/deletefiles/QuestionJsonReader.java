package deletefiles;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuestionJsonReader {
    private static final String[] diffs = "easy,medium,hard,very hard,extremely hard".trim().toLowerCase().split(",");
    private static final String[] categories = "Arrays,Binary Search Trees,Binary Tree,Dynamic Programming,Famous Algorithms,Graphs,Heaps,Linked Lists,Recursion,Searching,Sorting,Stacks,Strings,Tries".trim().toLowerCase().split(",");
    public static void main(String[] args) {
        try {
            final ArrayList<Question> questions = getAllJSONs("E:\\bck\\Algoexpert\\Problems");
            final HashMap<String, ArrayList<Integer>> questionsByDifficulty = new HashMap<>();
            for (String diff : diffs) questionsByDifficulty.put(diff, questionsByDifficultyInit(questions, diff));
            final HashMap<String, ArrayList<Integer>> questionsByCategory = new HashMap<>();
            for (String cat : categories) questionsByCategory.put(cat, questionsByCategoryInit(questions, cat));

            System.out.println(catCount + " " +(catCount == 100) +"; "+ (diffCount == 100) +"; "+ (questions.size() == 100));
            String baseFolderLoc = "file:///E:/bck/Algoexpert/Problems/";

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    static int catCount = 0, diffCount = 0;
    private static ArrayList<Integer> questionsByCategoryInit(final ArrayList<Question> questions, String category) {
        category = category.trim().toLowerCase();
        boolean found = false;

        for (String s : categories) {
            if (category.equals(s.trim())) {
                found = true;
                break;
            }
        }

        if (!found) throw new IllegalArgumentException("Could not find " + category);

        ArrayList<Integer> ret = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++)
            if (questions.get(i).type.trim().toLowerCase().equals(category))
                ret.add(i);

        ArrayList<Integer> e = new ArrayList<>(),
                m = new ArrayList<>(),
                h = new ArrayList<>(),
                vh = new ArrayList<>(),
                eh = new ArrayList<>();

        Question q;
        for (Integer i : ret) {
            q = questions.get(i);
            switch (q.difficulty.trim().toLowerCase()) {
                case "easy":
                    e.add(i);
                case "medium":
                    m.add(i);
                case "hard":
                    h.add(i);
                case "very hard":
                    vh.add(i);
                default:
                    eh.add(i);
            }
        }

        ret = new ArrayList<>(e);
        ret.addAll(m);
        ret.addAll(h);
        ret.addAll(vh);
        ret.addAll(eh);
        catCount += ret.size();
        System.out.println(category + " " + ret.size());
//        for (Integer i : ret) {
//            System.out.println(questions.get(i).name + " is " + questions.get(i).difficulty);
//        }
        return ret.size() != 0 ? ret : null;
    }

    private static ArrayList<Integer> questionsByDifficultyInit(final ArrayList<Question> questions, String difficulty) {
        difficulty = difficulty.trim().toLowerCase();
        boolean found = false;

        for (String s : diffs) {
            if (difficulty.equals(s.trim())) {
                found = true;
                break;
            }
        }

        if (!found) throw new IllegalArgumentException("Could not find " + difficulty);

        ArrayList<Integer> ret = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++)
            if (questions.get(i).difficulty.trim().toLowerCase().equals(difficulty)) {
                diffCount++;
                ret.add(i);
            }

        return ret.size() != 0 ? ret : null;
    }

    private static ArrayList<Question> getAllJSONs(String folder) throws IOException, ParseException {
        List<String> paths = files(new File(folder));
        ArrayList<Question> questions = new ArrayList<>();
        if (paths != null) {
            for (String p : paths) questions.add(new Question(new File(p)));
            return questions;
        }
        return null;
    }

    private static List<String> files(File folder) {
        try (Stream<Path> walk = Files.walk(Paths.get(folder.getAbsolutePath()))) {

            return walk.map(Path::toString)
                    .filter(f -> f.endsWith(".json"))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final class Question {
        String type;
        String name;
        String difficulty;
        String question;
        HashMap<String, String> testCases;

        public Question(File json) throws IOException, ParseException {
            JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader(json));
            this.type = (String) obj.get("type");
            this.name = (String) obj.get("name");
            this.difficulty = (String) obj.get("difficulty");
            this.question = (String) obj.get("question");
            this.testCases = ((HashMap<String, String>) obj.get("testCases"));
        }

        @Override
        public String toString() {
            return "Question{" +
                    "type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", difficulty='" + difficulty + '\'' +
                    ", question='" + question + '\'' +
                    ", testCases=" + testCases.get("Java") +
                    '}';
        }
    }
}
