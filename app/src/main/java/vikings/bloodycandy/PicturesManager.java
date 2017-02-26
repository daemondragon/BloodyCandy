package vikings.bloodycandy;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import junit.framework.Assert;

/**
 * Created by Jeremy on 25/02/2017.
 */

public class PicturesManager
{
    private static boolean is_initialized = false;
    public static Bitmap[] tiles;
    public static Bitmap[] background;

    static void load(final Resources res)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                TypedArray pictures_id = res.obtainTypedArray(R.array.tiles_pictures);
                Assert.assertNotNull("pictures_id is null", pictures_id);

                tiles = new Bitmap[pictures_id.length()];
                for (int i = 0; i < tiles.length; ++i)
                {
                    tiles[i] = BitmapFactory.decodeResource(res, pictures_id.getResourceId(i, 0));
                    Assert.assertNotNull("tiles " + Integer.toString(i) + " is null", tiles[i]);
                }

                pictures_id.recycle();
                pictures_id = res.obtainTypedArray(R.array.background_pictures);
                background = new Bitmap[pictures_id.length()];
                for (int i = 0; i < background.length; ++i)
                {
                    background[i] = BitmapFactory.decodeResource(res, pictures_id.getResourceId(i, 0));
                    Assert.assertNotNull("background " + Integer.toString(i) + " is null", background[i]);
                }

                is_initialized = true;
            }
        }).start();
    }

    static boolean isInitialized()
    {
        return (is_initialized);
    }
}
