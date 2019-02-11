package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000);
//        .toLocalDate();
//        .toLocalTime();
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(
            List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with correctly exceeded field
        if (null == mealList || 0 == mealList.size()) {
            return new ArrayList<>();
        }

        List<UserMealWithExceed> resultList = new ArrayList<>();

        Collections.sort(mealList, userMealComparator);
        LocalDate tempDate = mealList.get(0).getDateTime().toLocalDate();
        int accumulator = 0;
        int i = 0;
        for (UserMeal userMeal: mealList
             ) {
            if (i >= mealList.size() || !mealList.get(i).getDateTime().toLocalDate().isEqual(tempDate)) {
                if (mealList.get(i - 1).getDateTime().toLocalTime().isAfter(startTime) && mealList.get(i - 1).getDateTime().toLocalTime().isBefore(endTime)) {
                    resultList.add(
                            new UserMealWithExceed(
                                    mealList.get(i - 1).getDateTime(),
                                    "abc",
                                    accumulator,
                                    accumulator > caloriesPerDay));
                }
                accumulator = 0;
            }
            tempDate = mealList.get(i).getDateTime().toLocalDate();
            accumulator += mealList.get(i).getCalories();
            i++;
        }
        resultList.add(
                new UserMealWithExceed(
                        mealList.get(mealList.size() - 1).getDateTime(),
                        "abc",
                        accumulator,
                        accumulator > caloriesPerDay));

        return resultList;
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded2(
            List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        if (null == mealList || 0 == mealList.size()) {
            return new ArrayList<>();
        }
        Map<LocalDate, List<UserMeal>> groups = mealList.stream()
                .collect(Collectors.groupingBy(item -> item.getDateTime().toLocalDate()));
        return groups.entrySet()
                .stream().map(item -> new UserMealWithExceed(
                        item.getValue().get(0).getDateTime(),
                        "abc",
                        item.getValue().stream().collect(Collectors.summingInt(i -> i.getCalories())),
                        item.getValue().stream().collect(Collectors.summingInt(i -> i.getCalories())) > caloriesPerDay)).collect(Collectors.toList());
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded3(
            List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        /*if (null == mealList || 0 == mealList.size()) {
            return new ArrayList<>();
        }*/
        Map<LocalDate, Integer> caloriesSumByDate = mealList.stream()
                .collect(
                        Collectors.groupingBy(item -> item.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));
        return mealList.stream()
                .filter(item -> TimeUtil.isBetween(item.getDateTime().toLocalTime(), startTime, endTime))
                .map(item -> new UserMealWithExceed(item.getDateTime(), item.getDescription(), item.getCalories(),
                        caloriesSumByDate.get(item.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static Comparator<UserMeal> userMealComparator = UserMealsUtil::compare;

    private static int compare(UserMeal o1, UserMeal o2) {
        LocalDateTime date1 = o1.getDateTime();
        LocalDateTime date2 = o2.getDateTime();
        return date1.compareTo(date2);
    }

    /*
    Реализовать метод UserMealsUtil.getFilteredWithExceeded через циклы (`forEach`):
-  должны возвращаться только записи между startTime и endTime
-  поле UserMealWithExceed.exceed должно показывать,
                                     превышает ли сумма калорий за весь день параметра метода caloriesPerDay

Т.е UserMealWithExceed - это запись одной еды, но поле exceeded будет одинаково для всех записей за этот день.

- Проверьте результат выполнения ДЗ (можно проверить логику в http://topjava.herokuapp.com , список еды)
- Оцените Time complexity алгоритма. Если она больше O(N), например O(N*N) или N*log(N), сделайте O(N).
    */

    /*
    Optional (Java 8 Stream API)
Сделать реализацию через Java 8 Stream API.
    */

    /*
    Optional 2 (+5 бонусов)
Сделать реализацию со сложностью O(N):
- циклом за 1 проход по List<UserMeal>. Обратите внимание на п.13 замечаний
- через Stream API за 1 проход по исходному списку Stream<UserMeal> meals
  - возможно дополнительные проходы по частям списка
  - нельзя использовать внешние коллекции, не являющиеся частью коллектора или результатами работы stream
Замечания по использованию Stream API:
Когда встречаешь что-то непривычное, приходится перестраивать мозги. Например, переход с процедурного на ООП программирование дается непросто. Те, кто не знает шаблонов (и не хотят учить) также их встречают плохо. Хорошая новость в том, что если это принять и начать использовать, то начинаешь получать от этого удовольствие. И тут главное не впасть в другую крайность:
Используйте Stream API проще (или не используйте вообще)
Если вас беспокоить производительность стримов, обязательно прочитайте про оптимизацию
"Что? Где? Когда?"
Перформанс: что в имени тебе моём?
Performance это праздник
При использовании Stream API производительность улучшиться только на больших задачах, где возможно распараллеливание. Еще - просто так запустить и померять скорость JVM нельзя (как минимум дать прогреться и запустить очень большое число раз). Лучше использовать какие-нибудь бенчмарки, например JMH, который мы юзаем на другом проекте (Mastejava).

error Замечания к HW0
1: Код проекта менять можно! Одна из распространенных ошибок как в тестовых заданиях на собеседовании, так и при работе на проекте, что ничего нельзя менять. Конечно при правках в рабочем проекте обязательно нужно проконсультироваться/проревьюироваться у авторов кода (находится по истории VCS)
2: Наследовать UserMealWithExceed от UserMeal я не буду, т.к. это разные сущности: Transfer Object и Entity. Мы будет их проходить на 2м уроке.
3: Правильная реализация должна быть простой и красивой, можно сделать 2-мя способами: через стримы и через циклы. Сложность должна быть O(N), т.е. без вложенных стримов и циклов.
4: При реализации через циклы посмотрите в Map на методы getOrDefault или merge
5: При реализации через Stream заменяйте forEach оператором stream.map(..)
6: Объявляйте переменные непосредственно перед использованием (если возможно - сразу с инициализацией). При объявлении коллекций используйте тип переменной - интерфейс (Map, List, ..)
7: Если IDEA предлагает оптимизацию (желтым подчеркивает), например заменить лямбду на метод-референс, соглашайтесь (Alt+Enter)
8: Пользуйтесь форматированием кода в IDEA: Alt+Ctrl+L
9: Перед check-in проверяйте чендж-лист (курсор на файл и Ctrl+D): не оставляйте в коде ничего лишнего (закомментированный код, TODO и пр.). Если файл не меняется (например только пробелы или переводы строк), не надо его чекинить, делайте ему revert (Git -> Revert / Ctrl+Alt+Z).
10: System.out.println нельзя делать нигде, кроме как в main. Позже введем логирование.
11: Результаты, возвращаемые UserMealsUtil.getFilteredWithExceeded мы будем использовать в нашем приложении для фильтрации по времени и отображения еды правильным цветом.
12: Обращайте внимание на комментарии к вашим коммитам в git. Они должны быть короткие и информативные (лучше на english)
13: Не полагайтесь в решении на то, что список будет подаваться отсортированным. Такого условия нет.
    */
}
