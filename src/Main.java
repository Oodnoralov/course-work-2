import exception.IncorrectArgumentException;
import exception.TaskNotFoundException;
import model.*;
import services.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    private static final TaskService taskService = new TaskService();
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}\\:\\d{2}");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public static void main(String[] args) {


        try (Scanner sc = new Scanner(System.in)) {
            goto1:
            while (true) {
                printMenu();
                System.out.println("Введите число:");
                if (sc.hasNextInt()) {

                    int number = sc.nextInt();
                    switch (number) {
                        case 1:
                            inputTaskScanner(sc);
                            break;
                        case 2:
                            removeTask(sc);
                            break;
                        case 3:
                            getAllByDay(sc);
                            break;
                        case 4:
                            break goto1;
                        //выход из цикла
                    }
                } else {
                    System.out.println("введите число, соответствующее пункту меню");
                }
            }

        }
    }
    private static void getAllByDay(Scanner sc) {
        System.out.println("введите дату в формате dd.MM.yyyy");
        if (sc.hasNext(DATE_PATTERN)) {
            String dateTime = sc.next(DATE_PATTERN);
            LocalDate inputDate = LocalDate.parse(dateTime, DATE_FORMATTER);

            Collection<Task> tasksByDay = taskService.getAllByDate(inputDate);
            for (Task task: tasksByDay) {
                System.out.println(task);
            }
        } else {
            System.out.println("введите дату и время задачи в формате ДД.мм.гггг");
            sc.close();



        }
    }
    private static void removeTask(Scanner sc) {
        System.out.println("введите ID задачи на удаление");
        int id = sc.nextInt();
        try {
            taskService.remove(id);
        } catch (TaskNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static String inputTaskTitle(Scanner sc) {
        System.out.println("Введите название задачи");
        String title = sc.next();
        if (title.isBlank()) {
            System.out.println("Необходимо ввести наименование задачи");
            sc.close();
        }
        return title;

    }

    private static String inputTaskDescription(Scanner sc) {
        System.out.println("введите описание задачи");
        String description = sc.next();
        if (description.isBlank()) {
            System.out.println("необходимо внести описание задачи");
            sc.close();
        }
        return description;

    }

    private static TaskType inputTaskType(Scanner sc) {
        System.out.println("введите тип задачи (1 - личная, 2- рабочая)");
        TaskType type = null;
        int taskTypeChoice = sc.nextInt();
        switch (taskTypeChoice) {
            case 1:
                type = TaskType.PERSONAL;
                break;
            case 2:
                type = TaskType.WORK;
                break;
            default:
                System.out.println("тип задачи введен некорректно");
                sc.close();

        }
        ;
        return type;

    }

    private static LocalDateTime inputTaskTime(Scanner sc) {
        System.out.println("введите дату и время задачи в формате ДД.мм.гггг ЧЧ:мм");

        if (sc.hasNext(DATE_TIME_PATTERN)) {
            String dateTime = sc.next(DATE_TIME_PATTERN);
            return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
        } else {
            System.out.println("введите дату и время задачи в формате ДД.мм.гггг ЧЧ:мм");
            sc.close();
            return null;


        }


    }

    private static int inputRepeatability(Scanner sc) {
        System.out.println("введите ппризнак повторяемости задачи (1 - однократно, 2 -" +
                "  каждый день, 3 - каждую неделю, 4 - каждый месяц, 5 - каждый год");
        if (sc.hasNextInt()) {
            return sc.nextInt();
        } else {
            System.out.println("введите признак повторяемости задачи");
            sc.close();
            return -1;
        }


    }


    private static void printMenu() {
        System.out.println(" 1. добавить задачу\n 2. удалить задачу \n 3. получить задачу на указанный день\n 4. выход");
    }

    private static void inputTaskScanner(Scanner sc) {
        sc.useDelimiter("\n");
        String title = inputTaskTitle(sc);
        String description = inputTaskDescription(sc);
        TaskType type = inputTaskType(sc);
        LocalDateTime taskTime = inputTaskTime(sc);
        int repeatable = inputRepeatability(sc);
        createTask(title, description, type, taskTime, repeatable);
    }

    private static void createTask(String title, String description, TaskType type, LocalDateTime taskTime,
        int repeatable)
        {
            Task task = null;

            try
            {
                switch (repeatable) {
                    case 1:
                        task = new OneTimeTask(title, description, type, taskTime);
                        break;
                    case 2:
                        task = new DailyTask(title, description, type, taskTime);
                        break;
                    case 3:
                        task = new WeeklyTask(title, description, type, taskTime);
                        break;
                    case 4:
                        task = new MonthlyTask(title, description, type, taskTime);
                        break;
                    case 5:
                        task = new YearlyTask(title, description, type, taskTime);
                        break;
                    default:
                        System.out.println("признак повторяемости введен неверно");

                }
            } catch (IncorrectArgumentException e) {
                System.out.println(e.getMessage());
            }


            if(task != null) {
                taskService.add(task);
                System.out.println("задача добавлена");
                System.out.println(task.toString());
            }
            else {
                System.out.println("данные введены некорректно");
            }

        }



    }
