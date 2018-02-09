package littlesky.model.sky;

import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkyColorGradation {
    private final List<KeyFrame> frames = new ArrayList<>();
    
    public SkyColorGradation addKeyFrame(LocalTime time, Color color) {
        this.frames.add(new KeyFrame(time, color));
        return this;
    }
    
    public Color at(LocalTime time) {
        if (this.frames.isEmpty()) {
            throw new IllegalStateException("key frames are not specified.");
        }
        
        // 時間でソート
        List<KeyFrame> keyFrames = new ArrayList<>(this.frames);
        Collections.sort(keyFrames);
        
        // 先頭と末尾のそれぞれにセットされている色と同じ色を、日付の最初と最後にキーフレームとして追加する
        if (!keyFrames.get(0).time.equals(LocalTime.MIN)) {
            keyFrames.add(0, new KeyFrame(LocalTime.MIN, keyFrames.get(0).color));
        }
        if (!keyFrames.get(keyFrames.size()-1).time.equals(LocalTime.MAX)) {
            keyFrames.add(new KeyFrame(LocalTime.MAX, keyFrames.get(keyFrames.size()-1).color));
        }
        
        // 指定した時間がマッチする区間を決定する
        KeyFrame beginFrame = null;
        KeyFrame endFrame = null;

        for (int i = 0; i < keyFrames.size()-1; i++) {
            KeyFrame from = keyFrames.get(i);
            KeyFrame to = keyFrames.get(i+1);
            
            if ((from.time.equals(time) || from.time.isBefore(time)) && time.isBefore(to.time)) {
                beginFrame = from;
                endFrame = to;
            }
        }
        
        if (beginFrame == null) {
            throw new IllegalStateException("begin or end frame is not decided. time=" + time + ", keyFrames=" + keyFrames);
        }

        // 決定した区間の時間間隔、および指定された時間までの間隔から、グラデーション色の割合を決定する
        Duration frameDuration = Duration.between(beginFrame.time, endFrame.time);
        Duration timeDuration = Duration.between(beginFrame.time, time);
        double rate = (double)timeDuration.toMillis() / (double)frameDuration.toMillis();

        // マッチした区間の開始・終了色の間で、指定された時間に沿った割合で色を補完する
        return beginFrame.color.interpolate(endFrame.color, rate);
    }
    
    private static class KeyFrame implements Comparable<KeyFrame> {
        private final LocalTime time;
        private final Color color;

        private KeyFrame(LocalTime time, Color color) {
            this.time = time;
            this.color = color;
        }

        @Override
        public int compareTo(KeyFrame other) {
            return this.time.compareTo(other.time);
        }

        @Override
        public String toString() {
            return "KeyFrame{" +
                    "time=" + time +
                    ", color=" + color +
                    '}';
        }
    }
}
