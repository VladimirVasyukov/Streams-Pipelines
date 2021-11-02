package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;
import com.efimchick.ifmo.util.Person;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Collecting {
    private static final byte PARITY_DETERMINANT = 2;
    private static final byte CHECK_TOTAL_SCORE = 100;
    private static final byte SCORE_TO_A_MARK = 90;
    private static final byte SCORE_TO_B_MARK = 83;
    private static final byte SCORE_TO_C_MARK = 75;
    private static final byte SCORE_TO_D_MARK = 68;
    private static final byte SCORE_TO_E_MARK = 60;
    private static final byte SCORE_TO_F_MARK = 60;

    public int sum(IntStream intStream) {
        return intStream.sum();
    }

    public int production(IntStream intStream) {
        return intStream.reduce(
            1,
            (a, b) -> a * b
        );
    }

    public int oddSum(IntStream intStream) {
        return intStream.filter(i -> i % PARITY_DETERMINANT != 0).sum();
    }

    public Map<Integer, Integer> sumByRemainder(int divider, IntStream intStream) {
        Map<Integer, Integer> sumByRemainder = new HashMap<>();
        intStream.forEach((int x) -> {
            int remainder = x % divider;
            int value = sumByRemainder.containsKey(remainder)
                ? (sumByRemainder.get(remainder) + x) : x;
            sumByRemainder.put(remainder, value);
        });
        return sumByRemainder;
    }

    public Map<Person, Double> totalScores(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());
        int courseCountSize = coursesCount(courseResultList.stream());
        return courseResultList
            .stream()
            .collect(Collectors.groupingBy(
                CourseResult::getPerson,
                Collectors.summingDouble(courseResult -> (double) courseResult
                    .getTaskResults()
                    .values()
                    .stream()
                    .reduce(Integer::sum)
                    .orElse(0) / courseCountSize)
            ));
    }

    private static int coursesCount(Stream<CourseResult> courseResultStream) {
        Set<String> coursesSet = new HashSet<>();
        courseResultStream.forEach(s -> s.getTaskResults().forEach((k, v) -> coursesSet.add(k)));
        return coursesSet.size();
    }

    public double averageTotalScore(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());
        int courseCountSize = coursesCount(courseResultList.stream());
        int courseResultSize = courseResultList.size();
        return courseResultList
            .stream()
            .mapToDouble(courseResult -> (double) courseResult
                .getTaskResults()
                .values()
                .stream()
                .reduce(Integer::sum)
                .orElse(0) / (courseCountSize * courseResultSize)
            )
            .sum();
    }

    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> courseResultStream) {
        Map<String, Double> taskScoreSum = new HashMap<>();
        Set<Person> personSet = new HashSet<>();

        courseResultStream.forEach((CourseResult courseResult) -> {
            personSet.add(courseResult.getPerson());
            courseResult.getTaskResults().forEach((String taskName, Integer taskResult) -> {
                Double value = taskScoreSum.containsKey(taskName)
                    ? (taskScoreSum.get(taskName) + taskResult) : taskResult;
                taskScoreSum.put(taskName, value);
            });
        });
        int numberOfPersons = personSet.size();
        return taskScoreSum
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue() / numberOfPersons));


    }

    public Map<Person, String> defineMarks(Stream<CourseResult> courseResultStream) {
        Map<Person, Double> totalScoresMap = totalScores(courseResultStream);
        return totalScoresMap
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(Map.Entry::getKey, v -> getMark(v.getValue()))
            );
    }

    public String getMark(Double score) {
        if (score > CHECK_TOTAL_SCORE || score < 0) {
            return null;
        }
        if (score > SCORE_TO_A_MARK) {
            return "A";
        }
        if (score >= SCORE_TO_B_MARK) {
            return "B";
        }
        if (score >= SCORE_TO_C_MARK) {
            return "C";
        }
        if (score >= SCORE_TO_D_MARK) {
            return "D";
        }
        if (score >= SCORE_TO_E_MARK) {
            return "E";
        }
        if (score < SCORE_TO_F_MARK) {
            return "F";
        }
        return null;
    }

    public String easiestTask(Stream<CourseResult> courseResultStream) {
        Map<String, Double> averageScorePerTask = averageScoresPerTask(courseResultStream);
        return averageScorePerTask
            .entrySet()
            .stream()
            .max((entry1, entry2) -> entry1.getValue() > entry2.getValue()
                ? 1 : -1).get().getKey();
    }
}
