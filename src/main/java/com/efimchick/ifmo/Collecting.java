package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;
import com.efimchick.ifmo.util.Person;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public int sum(IntStream intStream) {
        return intStream.sum();
    }

    public int production(IntStream intStream) {
        return intStream.reduce(1, (a, b) -> a * b);
    }

    public int oddSum(IntStream intStream) {
        return intStream.filter(i -> i % PARITY_DETERMINANT != 0).sum();
    }

    public Map<Integer, Integer> sumByRemainder(int divider, IntStream intStream) {
        return intStream
            .boxed()
            .collect(Collectors.groupingBy(x -> x % divider, Collectors.summingInt(e -> e)));
    }

    public Map<Person, Double> totalScores(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());
        long coursesAmount = coursesAmount(courseResultList);
        return courseResultList
            .stream()
            .collect(Collectors.groupingBy(
                CourseResult::getPerson,
                Collectors.summingDouble(courseResult -> (double) courseResult
                    .getTaskResults()
                    .values()
                    .stream()
                    .reduce(Integer::sum)
                    .orElse(0) / coursesAmount)
            ));
    }

    private static long coursesAmount(List<CourseResult> courseResultList) {
        return courseResultList
            .stream()
            .map(s -> s.getTaskResults().keySet())
            .flatMap(Collection::stream)
            .distinct()
            .count();
    }

    public double averageTotalScore(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());
        long coursesAmount = coursesAmount(courseResultList);
        int courseResultSize = courseResultList.size();
        return courseResultList
            .stream()
            .mapToDouble(courseResult -> (double) courseResult
                .getTaskResults()
                .values()
                .stream()
                .reduce(Integer::sum)
                .orElse(0) / (coursesAmount * courseResultSize)
            )
            .sum();
    }

    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> courseResultStream) {
        List<CourseResult> courseResultList = courseResultStream.collect(Collectors.toList());
        double numberOfPersons = courseResultList.size();
        Map<String, Integer> scoresPerTask = courseResultList
            .stream()
            .map(CourseResult::getTaskResults)
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
        return scoresPerTask.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
            value -> value.getValue() / numberOfPersons));
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

    private String getMark(Double score) {
        String mark;
        if (score > CHECK_TOTAL_SCORE || score < 0) {
            mark = "Error";
        } else if (score > SCORE_TO_A_MARK) {
            mark = "A";
        } else if (score >= SCORE_TO_B_MARK) {
            mark = "B";
        } else if (score >= SCORE_TO_C_MARK) {
            mark = "C";
        } else if (score >= SCORE_TO_D_MARK) {
            mark = "D";
        } else if (score >= SCORE_TO_E_MARK) {
            mark = "E";
        } else {
            mark = "F";
        }
        return mark;
    }

    public String easiestTask(Stream<CourseResult> courseResultStream) {
        Map<String, Double> averageScorePerTask = averageScoresPerTask(courseResultStream);
        return Collections.max(averageScorePerTask.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}
