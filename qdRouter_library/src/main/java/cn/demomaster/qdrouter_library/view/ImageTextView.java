package cn.demomaster.qdrouter_library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatImageView;

import cn.demomaster.qdrouter_library.R;


/**
 * @author squirrel桓
 * @date 2018/11/20.
 * description：
 */
public class ImageTextView extends AppCompatImageView {

    public ImageTextView(Context context) {
        super(context);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public int textColor = Color.BLACK;
    public int textSize = -1;

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        postInvalidate();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        requestLayout();
        postInvalidate();
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ImageTextView);
            textColor = typedArray.getColor(R.styleable.ImageTextView_android_textColor, textColor);

            int defSize = context.getResources().getDimensionPixelSize(R.dimen.quickdev_title_text_size);
            textSize = typedArray.getDimensionPixelSize(R.styleable.ImageTextView_quick_textSize,defSize);
            text = typedArray.getString(R.styleable.ImageTextView_android_text);
            typedArray.recycle();
            //QDLogger.println("text1="+text);
            TypedArray typedArray2 = getContext().obtainStyledAttributes(attrs, R.styleable.QDTextView);
            textSize = typedArray2.getDimensionPixelSize(R.styleable.QDTextView_android_textSize,textSize);
            String str = typedArray2.getString(R.styleable.QDTextView_quick_text);
            if (!TextUtils.isEmpty(str)) {
                text = str;
                //QDLogger.println("text2="+text);
            }
        }
    }

    private float textWidth;
    private float baseLineY;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //QDLogger.e(TAG.TAG,"onMeasure");
        if (!TextUtils.isEmpty(text)) {
            initPaint();
            // 文字宽
            textWidth = mPaint.measureText(text);
            // 文字baseline在y轴方向的位置
            baseLineY = Math.abs(mPaint.ascent() + mPaint.descent()) / 2;

            int minimumWidth = getSuggestedMinimumWidth();
            int minimumHeight = getSuggestedMinimumHeight();
            int width = measureWidth(minimumWidth, widthMeasureSpec);
            int height = measureHeight(minimumHeight, heightMeasureSpec);
            //QDLogger.e(TAG.TAG,"width="+width+",height="+height);
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) textWidth;//super.getSuggestedMinimumWidth();
    }

    private int measureWidth(int defaultWidth, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //QDLogger.e("YViewWidth", "---speSize = " + specSize + ",specMode="+specMode);

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) mPaint.measureText(text) + getPaddingLeft() + getPaddingRight();
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;// (int) mPaint.measureText(text);
                break;
            case MeasureSpec.UNSPECIFIED:
                //defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }

    private int measureHeight(int defaultHeight, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //Log.e("YViewHeight", "---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (-mPaint.ascent() + mPaint.descent()) + getPaddingTop() + getPaddingBottom();
                //Log.e("YViewHeight", "---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                //Log.e("YViewHeight", "---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                //Log.e("YViewHeight", "---speSize = UNSPECIFIED");
//        1.基准点是baseline
//        2.ascent：是baseline之上至字符最高处的距离
//        3.descent：是baseline之下至字符最低处的距离
//        4.leading：是上一行字符的descent到下一行的ascent之间的距离,也就是相邻行间的空白距离
//        5.top：是指的是最高字符到baseline的值,即ascent的最大值
//        6.bottom：是指最低字符到baseline的值,即descent的最大值
                break;
        }
        return defaultHeight;
    }

    public int center_x, center_y, mwidth, width, height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //QDLogger.e(TAG.TAG,"onSizeChanged"+"w="+w+",h="+h);
        width = w;
        height = h;
        center_x = width / 2;
    }

    boolean showImage = true;

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //QDLogger.e(TAG.TAG,"onDraw");
        if (showImage) {
            super.onDraw(canvas);
        }
        if (text != null) {
            initPaint();
            drawText(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //QDLogger.e(TAG.TAG,"onLayout");
    }

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        requestLayout();
        invalidate();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    Paint mPaint;

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
    }

    private int textGravity = Gravity.CENTER;

    public void setTextGravity(int textGravity) {
        this.textGravity = textGravity;
        postInvalidate();
    }

    private void drawText(Canvas canvas) {
        //int h = getHeight();
       /* // 计算Baseline绘制的起点X轴坐标
        int baseX = (int) (canvas.getWidth() / 2 - mPaint.measureText(text) / 2);
        // 计算Baseline绘制的Y坐标(有点难理解，记住)
        int baseY = (int) ((canvas.getHeight() / 2) - ((mPaint.descent() + mPaint.ascent()) / 2));*/
        float x = 0;
        float y = 0;
        if (textGravity == Gravity.LEFT) {
            x = getPaddingLeft();
            y = height / 2f + baseLineY;
        } else if (textGravity == Gravity.CENTER) {
            x = width / 2f - textWidth / 2f;
            y = height / 2f + baseLineY;
        } else if (textGravity == Gravity.RIGHT) {
            x = width / 2f - textWidth / 2f;
            y = height / 2f + baseLineY;
        }
        canvas.drawText(text, x, y, mPaint);
        //canvas.drawLine( 0,  height/2,  width,  height/2, mPaint);
    }

    private int showType;

    public void asTextView() {

    }
}
