package littlesky.model.sun;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import littlesky.model.location.UserLocation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SunriseSunsetTime {
    private final LocalDate localDate;
    private final SunriseSunsetCalculator calculator;
    private final TimeZone timeZone = TimeZone.getDefault();
    
    public SunriseSunsetTime(UserLocation userLocation, LocalDate localDate) {
        this.localDate = localDate;
        Location location = new Location(userLocation.getLatitude(), userLocation.getLongitude());
        this.calculator = new SunriseSunsetCalculator(location, this.timeZone);
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
