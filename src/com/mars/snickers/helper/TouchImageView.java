package com.mars.snickers.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

//public class TouchImageView extends ImageView {
//
//    private static final String DEBUG = "DEBUG";
//
//
//    private static final float SUPER_MIN_MULTIPLIER = .75f;
//    private static final float SUPER_MAX_MULTIPLIER = 1.25f;
//
//
//    private float normalizedScale;
//
//
//    private Matrix matrix, prevMatrix;
//
//    private static enum State {NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM}
//
//    ;
//    private State state;
//
//    private float minScale;
//    private float maxScale;
//    private float superMinScale;
//    private float superMaxScale;
//    private float[] m;
//
//    private Context context;
//    private Fling fling;
//
//    private ScaleType mScaleType;
//
//    private boolean imageRenderedAtLeastOnce;
//    private boolean onDrawReady;
//
//    private ZoomVariables delayedZoomVariables;
//
//
//    private int viewWidth, viewHeight, prevViewWidth, prevViewHeight;
//
//
//    private float matchViewWidth, matchViewHeight, prevMatchViewWidth, prevMatchViewHeight;
//
//    private ScaleGestureDetector mScaleDetector;
//    private GestureDetector mGestureDetector;
//    private GestureDetector.OnDoubleTapListener doubleTapListener = null;
//    private OnTouchListener userTouchListener = null;
//    private OnTouchImageViewListener touchImageViewListener = null;
//
//    public TouchImageView(Context context) {
//        super(context);
//        sharedConstructing(context);
//    }
//
//    public TouchImageView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        sharedConstructing(context);
//    }
//
//    public TouchImageView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        sharedConstructing(context);
//    }
//
//    private void sharedConstructing(Context context) {
//        super.setClickable(true);
//        this.context = context;
//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//        mGestureDetector = new GestureDetector(context, new GestureListener());
//        matrix = new Matrix();
//        prevMatrix = new Matrix();
//        m = new float[9];
//        normalizedScale = 1;
//        if (mScaleType == null) {
//            mScaleType = ScaleType.FIT_CENTER;
//        }
//        minScale = 1;
//        maxScale = 3;
//        superMinScale = SUPER_MIN_MULTIPLIER * minScale;
//        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale;
//        setImageMatrix(matrix);
//        setScaleType(ScaleType.MATRIX);
//        setState(State.NONE);
//        onDrawReady = false;
//        super.setOnTouchListener(new PrivateOnTouchListener());
//    }
//
//    @Override
//    public void setOnTouchListener(View.OnTouchListener l) {
//        userTouchListener = l;
//    }
//
//    public void setOnTouchImageViewListener(OnTouchImageViewListener l) {
//        touchImageViewListener = l;
//    }
//
//    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener l) {
//        doubleTapListener = l;
//    }
//
//    @Override
//    public void setImageResource(int resId) {
//        super.setImageResource(resId);
//        savePreviousImageValues();
//        fitImageToView();
//    }
//
//    @Override
//    public void setImageBitmap(Bitmap bm) {
//        super.setImageBitmap(bm);
//        savePreviousImageValues();
//        fitImageToView();
//    }
//
//    @Override
//    public void setImageDrawable(Drawable drawable) {
//        super.setImageDrawable(drawable);
//        savePreviousImageValues();
//        fitImageToView();
//    }
//
//    @Override
//    public void setImageURI(Uri uri) {
//        super.setImageURI(uri);
//        savePreviousImageValues();
//        fitImageToView();
//    }
//
//    @Override
//    public void setScaleType(ScaleType type) {
//        if (type == ScaleType.FIT_START || type == ScaleType.FIT_END) {
//            throw new UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END");
//        }
//        if (type == ScaleType.MATRIX) {
//            super.setScaleType(ScaleType.MATRIX);
//
//        } else {
//            mScaleType = type;
//            if (onDrawReady) {
//
//                setZoom(this);
//            }
//        }
//    }
//
//    @Override
//    public ScaleType getScaleType() {
//        return mScaleType;
//    }
//
//
//    public boolean isZoomed() {
//        return normalizedScale != 1;
//    }
//
//
//    public RectF getZoomedRect() {
//        if (mScaleType == ScaleType.FIT_XY) {
//            throw new UnsupportedOperationException("getZoomedRect() not supported with FIT_XY");
//        }
//        PointF topLeft = transformCoordTouchToBitmap(0, 0, true);
//        PointF bottomRight = transformCoordTouchToBitmap(viewWidth, viewHeight, true);
//
//        float w = getDrawable().getIntrinsicWidth();
//        float h = getDrawable().getIntrinsicHeight();
//        return new RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h);
//    }
//
//
//    private void savePreviousImageValues() {
//        if (matrix != null && viewHeight != 0 && viewWidth != 0) {
//            matrix.getValues(m);
//            prevMatrix.setValues(m);
//            prevMatchViewHeight = matchViewHeight;
//            prevMatchViewWidth = matchViewWidth;
//            prevViewHeight = viewHeight;
//            prevViewWidth = viewWidth;
//        }
//    }
//
//    @Override
//    public Parcelable onSaveInstanceState() {
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("instanceState", super.onSaveInstanceState());
//        bundle.putFloat("saveScale", normalizedScale);
//        bundle.putFloat("matchViewHeight", matchViewHeight);
//        bundle.putFloat("matchViewWidth", matchViewWidth);
//        bundle.putInt("viewWidth", viewWidth);
//        bundle.putInt("viewHeight", viewHeight);
//        matrix.getValues(m);
//        bundle.putFloatArray("matrix", m);
//        bundle.putBoolean("imageRendered", imageRenderedAtLeastOnce);
//        return bundle;
//    }
//
//    @Override
//    public void onRestoreInstanceState(Parcelable state) {
//        if (state instanceof Bundle) {
//            Bundle bundle = (Bundle) state;
//            normalizedScale = bundle.getFloat("saveScale");
//            m = bundle.getFloatArray("matrix");
//            prevMatrix.setValues(m);
//            prevMatchViewHeight = bundle.getFloat("matchViewHeight");
//            prevMatchViewWidth = bundle.getFloat("matchViewWidth");
//            prevViewHeight = bundle.getInt("viewHeight");
//            prevViewWidth = bundle.getInt("viewWidth");
//            imageRenderedAtLeastOnce = bundle.getBoolean("imageRendered");
//            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
//            return;
//        }
//
//        super.onRestoreInstanceState(state);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        onDrawReady = true;
//        imageRenderedAtLeastOnce = true;
//        if (delayedZoomVariables != null) {
//            setZoom(delayedZoomVariables.scale, delayedZoomVariables.focusX, delayedZoomVariables.focusY, delayedZoomVariables.scaleType);
//            delayedZoomVariables = null;
//        }
//        super.onDraw(canvas);
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        savePreviousImageValues();
//    }
//
//
//    public float getMaxZoom() {
//        return maxScale;
//    }
//
//
//    public void setMaxZoom(float max) {
//        maxScale = max;
//        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale;
//    }
//
//
//    public float getMinZoom() {
//        return minScale;
//    }
//
//
//    public float getCurrentZoom() {
//        return normalizedScale;
//    }
//
//
//    public void setMinZoom(float min) {
//        minScale = min;
//        superMinScale = SUPER_MIN_MULTIPLIER * minScale;
//    }
//
//
//    public void resetZoom() {
//        normalizedScale = 1;
//        fitImageToView();
//    }
//
//
//    public void setZoom(float scale) {
//        setZoom(scale, 0.5f, 0.5f);
//    }
//
//
//    public void setZoom(float scale, float focusX, float focusY) {
//        setZoom(scale, focusX, focusY, mScaleType);
//    }
//
//
//    public void setZoom(float scale, float focusX, float focusY, ScaleType scaleType) {
//
//        if (!onDrawReady) {
//            delayedZoomVariables = new ZoomVariables(scale, focusX, focusY, scaleType);
//            return;
//        }
//
//        if (scaleType != mScaleType) {
//            setScaleType(scaleType);
//        }
//        resetZoom();
//        scaleImage(scale, viewWidth / 2, viewHeight / 2, true);
//        matrix.getValues(m);
//        m[Matrix.MTRANS_X] = -((focusX * getImageWidth()) - (viewWidth * 0.5f));
//        m[Matrix.MTRANS_Y] = -((focusY * getImageHeight()) - (viewHeight * 0.5f));
//        matrix.setValues(m);
//        fixTrans();
//        setImageMatrix(matrix);
//    }
//
//
//    public void setZoom(TouchImageView img) {
//        PointF center = img.getScrollPosition();
//        setZoom(img.getCurrentZoom(), center.x, center.y, img.getScaleType());
//    }
//
//
//    public PointF getScrollPosition() {
//        Drawable drawable = getDrawable();
//        if (drawable == null) {
//            return null;
//        }
//        int drawableWidth = drawable.getIntrinsicWidth();
//        int drawableHeight = drawable.getIntrinsicHeight();
//
//        PointF point = transformCoordTouchToBitmap(viewWidth / 2, viewHeight / 2, true);
//        point.x /= drawableWidth;
//        point.y /= drawableHeight;
//        return point;
//    }
//
//
//    public void setScrollPosition(float focusX, float focusY) {
//        setZoom(normalizedScale, focusX, focusY);
//    }
//
//
//    private void fixTrans() {
//        matrix.getValues(m);
//        float transX = m[Matrix.MTRANS_X];
//        float transY = m[Matrix.MTRANS_Y];
//
//        float fixTransX = getFixTrans(transX, viewWidth, getImageWidth());
//        float fixTransY = getFixTrans(transY, viewHeight, getImageHeight());
//
//        if (fixTransX != 0 || fixTransY != 0) {
//            matrix.postTranslate(fixTransX, fixTransY);
//        }
//    }
//
//
//    private void fixScaleTrans() {
//        fixTrans();
//        matrix.getValues(m);
//        if (getImageWidth() < viewWidth) {
//            m[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2;
//        }
//
//        if (getImageHeight() < viewHeight) {
//            m[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2;
//        }
//        matrix.setValues(m);
//    }
//
//    private float getFixTrans(float trans, float viewSize, float contentSize) {
//        float minTrans, maxTrans;
//
//        if (contentSize <= viewSize) {
//            minTrans = 0;
//            maxTrans = viewSize - contentSize;
//
//        } else {
//            minTrans = viewSize - contentSize;
//            maxTrans = 0;
//        }
//
//        if (trans < minTrans)
//            return -trans + minTrans;
//        if (trans > maxTrans)
//            return -trans + maxTrans;
//        return 0;
//    }
//
//    private float getFixDragTrans(float delta, float viewSize, float contentSize) {
//        if (contentSize <= viewSize) {
//            return 0;
//        }
//        return delta;
//    }
//
//    private float getImageWidth() {
//        return matchViewWidth * normalizedScale;
//    }
//
//    private float getImageHeight() {
//        return matchViewHeight * normalizedScale;
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Drawable drawable = getDrawable();
//        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
//            setMeasuredDimension(0, 0);
//            return;
//        }
//
//        int drawableWidth = drawable.getIntrinsicWidth();
//        int drawableHeight = drawable.getIntrinsicHeight();
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        viewWidth = setViewSize(widthMode, widthSize, drawableWidth);
//        viewHeight = setViewSize(heightMode, heightSize, drawableHeight);
//
//
//        setMeasuredDimension(viewWidth, viewHeight);
//
//
//        fitImageToView();
//    }
//
//
//    private void fitImageToView() {
//        Drawable drawable = getDrawable();
//        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
//            return;
//        }
//        if (matrix == null || prevMatrix == null) {
//            return;
//        }
//
//        int drawableWidth = drawable.getIntrinsicWidth();
//        int drawableHeight = drawable.getIntrinsicHeight();
//
//
//        float scaleX = (float) viewWidth / drawableWidth;
//        float scaleY = (float) viewHeight / drawableHeight;
//
//        switch (mScaleType) {
//            case CENTER:
//                scaleX = scaleY = 1;
//                break;
//
//            case CENTER_CROP:
//                scaleX = scaleY = Math.max(scaleX, scaleY);
//                break;
//
//            case CENTER_INSIDE:
//                scaleX = scaleY = Math.min(1, Math.min(scaleX, scaleY));
//
//            case FIT_CENTER:
//                scaleX = scaleY = Math.min(scaleX, scaleY);
//                break;
//
//            case FIT_XY:
//                break;
//
//            default:
//
//                throw new UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END");
//
//        }
//
//
//        float redundantXSpace = viewWidth - (scaleX * drawableWidth);
//        float redundantYSpace = viewHeight - (scaleY * drawableHeight);
//        matchViewWidth = viewWidth - redundantXSpace;
//        matchViewHeight = viewHeight - redundantYSpace;
//        if (!isZoomed() && !imageRenderedAtLeastOnce) {
//
//            matrix.setScale(scaleX, scaleY);
//            matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);
//            normalizedScale = 1;
//
//        } else {
//
//            if (prevMatchViewWidth == 0 || prevMatchViewHeight == 0) {
//                savePreviousImageValues();
//            }
//
//            prevMatrix.getValues(m);
//
//
//            m[Matrix.MSCALE_X] = matchViewWidth / drawableWidth * normalizedScale;
//            m[Matrix.MSCALE_Y] = matchViewHeight / drawableHeight * normalizedScale;
//
//
//            float transX = m[Matrix.MTRANS_X];
//            float transY = m[Matrix.MTRANS_Y];
//
//
//            float prevActualWidth = prevMatchViewWidth * normalizedScale;
//            float actualWidth = getImageWidth();
//            translateMatrixAfterRotate(Matrix.MTRANS_X, transX, prevActualWidth, actualWidth, prevViewWidth, viewWidth, drawableWidth);
//
//
//            float prevActualHeight = prevMatchViewHeight * normalizedScale;
//            float actualHeight = getImageHeight();
//            translateMatrixAfterRotate(Matrix.MTRANS_Y, transY, prevActualHeight, actualHeight, prevViewHeight, viewHeight, drawableHeight);
//
//            matrix.setValues(m);
//        }
//        fixTrans();
//        setImageMatrix(matrix);
//    }
//
//    private int setViewSize(int mode, int size, int drawableWidth) {
//        int viewSize;
//        switch (mode) {
//            case MeasureSpec.EXACTLY:
//                viewSize = size;
//                break;
//
//            case MeasureSpec.AT_MOST:
//                viewSize = Math.min(drawableWidth, size);
//                break;
//
//            case MeasureSpec.UNSPECIFIED:
//                viewSize = drawableWidth;
//                break;
//
//            default:
//                viewSize = size;
//                break;
//        }
//        return viewSize;
//    }
//
//    private void translateMatrixAfterRotate(int axis, float trans, float prevImageSize, float imageSize, int prevViewSize, int viewSize, int drawableSize) {
//        if (imageSize < viewSize) {
//            m[axis] = (viewSize - (drawableSize * m[Matrix.MSCALE_X])) * 0.5f;
//
//        } else if (trans > 0) {
//
//            m[axis] = -((imageSize - viewSize) * 0.5f);
//
//        } else {
//
//            float percentage = (Math.abs(trans) + (0.5f * prevViewSize)) / prevImageSize;
//            m[axis] = -((percentage * imageSize) - (viewSize * 0.5f));
//        }
//    }
//
//    private void setState(State state) {
//        this.state = state;
//    }
//
//    public boolean canScrollHorizontallyFroyo(int direction) {
//        return canScrollHorizontally(direction);
//    }
//
//    @Override
//    public boolean canScrollHorizontally(int direction) {
//        matrix.getValues(m);
//        float x = m[Matrix.MTRANS_X];
//
//        if (getImageWidth() < viewWidth) {
//            return false;
//
//        } else if (x >= -1 && direction < 0) {
//            return false;
//
//        } else if (Math.abs(x) + viewWidth + 1 >= getImageWidth() && direction > 0) {
//            return false;
//        }
//
//        return true;
//    }
//
//
//    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
//
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            if (doubleTapListener != null) {
//                return doubleTapListener.onSingleTapConfirmed(e);
//            }
//            return performClick();
//        }
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//            performLongClick();
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            if (fling != null) {
//
//                fling.cancelFling();
//            }
//            fling = new Fling((int) velocityX, (int) velocityY);
//            compatPostOnAnimation(fling);
//            return super.onFling(e1, e2, velocityX, velocityY);
//        }
//
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            boolean consumed = false;
//            if (doubleTapListener != null) {
//                consumed = doubleTapListener.onDoubleTap(e);
//            }
//            if (state == State.NONE) {
//                float targetZoom = (normalizedScale == minScale) ? maxScale : minScale;
//                DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, e.getX(), e.getY(), false);
//                compatPostOnAnimation(doubleTap);
//                consumed = true;
//            }
//            return consumed;
//        }
//
//        @Override
//        public boolean onDoubleTapEvent(MotionEvent e) {
//            if (doubleTapListener != null) {
//                return doubleTapListener.onDoubleTapEvent(e);
//            }
//            return false;
//        }
//    }
//
//    public interface OnTouchImageViewListener {
//        public void onMove();
//    }
//
//
//    private class PrivateOnTouchListener implements OnTouchListener {
//
//
//        private PointF last = new PointF();
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            mScaleDetector.onTouchEvent(event);
//            mGestureDetector.onTouchEvent(event);
//            PointF curr = new PointF(event.getX(), event.getY());
//
//            if (state == State.NONE || state == State.DRAG || state == State.FLING) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        last.set(curr);
//                        if (fling != null)
//                            fling.cancelFling();
//                        setState(State.DRAG);
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//                        if (state == State.DRAG) {
//                            float deltaX = curr.x - last.x;
//                            float deltaY = curr.y - last.y;
//                            float fixTransX = getFixDragTrans(deltaX, viewWidth, getImageWidth());
//                            float fixTransY = getFixDragTrans(deltaY, viewHeight, getImageHeight());
//                            matrix.postTranslate(fixTransX, fixTransY);
//                            fixTrans();
//                            last.set(curr.x, curr.y);
//                        }
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_POINTER_UP:
//                        setState(State.NONE);
//                        break;
//                }
//            }
//
//            setImageMatrix(matrix);
//
//
//            if (userTouchListener != null) {
//                userTouchListener.onTouch(v, event);
//            }
//
//
//            if (touchImageViewListener != null) {
//                touchImageViewListener.onMove();
//            }
//
//
//            return true;
//        }
//    }
//
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScaleBegin(ScaleGestureDetector detector) {
//            setState(State.ZOOM);
//            return true;
//        }
//
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY(), true);
//
//
//            if (touchImageViewListener != null) {
//                touchImageViewListener.onMove();
//            }
//            return true;
//        }
//
//        @Override
//        public void onScaleEnd(ScaleGestureDetector detector) {
//            super.onScaleEnd(detector);
//            setState(State.NONE);
//            boolean animateToZoomBoundary = false;
//            float targetZoom = normalizedScale;
//            if (normalizedScale > maxScale) {
//                targetZoom = maxScale;
//                animateToZoomBoundary = true;
//
//            } else if (normalizedScale < minScale) {
//                targetZoom = minScale;
//                animateToZoomBoundary = true;
//            }
//
//            if (animateToZoomBoundary) {
//                DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, viewWidth / 2, viewHeight / 2, true);
//                compatPostOnAnimation(doubleTap);
//            }
//        }
//    }
//
//    private void scaleImage(double deltaScale, float focusX, float focusY, boolean stretchImageToSuper) {
//
//        float lowerScale, upperScale;
//        if (stretchImageToSuper) {
//            lowerScale = superMinScale;
//            upperScale = superMaxScale;
//
//        } else {
//            lowerScale = minScale;
//            upperScale = maxScale;
//        }
//
//        float origScale = normalizedScale;
//        normalizedScale *= deltaScale;
//        if (normalizedScale > upperScale) {
//            normalizedScale = upperScale;
//            deltaScale = upperScale / origScale;
//        } else if (normalizedScale < lowerScale) {
//            normalizedScale = lowerScale;
//            deltaScale = lowerScale / origScale;
//        }
//
//        matrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);
//        fixScaleTrans();
//    }
//
//
//    private class DoubleTapZoom implements Runnable {
//
//        private long startTime;
//        private static final float ZOOM_TIME = 500;
//        private float startZoom, targetZoom;
//        private float bitmapX, bitmapY;
//        private boolean stretchImageToSuper;
//        private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
//        private PointF startTouch;
//        private PointF endTouch;
//
//        DoubleTapZoom(float targetZoom, float focusX, float focusY, boolean stretchImageToSuper) {
//            setState(State.ANIMATE_ZOOM);
//            startTime = System.currentTimeMillis();
//            this.startZoom = normalizedScale;
//            this.targetZoom = targetZoom;
//            this.stretchImageToSuper = stretchImageToSuper;
//            PointF bitmapPoint = transformCoordTouchToBitmap(focusX, focusY, false);
//            this.bitmapX = bitmapPoint.x;
//            this.bitmapY = bitmapPoint.y;
//
//
//            startTouch = transformCoordBitmapToTouch(bitmapX, bitmapY);
//            endTouch = new PointF(viewWidth / 2, viewHeight / 2);
//        }
//
//        @Override
//        public void run() {
//            float t = interpolate();
//            double deltaScale = calculateDeltaScale(t);
//            scaleImage(deltaScale, bitmapX, bitmapY, stretchImageToSuper);
//            translateImageToCenterTouchPosition(t);
//            fixScaleTrans();
//            setImageMatrix(matrix);
//
//            //
//            // OnTouchImageViewListener is set: double tap runnable updates listener
//            // with every frame.
//            //
//            if (touchImageViewListener != null) {
//                touchImageViewListener.onMove();
//            }
//
//            if (t < 1f) {
//                //
//                // We haven't finished zooming
//                //
//                compatPostOnAnimation(this);
//
//            } else {
//
//                setState(State.NONE);
//            }
//        }
//
//
//        private void translateImageToCenterTouchPosition(float t) {
//            float targetX = startTouch.x + t * (endTouch.x - startTouch.x);
//            float targetY = startTouch.y + t * (endTouch.y - startTouch.y);
//            PointF curr = transformCoordBitmapToTouch(bitmapX, bitmapY);
//            matrix.postTranslate(targetX - curr.x, targetY - curr.y);
//        }
//
//
//        private float interpolate() {
//            long currTime = System.currentTimeMillis();
//            float elapsed = (currTime - startTime) / ZOOM_TIME;
//            elapsed = Math.min(1f, elapsed);
//            return interpolator.getInterpolation(elapsed);
//        }
//
//
//        private double calculateDeltaScale(float t) {
//            double zoom = startZoom + t * (targetZoom - startZoom);
//            return zoom / normalizedScale;
//        }
//    }
//
//
//    private PointF transformCoordTouchToBitmap(float x, float y, boolean clipToBitmap) {
//        matrix.getValues(m);
//        float origW = getDrawable().getIntrinsicWidth();
//        float origH = getDrawable().getIntrinsicHeight();
//        float transX = m[Matrix.MTRANS_X];
//        float transY = m[Matrix.MTRANS_Y];
//        float finalX = ((x - transX) * origW) / getImageWidth();
//        float finalY = ((y - transY) * origH) / getImageHeight();
//
//        if (clipToBitmap) {
//            finalX = Math.min(Math.max(finalX, 0), origW);
//            finalY = Math.min(Math.max(finalY, 0), origH);
//        }
//
//        return new PointF(finalX, finalY);
//    }
//
//
//    private PointF transformCoordBitmapToTouch(float bx, float by) {
//        matrix.getValues(m);
//        float origW = getDrawable().getIntrinsicWidth();
//        float origH = getDrawable().getIntrinsicHeight();
//        float px = bx / origW;
//        float py = by / origH;
//        float finalX = m[Matrix.MTRANS_X] + getImageWidth() * px;
//        float finalY = m[Matrix.MTRANS_Y] + getImageHeight() * py;
//        return new PointF(finalX, finalY);
//    }
//
//
//    private class Fling implements Runnable {
//
//        CompatScroller scroller;
//        int currX, currY;
//
//        Fling(int velocityX, int velocityY) {
//            setState(State.FLING);
//            scroller = new CompatScroller(context);
//            matrix.getValues(m);
//
//            int startX = (int) m[Matrix.MTRANS_X];
//            int startY = (int) m[Matrix.MTRANS_Y];
//            int minX, maxX, minY, maxY;
//
//            if (getImageWidth() > viewWidth) {
//                minX = viewWidth - (int) getImageWidth();
//                maxX = 0;
//
//            } else {
//                minX = maxX = startX;
//            }
//
//            if (getImageHeight() > viewHeight) {
//                minY = viewHeight - (int) getImageHeight();
//                maxY = 0;
//
//            } else {
//                minY = maxY = startY;
//            }
//
//            scroller.fling(startX, startY, (int) velocityX, (int) velocityY, minX,
//                    maxX, minY, maxY);
//            currX = startX;
//            currY = startY;
//        }
//
//        public void cancelFling() {
//            if (scroller != null) {
//                setState(State.NONE);
//                scroller.forceFinished(true);
//            }
//        }
//
//        @Override
//        public void run() {
//
//
//            if (touchImageViewListener != null) {
//                touchImageViewListener.onMove();
//            }
//
//            if (scroller.isFinished()) {
//                scroller = null;
//                return;
//            }
//
//            if (scroller.computeScrollOffset()) {
//                int newX = scroller.getCurrX();
//                int newY = scroller.getCurrY();
//                int transX = newX - currX;
//                int transY = newY - currY;
//                currX = newX;
//                currY = newY;
//                matrix.postTranslate(transX, transY);
//                fixTrans();
//                setImageMatrix(matrix);
//                compatPostOnAnimation(this);
//            }
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
//    private class CompatScroller {
//        Scroller scroller;
//        OverScroller overScroller;
//        boolean isPreGingerbread;
//
//        public CompatScroller(Context context) {
//            if (VERSION.SDK_INT < VERSION_CODES.GINGERBREAD) {
//                isPreGingerbread = true;
//                scroller = new Scroller(context);
//
//            } else {
//                isPreGingerbread = false;
//                overScroller = new OverScroller(context);
//            }
//        }
//
//        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
//            if (isPreGingerbread) {
//                scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
//            } else {
//                overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
//            }
//        }
//
//        public void forceFinished(boolean finished) {
//            if (isPreGingerbread) {
//                scroller.forceFinished(finished);
//            } else {
//                overScroller.forceFinished(finished);
//            }
//        }
//
//        public boolean isFinished() {
//            if (isPreGingerbread) {
//                return scroller.isFinished();
//            } else {
//                return overScroller.isFinished();
//            }
//        }
//
//        public boolean computeScrollOffset() {
//            if (isPreGingerbread) {
//                return scroller.computeScrollOffset();
//            } else {
//                overScroller.computeScrollOffset();
//                return overScroller.computeScrollOffset();
//            }
//        }
//
//        public int getCurrX() {
//            if (isPreGingerbread) {
//                return scroller.getCurrX();
//            } else {
//                return overScroller.getCurrX();
//            }
//        }
//
//        public int getCurrY() {
//            if (isPreGingerbread) {
//                return scroller.getCurrY();
//            } else {
//                return overScroller.getCurrY();
//            }
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private void compatPostOnAnimation(Runnable runnable) {
//        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
//            postOnAnimation(runnable);
//
//        } else {
//            postDelayed(runnable, 1000 / 60);
//        }
//    }
//
//    private class ZoomVariables {
//        public float scale;
//        public float focusX;
//        public float focusY;
//        public ScaleType scaleType;
//
//        public ZoomVariables(float scale, float focusX, float focusY, ScaleType scaleType) {
//            this.scale = scale;
//            this.focusX = focusX;
//            this.focusY = focusY;
//            this.scaleType = scaleType;
//        }
//    }
//
//    private void printMatrixInfo() {
//        float[] n = new float[9];
//        matrix.getValues(n);
//        Log.d(DEBUG, "Scale: " + n[Matrix.MSCALE_X] + " TransX: " + n[Matrix.MTRANS_X] + " TransY: " + n[Matrix.MTRANS_Y]);
//    }
//}







/*********************************************/
public class TouchImageView extends ImageView {
	private Paint borderPaint = null;
	private Paint backgroundPaint = null;

	private float mPosX = 0f;
	private float mPosY = 0f;

	private float mLastTouchX;
	private float mLastTouchY;
	private static final int INVALID_POINTER_ID = -1;
	private static final String LOG_TAG = "TouchImageView";

	// The ‘active pointer’ is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;

	public TouchImageView(Context context) {
		this(context, null, 0);
	}

	public TouchImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;

	// Existing code ...
	public TouchImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// Create our ScaleGestureDetector
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

		borderPaint = new Paint();
		borderPaint.setARGB(255, 255,255,255);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(4);

		backgroundPaint = new Paint();
//		backgroundPaint.setARGB(255,255,255,255);
//		backgroundPaint.setStyle(Paint.Style.FILL);

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);

		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX();
			final float y = ev.getY();

			mLastTouchX = x;
			mLastTouchY = y;

			mActivePointerId = ev.getPointerId(0);
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			final float x = ev.getX(pointerIndex);
			final float y = ev.getY(pointerIndex);

			// Only move if the ScaleGestureDetector isn't processing a gesture.
			if (!mScaleDetector.isInProgress()) {
				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				mPosX += dx;
				mPosY += dy;

				invalidate();
			}

			mLastTouchX = x;
			mLastTouchY = y;
			break;
		}

		case MotionEvent.ACTION_UP: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mLastTouchX = ev.getX(newPointerIndex);
				mLastTouchY = ev.getY(newPointerIndex);
				mActivePointerId = ev.getPointerId(newPointerIndex);
			}
			break;
		}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, borderPaint);
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, backgroundPaint);
		if (this.getDrawable() != null) {
			canvas.save();
			canvas.translate(mPosX, mPosY);

			Matrix matrix = new Matrix();
			matrix.postScale(mScaleFactor, mScaleFactor, pivotPointX,
					pivotPointY);
			// canvas.setMatrix(matrix);

			canvas.drawBitmap(
					((BitmapDrawable) this.getDrawable()).getBitmap(), matrix,
					null);

			// this.getDrawable().draw(canvas);
			canvas.restore();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable
	 * )
	 */
	@Override
	public void setImageDrawable(Drawable drawable) {
		// Constrain to given size but keep aspect ratio
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		mLastTouchX = mPosX = 0;
		mLastTouchY = mPosY = 0;

		int borderWidth = (int) borderPaint.getStrokeWidth();
		mScaleFactor = Math.min(((float) getLayoutParams().width - borderWidth)
				/ width, ((float) getLayoutParams().height - borderWidth)
				/ height);
		pivotPointX = (((float) getLayoutParams().width - borderWidth) - (int) (width * mScaleFactor)) / 2;
		pivotPointY = (((float) getLayoutParams().height - borderWidth) - (int) (height * mScaleFactor)) / 2;
		super.setImageDrawable(drawable);
	}

	float pivotPointX = 0f;
	float pivotPointY = 0f;

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();

			pivotPointX = detector.getFocusX();
			pivotPointY = detector.getFocusY();

			Log.d(LOG_TAG, "mScaleFactor " + mScaleFactor);
			Log.d(LOG_TAG, "pivotPointY " + pivotPointY + ", pivotPointX= "
					+ pivotPointX);
			mScaleFactor = Math.max(0.05f, mScaleFactor);

			invalidate();
			return true;
		}
	}
	
}