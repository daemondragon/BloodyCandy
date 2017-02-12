package vikings.bloodycandy;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Jeremy on 31/01/2017.
 */

public class Game extends Activity
{
    GestureDetector gesture_detector;
    BoardView       board;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        board = (BoardView)findViewById(R.id.board);
        board.setScoreView((TextView)findViewById(R.id.score));
        gesture_detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return board.onFling(e1, e2);
            }
        });
    }

    public boolean onTouchEvent(MotionEvent e)
    {
        return (gesture_detector.onTouchEvent(e));
    }
}
