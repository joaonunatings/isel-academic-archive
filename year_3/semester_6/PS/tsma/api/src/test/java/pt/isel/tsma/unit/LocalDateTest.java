package pt.isel.tsma.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

public class LocalDateTest {

	@Test
	public void addDays_next_month_test() {
		LocalDate date = LocalDate.of(2020, 1, 31);
		LocalDate newDate = date.plusDays(1);
		assert newDate.getMonthValue() == 2;
	}

	@Test
	public void toString_test() {
		LocalDate date = LocalDate.of(2020, 1, 31);
		assert date.toString().equals("2020-01-31");
	}

	@Test
	@DisplayName("Two LocalTime's go over midnight")
	void twoLocalTimeSGoOverMidnight() {
		LocalTime start = LocalTime.of(16, 0);
		LocalTime end = LocalTime.of(0, 15);
		System.out.println("Difference is: " + start.compareTo(end));
	}
}
