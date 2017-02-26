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

        PicturesManager.load(getResources());
    }

    public void launchGame(View v)
    {
        if (PicturesManager.isInitialized())
            startActivity(new Intent(this, Game.class));
    }

    public void quit(View v)
    {
        finish();
    }
}
