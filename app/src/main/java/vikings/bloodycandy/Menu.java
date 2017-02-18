package vikings.bloodycandy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Jeremy on 02/02/2017.
 */

public class Menu extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
    }

    public void launchGame(View v)
    {
        startActivity(new Intent(this, Game.class));
    }

    public void quit(View v)
    {
        finish();
    }
}
