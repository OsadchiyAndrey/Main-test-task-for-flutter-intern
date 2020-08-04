package ua.osadchiy.task;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws ParseException {
        Course course =
                new Course("https://www.udemy.com/course/flutter-mobile-development/");
        Map<String, Date> map = course.getInfoAboutVideo();
        map = Course.sortByValue(map);
        Course.print(map);
    }


}
