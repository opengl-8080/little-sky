package littlesky;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SunriseSunsetTime {
    private final LocalDate localDate;
    private final TimeZone timeZone;
    private final SunriseSunsetCalculator calculator;
    
    public SunriseSunsetTime(City city, LocalDate localDate) {
        this.localDate = localDate;
        this.timeZone = city.timeZone();
        this.calculator = new SunriseSunsetCalculator(city.location(), this.timeZone);
    }
    
    public LocalTime sunriseTime() {
        Calendar today = this.toCalendar(this.localDate);
        Calendar sunriseTime = this.calculator.getOfficialSunriseCalendarForDate(today);

        return this.toLocalTime(sunriseTime);
    }
    
    public LocalTime sunsetTime() {
        Calendar today = this.toCalendar(this.localDate);
        Calendar sunsetTime = this.calculator.getOfficialSunsetCalendarForDate(today);

        return this.toLocalTime(sunsetTime);
    }
    
    private Calendar toCalendar(LocalDate localDate) {
        Date date = Date.from(localDate.atStartOfDay(this.timeZone.toZoneId()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
    
    private LocalTime toLocalTime(Calendar calendar) {
        return calendar.getTime().toInstant().atZone(this.timeZone.toZoneId()).toLocalTime();
    }
}
