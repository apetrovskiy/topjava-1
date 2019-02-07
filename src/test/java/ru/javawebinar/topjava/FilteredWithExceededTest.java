package ru.javawebinar.topjava;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;
import ru.javawebinar.topjava.util.UserMealsUtil;

public class FilteredWithExceededTest {

    // private final static MethodUnderTestVersion VERSION = MethodUnderTestVersion.FOR_EACH;
    private final static MethodUnderTestVersion VERSION = MethodUnderTestVersion.STREAMS;
    private List<UserMeal> inputList;
    private List<UserMealWithExceed> filteredList;

    @AfterEach
    void deleteList() {
        inputList = null;
    }

    @Test
    void shouldReturnEmptyListOnEmptyListInputted() {
        inputList = givenEmptyList();
        filteredList = whenFilteringTakesPlace();
        thenListIsEmpty();
    }

    @Test
    void shouldReturnListNotExceedingDailyNorm() {
        inputList = givenSampleList2015May();
        filteredList = whenFilteringTakesPlace();
        thenDoesNotExceed(LocalDateTime.of(2015, Month.MAY, 30,10,0));
    }

    @Test
    void shouldReturnListExceedingDailyNorm() {
        inputList = givenSampleList2015May();
        filteredList = whenFilteringTakesPlace();
        thenExceeds(LocalDateTime.of(2015, Month.MAY, 31,10,0));
    }

    private List<UserMeal> givenEmptyList() {
        return new ArrayList<>();
    }

    private List<UserMeal> givenSampleList2015May() {
        return Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
    }

    private List<UserMealWithExceed> whenFilteringTakesPlace() {
        return VERSION == MethodUnderTestVersion.FOR_EACH
            ? UserMealsUtil.getFilteredWithExceeded(
                inputList,
                LocalTime.of(7, 0),
                LocalTime.of(23,0),
                2000)
                : UserMealsUtil.getFilteredWithExceeded2(
                inputList,
                LocalTime.of(7, 0),
                LocalTime.of(23,0),
                2000);
    }

    private void thenListIsEmpty() {
        Assertions.assertEquals(0, filteredList.size());
    }

    private void thenExceeds(LocalDateTime dateTime) {
        Assertions.assertEquals(true,  getUserMealWithExceedFromList(dateTime).getExceed());
    }

    private void thenDoesNotExceed(LocalDateTime dateTime) {
        Assertions.assertEquals(false, getUserMealWithExceedFromList(dateTime).getExceed());
    }

    private UserMealWithExceed getUserMealWithExceedFromList(LocalDateTime dateTime) {
        Optional<UserMealWithExceed> userMeal = filteredList
                .stream()
                .filter(item -> item.getDateTime().toLocalDate().isEqual(dateTime.toLocalDate()))
                .findFirst();
        Assertions.assertTrue(userMeal.isPresent());
        return userMeal.get();
    }

    private enum MethodUnderTestVersion {
        FOR_EACH,
        STREAMS;
    }
}
