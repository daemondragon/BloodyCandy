package vikings.bloodycandy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Jeremy on 31/01/2017.
 */

public class Game extends View
{
    private Paint paint;
    private Terrain terrain;

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
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        for (int x = 0; x < terrain.width(); ++x)
        {
            for (int y = 0; y < terrain.height(); ++y)
            {
                int top = border_padding + y * (tile_size + inline_padding);
                int left = border_padding + x * (tile_size + inline_padding);
                canvas.drawRect(left, top + offset_y, left + tile_size, top + tile_size + offset_y, paint);
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
