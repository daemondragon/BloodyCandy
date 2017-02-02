package vikings.bloodycandy;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Jeremy on 31/01/2017.
 */

public class MainActivity extends Activity
{
    GestureDetector gesture_detector;
    Game            game_board;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        game_board = (Game)findViewById(R.id.board_game);
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
                return game_board.onFling(e1, e2);
            }
        });
    }

    public boolean onTouchEvent(MotionEvent e)
    {
        return (gesture_detector.onTouchEvent(e));
    }
}

