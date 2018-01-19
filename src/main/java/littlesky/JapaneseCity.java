package littlesky;

import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.TimeZone;

/**
 * <a href="http://eco.mtk.nao.ac.jp/koyomi/dni/">各地のこよみ</a>
 */
public enum JapaneseCity implements City {
    TOKYO(35.6581, 139.7414),
    OSAKA(34.6833, 135.4833);

    private final double latitude;
    private final double longitude;

    JapaneseCity(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public Location location() {
        return new Location(this.latitude, this.longitude);
    }

    @Override
    public TimeZone timeZone() {
        return TimeZone.getTimeZone("Asia/Tokyo");
    }
}
