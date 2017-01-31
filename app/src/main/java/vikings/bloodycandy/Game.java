package vikings.bloodycandy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import junit.framework.Assert;

import static vikings.bloodycandy.R.string.*;

/**
 * Created by Jeremy on 31/01/2017.
 */

public class Game extends View
{
    private Paint paint;
    private Terrain terrain;
    private Bitmap[] tiles_pictures;
    Rect            tiles_src;
    Rect            tiles_dest;

    private int tile_size;
    private int border_padding;
    private int inline_padding;
    private int offset_y;//To place the terrain at the bottom of the screen

    public Game(Context context)
    {
        super(context);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setARGB(255, 0, 244, 128);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(32);

        terrain = new Terrain(getResources().getInteger(R.integer.nb_blocs_width),
                              getResources().getInteger(R.integer.nb_blocs_height));

        TypedArray pictures_id = getResources().obtainTypedArray(R.array.block_pictures);

        Assert.assertNotNull("pictures_id is null",pictures_id);

        tiles_pictures = new Bitmap[pictures_id.length()];
        for (int i = 0; i < tiles_pictures.length; ++i)
            tiles_pictures[i] = BitmapFactory.decodeResource(getResources(), pictures_id.getResourceId(i, 0));

        for (int i = 0; i < 16; ++i)
            Assert.assertNotNull("tiles " + Integer.toString(i) + " is null", tiles_pictures[i]);

        tiles_src = new Rect();
        tiles_dest = new Rect();

        tiles_src.left = 0;
        tiles_src.top = 0;
        tiles_src.right = tiles_pictures[0].getWidth();
        tiles_src.bottom = tiles_pictures[0].getHeight();
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        for (int x = 0; x < terrain.width(); ++x)
        {
            for (int y = 0; y < terrain.height(); ++y)
            {
                int tile_id = terrain.get(x, y);
                if (tile_id != 0)
                {
                    tiles_dest.top = border_padding + y * (tile_size + inline_padding) + offset_y;
                    tiles_dest.left = border_padding + x * (tile_size + inline_padding);
                    tiles_dest.bottom = tiles_dest.top + tile_size;
                    tiles_dest.right = tiles_dest.left + tile_size;

                    canvas.drawBitmap(tiles_pictures[tile_id - 1], tiles_src, tiles_dest, paint);
                }
            }
        }
    }

    public void onSizeChanged (int w, int h, int old_w, int old_h)
    {
        float border_ratio = getResources().getInteger(R.integer.border_padding_ratio) * 0.01f;
        float inline_ratio = getResources().getInteger(R.integer.inline_padding_ratio) * 0.01f;

        float ratio = w / (terrain.width() + border_ratio * 2 + inline_ratio * (terrain.width() - 1));
        tile_size = (int)(ratio);
        inline_padding = (int)(ratio * inline_ratio);
        border_padding = (int)(ratio * border_ratio);

        offset_y = h - (terrain.height() * tile_size +
                inline_padding * (terrain.height() - 1) +
                border_padding * 2);
    }
}
