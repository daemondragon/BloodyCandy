package vikings.bloodycandy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import junit.framework.Assert;

/**
 * Created by Jeremy on 04/02/2017.
 */

public class BoardView extends View
{
    private static boolean     loaded_pictures = false;
    private static Bitmap[]    tiles_pictures;
    private static Bitmap      tile_back;
    private Rect tiles_dest;

    private int tile_size;
    private int inline_padding;

    private Board board;
    private long lastFrameTime;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadPictures();

        tiles_dest = new Rect();
        board = new Board(getResources().getInteger(R.integer.nb_blocs_width),
                getResources().getInteger(R.integer.nb_blocs_height));

        Block.setGravity(getResources().getInteger(R.integer.gravity) / 100.f);

        lastFrameTime = System.currentTimeMillis();
    }

    public void loadPictures()
    {
        if (loaded_pictures)
            return;

        loaded_pictures = true;

        TypedArray pictures_id = getResources().obtainTypedArray(R.array.tiles_pictures);
        Assert.assertNotNull("pictures_id is null", pictures_id);

        tiles_pictures = new Bitmap[pictures_id.length()];
        for (int i = 0; i < tiles_pictures.length; ++i)
            tiles_pictures[i] = BitmapFactory.decodeResource(getResources(), pictures_id.getResourceId(i, 0));

        for (int i = 0; i < 16; ++i)
            Assert.assertNotNull("tiles " + Integer.toString(i) + " is null", tiles_pictures[i]);

        tile_back = BitmapFactory.decodeResource(getResources(), R.drawable.tile_back);
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        long currentFrameTime = System.currentTimeMillis();
        board.update((currentFrameTime - lastFrameTime) / 1000.f);
        lastFrameTime = currentFrameTime;

        drawBackground(canvas);
        drawBoard(canvas);
        drawBlocks(canvas);

        invalidate();
    }

    void drawBackground(Canvas canvas)
    {

    }
    void drawBoard(Canvas canvas)
    {
        for (int x = 0; x < board.width(); ++x)
        {
            for (int y = 0; y < board.height(); ++y)
            {
                if (board.get(x, y).getType() != Block.Type.Hole)
                {
                    tiles_dest.top = y * (tile_size + inline_padding);
                    tiles_dest.left = x * (tile_size + inline_padding);
                    tiles_dest.bottom = tiles_dest.top + tile_size + inline_padding;
                    tiles_dest.right = tiles_dest.left + tile_size + inline_padding;

                    canvas.drawBitmap(tile_back, null, tiles_dest, null);
                }
            }
        }
    }
    void drawBlocks(Canvas canvas)
    {
        for (int x = 0; x < board.width(); ++x)
        {
            for (int y = 0; y < board.height(); ++y)
            {
                Block block = board.get(x, y);
                if (block.getType() == Block.Type.Normal)
                {
                    tiles_dest.top = inline_padding / 2 + (int)((y - block.getFallingStatus()) * (tile_size + inline_padding));
                    tiles_dest.left = inline_padding / 2 + x * (tile_size + inline_padding);
                    tiles_dest.bottom = tiles_dest.top + tile_size;
                    tiles_dest.right = tiles_dest.left + tile_size;

                    canvas.drawBitmap(tiles_pictures[block.getId()], null, tiles_dest, null);
                }
            }
        }
    }

    public void onSizeChanged (int w, int h, int old_w, int old_h)
    {
        float inline_ratio = getResources().getInteger(R.integer.inline_padding_ratio) * 0.01f;

        float ratio = w / (board.width() + inline_ratio * (board.width()));
        tile_size = (int)(ratio);
        inline_padding = (int)(ratio * inline_ratio);
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2)
    {
        int x1 = (int)(e1.getX() - getLeft()) / (inline_padding + tile_size);
        int y1 = (int)(e1.getY() - getTop()) / (inline_padding + tile_size);

        int x2 = x1;
        int y2 = y1;

        if (Math.abs(e1.getX() - e2.getX()) < Math.abs(e1.getY() - e2.getY()))
            y2 += (e1.getY() > e2.getY() ? -1 : 1);
        else
            x2 += (e1.getX() > e2.getX() ? -1 : 1);

        if (board.canSwap(x1, y1, x2, y2))
        {
            board.swap(x1, y1, x2, y2);
            if (board.canDestroy(x1, y1))
                board.destroy(x1, x2);
            if (board.canDestroy(x2, y2))
                board.destroy(x2, y2);
            return (true);
        }
        else
            return (false);
    }
}
