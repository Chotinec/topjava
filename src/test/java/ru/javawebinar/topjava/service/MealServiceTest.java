package ru.javawebinar.topjava.service;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    private static long startTime;
    private static long endTime;

    static {
        SLF4JBridgeHandler.install();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        private long timeStart;
        private long timeEnd;

        protected void starting(Description description) {
            timeStart = System.currentTimeMillis();
            System.out
                    .println("===========================================================================");
            System.out.println("Test: " + description.getMethodName());
            System.out.println("Start Time: " + Calendar.getInstance().getTime());
            System.out
                    .println("===========================================================================");
        }

        protected void finished(Description description) {
            timeEnd = System.currentTimeMillis();
            double seconds = (timeEnd-timeStart)/1000.0;
            System.out
                    .println("\n===========================================================================");
            System.out
                    .println("Test completed - ran in: "+new DecimalFormat("0.000").format(seconds)+" sec");
            System.out
                    .println("===========================================================================\n");

        }
    };

    @Autowired
    private MealService service;

    @BeforeClass
    public static void recordStartTime() {
        startTime = System.currentTimeMillis();
    }

    @AfterClass
    public static void recordEndAndExecutionTime() {
        endTime = System.currentTimeMillis();
        double seconds = (startTime-endTime)/1000.0;
        System.out.println("Last testcase exection time in millisecond : " + (endTime - startTime));
        System.out
                .println("\n===========================================================================");
        System.out
                .println("Test class "  +  MealServiceTest.class.getName() + " completed - ran in: "+new DecimalFormat("0.000").format(seconds)+" sec");
        System.out
                .println("===========================================================================\n");
    }

    @Test
    public void delete() throws Exception {
        service.delete(MEAL1_ID, USER_ID);
        assertMatch(service.getAll(USER_ID), MEAL6, MEAL5, MEAL4, MEAL3, MEAL2);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFound() throws Exception {
        service.delete(MEAL1_ID, 1);
    }

    @Test
    public void create() throws Exception {
        Meal created = getCreated();
        service.create(created, USER_ID);
        assertMatch(service.getAll(USER_ID), created, MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1);
    }

    @Test
    public void get() throws Exception {
        Meal actual = service.get(ADMIN_MEAL_ID, ADMIN_ID);
        assertMatch(actual, ADMIN_MEAL1);
    }

    //@Test(expected = NotFoundException.class)
    @Ignore
    public void getNotFound() throws Exception {
        thrown.expect(NotFoundException.class);
        service.get(MEAL1_ID, ADMIN_ID);
    }

    @Test
    public void update() throws Exception {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(MEAL1_ID, USER_ID), updated);
    }

    //@Test(expected = NotFoundException.class)
    @Ignore
    public void updateNotFound() throws Exception {
        thrown.expect(NotFoundException.class);
        service.update(MEAL1, ADMIN_ID);
    }

    @Test
    public void getAll() throws Exception {
        assertMatch(service.getAll(USER_ID), MEALS);
    }

    @Test
    public void getBetween() throws Exception {
        assertMatch(service.getBetweenDates(
                LocalDate.of(2015, Month.MAY, 30),
                LocalDate.of(2015, Month.MAY, 30), USER_ID), MEAL3, MEAL2, MEAL1);
    }
}