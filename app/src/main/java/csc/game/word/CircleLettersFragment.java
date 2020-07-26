package csc.game.word;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CircleLettersFragment extends Fragment implements View.OnTouchListener {
    private static final String LEVELWORD = "level_word";

    private String levelLetters;
    private String createdWord = "";
    private ArrayList<Integer> activeLetters;
    private LineConnectorView lastLine;
    private PointF lastPoint;
    private ArrayList<PointF> pointsLocation;

    RelativeLayout containerLettersView;
    RelativeLayout containerConnectorLines;

    int contRadius = 115;

    private float scale;

    public CircleLettersFragment() {
        // Required empty public constructor
    }

    public static CircleLettersFragment newInstance(String levelWord) {
        CircleLettersFragment fragment = new CircleLettersFragment();
        Bundle args = new Bundle();
        args.putString(LEVELWORD, levelWord.toUpperCase());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            List<String> letters = Arrays.asList(getArguments().getString(LEVELWORD).split(""));
            Collections.shuffle(letters);
            levelLetters = "";
            for (String letter : letters) {
                levelLetters += letter;
            }
        }
        Log.d("mylog", "asas"+levelLetters);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_letters, container, false);

        containerConnectorLines = view.findViewById(R.id.connector_lines);
        containerLettersView = view.findViewById(R.id.letters_menu);
        containerLettersView.setOnTouchListener(this);

        scale = getResources().getDisplayMetrics().density;
        generateCircularView();
        activeLetters = new ArrayList<>();
        return view;
    }

    public void generateCircularView() {
        double angle = Math.PI/2;
        double d_angle = 2*Math.PI/levelLetters.length();
        pointsLocation = new ArrayList<>();
        int letterRadius = 30;
        int letterDist = contRadius-letterRadius-5; //letter distance from origin

        for (int i=0; i<levelLetters.length(); i++){
            TextView textView = new TextView(getContext());
            textView.setText(String.valueOf(levelLetters.charAt(i)));
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    dpToPixel(letterRadius*2), dpToPixel(letterRadius*2));

            float left = (float)(contRadius+Math.cos(angle)*letterDist);
            float top = (float) (contRadius-Math.sin(angle)*letterDist);
            PointF pointF = new PointF(dpToPixel(left), dpToPixel(top));
            pointsLocation.add(pointF);
            lp.setMargins(dpToPixel(left-letterRadius), dpToPixel(top-letterRadius), 0,0 );

            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.colorText));
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(45);

            containerLettersView.addView(textView);

            Log.d("mylog", angle*180/Math.PI+", "+(pointF.x)+", "+pointF.y);

            angle += d_angle;
        }
    }

    private int dpToPixel(double dps){
        return (int) (dps * scale + 0.5f);
    }

    private double pixelToDp(double pixel){
        return ((pixel-0.5f) / scale );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //get coordinates of tap
        double x = pixelToDp(event.getX())-contRadius;
        double y = contRadius-pixelToDp(event.getY());

        Log.d("mylog", String.valueOf(calculateIfLetter(x, y)));

        int letter = calculateIfLetter(x, y);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (letter>-1)
                    addNewLetterToCurrent(letter);
                break;
            case MotionEvent.ACTION_MOVE:
                if (letter>-1)
                    addNewLetterToCurrent(letter);
                else
                    drawFloatingLine(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                Log.d("mylog", createdWord);
                ((LevelActivity) Objects.requireNonNull(getActivity())).checkFoundWord(createdWord);
                resetActives();
                return false;
        }
        return true;

    }

    private void addNewLetterToCurrent(int index) {
        if (index<0 || activeLetters.contains(index))
            return;

        activeLetters.add(index);
        createdWord += levelLetters.charAt(index);
        Log.d("mylog", createdWord);

        TextView textView = (TextView) containerLettersView.getChildAt(index);
        textView.setBackground(getResources().getDrawable(R.drawable.bg_active_letter));
        textView.setTextColor(Color.WHITE);

        drawConnectorLine(index);

        ((LevelActivity) Objects.requireNonNull(getActivity())).onNewLetter(createdWord);
    }

    private void drawConnectorLine(int index) {
        if (lastLine!=null) {
            lastLine.draw(pointsLocation.get(index));
        }

        lastLine = new LineConnectorView(getContext(), pointsLocation.get(index));
        containerConnectorLines.addView(lastLine);
        lastPoint = pointsLocation.get(index);
    }

    private void drawFloatingLine(float x, float y){
        lastLine.draw(new PointF(x, y));
    }

    private void resetActives(){
        activeLetters.clear();
        createdWord = "";
        lastPoint = null;
        containerConnectorLines.removeAllViews();
        inactiveAllLetters();
    }

    private void inactiveAllLetters() {
        int count = containerLettersView.getChildCount();
        for (int i=0; i<count; i++){
            TextView textView = (TextView) containerLettersView.getChildAt(i);
            textView.setBackground(null);
            textView.setTextColor(Color.BLACK);
        }
    }

    private int calculateIfLetter(double x, double y){
        double dist = Math.sqrt(x*x+y*y);

        if (dist<60){
            Log.d("mylog", "middle dis: "+dist+", x: "+x+", y: "+y);
            return -1;
        }
        //calculate angle (angle between 0-2pi)
        double angle = Math.atan(y/x);
        if (angle<0 && y<0)
            angle += 2*Math.PI;
        if (x<0)
            angle += Math.PI;
        //calculate if touched coordinate contains letter
        double d_angle = 2*Math.PI/levelLetters.length();
        int ang = (int) (angle/d_angle);
        double delta = 0.32;

        if (Math.PI/2+d_angle*ang-delta<angle){
            return ang;
        }
        if(Math.PI/2+d_angle*(ang-1)+delta>angle){
            if (ang==0)
                ang=levelLetters.length();
            return ang-1;
        }

        return -1;
    }

}
