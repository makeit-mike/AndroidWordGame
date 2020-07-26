package csc.game.word;

import android.content.Context;
import android.content.SharedPreferences;

public class GamePoints {
    private int point;
    private Runnable onUpdate;
    private boolean isCurrentLevel;

    private static final String PREF_POINTS = "points";

    Context context;

    GamePoints(Context context, boolean isCurrentLevel, Runnable onUpdate){
        this.context = context;
        this.isCurrentLevel = isCurrentLevel;
        this.onUpdate = onUpdate;
        readCache();
    }

    public int getPoint() {
        return point;
    }

    public int addPoint(int point){
        if (!isCurrentLevel)
            return this.point;

        if (isCurrentLevel)
        this.point += point;
        this.onUpdate.run();
        cachePoints();
        return this.point;
    }

    private void cachePoints(){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_POINTS, Context.MODE_PRIVATE).edit();
        editor.putInt("points", point);
        editor.apply();
    }

    private void readCache(){
        SharedPreferences prefs = context.getSharedPreferences(PREF_POINTS, Context.MODE_PRIVATE);
        this.point = prefs.getInt("points", 0);
    }
}
