package csc.game.word;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

public class LineConnectorView extends View {
    Paint paint = new Paint();
    PointF startPoint;
    PointF endPoint;
    private final Context context;

    public LineConnectorView(Context context) {
        super(context);
        this.context = context;
    }

    public LineConnectorView(Context context, PointF startPoint) {
        super(context);
        this.context = context;
        this.startPoint = startPoint;
        this.endPoint = startPoint;
    }

    public void draw(PointF endPoint) {
        this.endPoint = endPoint;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(context.getResources().getColor(R.color.colorPrimary));
        paint.setStrokeWidth(25);
        paint.setAntiAlias(true);

        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
        super.onDraw(canvas);
    }
}
