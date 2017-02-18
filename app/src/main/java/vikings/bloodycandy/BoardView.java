package vikings.bloodycandy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.ArrayList;

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
    private int offset_x;
    private int offset_y;

    private float selected_scale_factor;
    private float selected_wave_length;

    private Board board;
    private long lastFrameTime;
    private TextView score = null;

    private long need_to_be_selected_time;
    private boolean have_blocks_selected;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadPictures();

        tiles_dest = new Rect();
        board = new Board(getResources().getInteger(R.integer.nb_blocs_width),
                getResources().getInteger(R.integer.nb_blocs_height));

        board.setNbBlocks(getResources().obtainTypedArray(R.array.tiles_pictures).length());
        Block.setGravity(getResources().getInteger(R.integer.gravity) / 100.f);
        Block.setSwapVelocity(getResources().getInteger(R.integer.swap_velocity) / 100.f);

        selected_scale_factor = (getResources().getInteger(R.integer.selected_scale) - 100) / 100.f;
        Log.d(".Board", "scale: " + selected_scale_factor);

        selected_wave_length = getResources().getInteger(R.integer.selected_wave_length);

        lastFrameTime = System.currentTimeMillis();
        need_to_be_selected_time = lastFrameTime;
        have_blocks_selected = false;
    }

    public void setScoreView(TextView view)
    {
        score = view;
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
        {
            tiles_pictures[i] = BitmapFactory.decodeResource(getResources(), pictures_id.getResourceId(i, 0));
            Assert.assertNotNull("tiles " + Integer.toString(i) + " is null", tiles_pictures[i]);
        }

        tile_back = BitmapFactory.decodeResource(getResources(), R.drawable.tile_back);
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        boolean previous_can_fall = board.someBlocksAreFalling();

        long currentFrameTime = System.currentTimeMillis();
        board.update((currentFrameTime - lastFrameTime) / 1000.f);
        lastFrameTime = currentFrameTime;

        drawBackground(canvas);
        drawBoard(canvas);
        drawBlocks(canvas);

        boolean actual_can_fall = board.someBlocksAreFalling();
        if (previous_can_fall)
        {
            have_blocks_selected = false;
            if (!actual_can_fall)
                need_to_be_selected_time = System.currentTimeMillis();
        }


        if (lastFrameTime - need_to_be_selected_time > 5000 && !have_blocks_selected)
        {
            ArrayList<Block> to_select = new ArrayList<>();
            if (board.firstAvailableSwap(to_select))
            {
                for (Block b : to_select)
                    b.select(true);
                have_blocks_selected = true;
            }
            else
                board.reset();
        }

        invalidate();
    }

    void drawBackground(Canvas canvas)
    {
        if (score != null)
            score.setText(String.format(getResources().getString(R.string.score), board.getScore()));
    }
    void drawBoard(Canvas canvas)
    {
        for (int x = 0; x < board.width(); ++x)
        {
            for (int y = 0; y < board.height(); ++y)
            {
                if (board.get(x, y).getType() != Block.Type.Hole)
                {
                    tiles_dest.top = y * (tile_size + inline_padding) + offset_y;
                    tiles_dest.left = x * (tile_size + inline_padding) + offset_x;
                    tiles_dest.bottom = tiles_dest.top + tile_size + inline_padding;
                    tiles_dest.right = tiles_dest.left + tile_size + inline_padding;

                    canvas.drawBitmap(tile_back, null, tiles_dest, null);
                }
            }
        }
    }
    void drawBlocks(Canvas canvas)
    {
        //Log.d(".drawBoard", "scale_factor: " + (System.currentTimeMillis() % (long)selected_wave_length));
        float scale_factor = 1.f + selected_scale_factor *
                (float)Math.cos(Math.PI * 2 * (System.currentTimeMillis() % (long)selected_wave_length) / selected_wave_length);
        float half_inline_padding = inline_padding / 2;
        float half_tile_size = tile_size / 2;

        for (int x = 0; x < board.width(); ++x)
        {
            for (int y = 0; y < board.height(); ++y)
            {
                Block block = board.get(x, y);
                if (block.getType() == Block.Type.Normal)
                {
                    float center_y = offset_y + half_inline_padding + half_tile_size +
                            (y - block.getFallingStatus() + block.getOffsetY()) * (tile_size + inline_padding);
                    float center_x = offset_x + half_inline_padding + half_tile_size +
                            (x + block.getOffsetX()) * (tile_size + inline_padding);
                    float scale = 1.0f;
                    if (block.isSelected())
                        scale = scale_factor;

                    tiles_dest.top = (int)(center_y - half_tile_size * scale);
                    tiles_dest.left = (int)(center_x - half_tile_size * scale);
                    tiles_dest.bottom = (int)(center_y + half_tile_size * scale);
                    tiles_dest.right = (int)(center_x + half_tile_size * scale);

                    canvas.drawBitmap(tiles_pictures[block.getId()], null, tiles_dest, null);
                }
            }
        }
    }

    public void onSizeChanged (int w, int h, int old_w, int old_h)
    {
        offset_x = 0;
        offset_y = 0;

        float inline_ratio = getResources().getInteger(R.integer.inline_padding_ratio) * 0.01f;
        float ratio;
        if (w < h)//Portrait
            ratio = w / ((1 + inline_ratio) * board.width());
        else//Landscape
            ratio = h / ((1 + inline_ratio) * board.height());

        tile_size = (int)(ratio);
        inline_padding = (int)(ratio * inline_ratio);

        if (w < h)//Portrait
            offset_y = getHeight() - (tile_size + inline_padding) * board.height();
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2)
    {
        int x1 = (int)(e1.getX() - getLeft() - offset_x) / (inline_padding + tile_size);
        int y1 = (int)(e1.getY() - getTop() - offset_y) / (inline_padding + tile_size);

        int x2 = x1;
        int y2 = y1;

        if (Math.abs(e1.getX() - e2.getX()) < Math.abs(e1.getY() - e2.getY()))
            y2 += (e1.getY() > e2.getY() ? -1 : 1);
        else
            x2 += (e1.getX() > e2.getX() ? -1 : 1);

        if (!board.someBlocksAreFalling() && board.canSwap(x1, y1, x2, y2))
        {
            board.swap(x1, y1, x2, y2);
            board.selectAll(false);
            return (true);
        }
        else
            return (false);
    }
}
