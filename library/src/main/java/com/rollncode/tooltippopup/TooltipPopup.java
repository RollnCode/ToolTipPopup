package com.rollncode.tooltippopup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.lang.ref.WeakReference;

/**
 * @author Sviatoslav Koliesnik kolesniksy@gmail.com
 * @since 15.11.16
 */
public class TooltipPopup extends PopupWindow
        implements PopupWindow.OnDismissListener, RequestListener<Integer, Bitmap>, Runnable, View.OnClickListener {

    private final WeakReference<View> mAnchor;
    @DrawableRes
    private final int mImageResource;
    private final ImageView mImageView;
    private final boolean mAboveAnchor;
    private final boolean mFullWidth;
    private final int mGravity;
    private final int mActionBarHeight;
    private final boolean mNeedSetLeftRightMargin;
    private final int mScreenWidth;
    private Point mImageSize;
    private TooltipPopupDismissListener mListener;

    public static class Builder {

        // Mandatory parameters
        private final View mAnchor;
        @DrawableRes
        private final int mImageResource;
        private final int mScreenWidth;

        // Additional options - initialized to default values
        private boolean mAboveAnchor;
        private boolean mFullWidth;
        private int mGravity;
        private int mActionBarHeight;
        private boolean mNeedSetLeftRightMargin;
        private TooltipPopupDismissListener mListener;

        public Builder(@NonNull View anchor, @DrawableRes int imageResource) {
            mAnchor = anchor;

            final WindowManager windowManager = (WindowManager) anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
            final Point screenSize = new Point();
            windowManager.getDefaultDisplay().getSize(screenSize);

            mScreenWidth = screenSize.x;
            mImageResource = imageResource;

            mAboveAnchor = true;
            mFullWidth = false;
            mGravity = Gravity.CENTER_HORIZONTAL;
            mActionBarHeight = Integer.MIN_VALUE;
            mNeedSetLeftRightMargin = false;
        }

        public Builder setListener(TooltipPopupDismissListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * default - true
         */
        public Builder setAboveAnchor(boolean aboveAnchor) {
            mAboveAnchor = aboveAnchor;
            return this;
        }

        /**
         * default - false
         */
        public Builder setFullWidth(boolean fullWidth) {
            mFullWidth = fullWidth;
            return this;
        }

        /**
         * default - Gravity.CENTER_HORIZONTAL
         */
        public Builder setGravity(int gravity) {
            mGravity = gravity;
            return this;
        }

        /**
         * Set if need to minus the action bar height (default - Integer.MIN_VALUE)
         */
        public Builder setActionBarHeight(int actionBarHeight) {
            mActionBarHeight = actionBarHeight;
            return this;
        }

        /**
         * default - false
         */
        public Builder setNeedSetLeftRightMargin(boolean needSetLeftRightMargin) {
            mNeedSetLeftRightMargin = needSetLeftRightMargin;
            return this;
        }

        public TooltipPopup show() {
            return new TooltipPopup(this);
        }
    }

    private TooltipPopup(Builder builder) {
        super(builder.mAnchor.getContext());
        final Context context = builder.mAnchor.getContext();

        FrameLayout fl = new FrameLayout(context);
        fl.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        fl.setBackground(null);

        mImageView = new ImageView(context);
        LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final float scale = context.getResources().getDisplayMetrics().density;
        int ivMargin = (int) (10 * scale + 0.5f); // 10 dp
        lpImage.setMargins(ivMargin, 0, ivMargin, 0);
        mImageView.setLayoutParams(lpImage);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        fl.addView(mImageView);

        fl.setOnClickListener(this);

        super.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        super.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        super.setAnimationStyle(R.style.popup_animation);
        super.setContentView(fl);

        super.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        super.setOnDismissListener(this);
        super.setFocusable(true);

        mImageResource = builder.mImageResource;
        mAnchor = new WeakReference<>(builder.mAnchor);
        mAboveAnchor = builder.mAboveAnchor;
        mFullWidth = builder.mFullWidth;
        mGravity = builder.mGravity;
        mActionBarHeight = builder.mActionBarHeight;
        mNeedSetLeftRightMargin = builder.mNeedSetLeftRightMargin;
        mScreenWidth = builder.mScreenWidth;

        mListener = builder.mListener;

        Glide.with(context).load(mImageResource).asBitmap().listener(this).into(mImageView);
    }

    @Override
    public void onDismiss() {
        if (mListener == null) {
            return;
        }
        mListener.onDismiss(mImageResource);
    }

    @Override
    public boolean onException(Exception e, Integer model, Target<Bitmap> target, boolean isFirstResource) {
        dismiss();
        return false;
    }

    @Override
    public boolean onResourceReady(Bitmap resource, Integer model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
        final View anchor = mAnchor.get();
        if (anchor == null) {
            dismiss();

        } else {
            final float bmpWidth = resource.getWidth();
            final float bmpHeight = resource.getHeight();

            final float screenWidthHalf = mScreenWidth / 2F;

            final float factorBitmap = bmpWidth / bmpHeight;
            final float factorScreenBitmap = screenWidthHalf / bmpWidth;

            final int width = mFullWidth ? mScreenWidth : (int) (bmpWidth * factorScreenBitmap);
            final int height = (int) (width / factorBitmap);

            mImageSize = new Point(width, height);
            {
                int xOff = 0;
                int yOff = 0;
                if (mAboveAnchor) {
                    if (mActionBarHeight == Integer.MIN_VALUE) {
                        yOff = -(height + anchor.getHeight());

                    } else {
                        yOff = -(height - mActionBarHeight);
                    }
                }
                super.showAsDropDown(anchor, xOff, yOff);
            }
            mImageView.post(this);
        }
        return false;
    }

    @Override
    public void run() {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mImageView.getLayoutParams();
        params.width = mImageSize.x;
        params.height = mImageSize.y;
        params.gravity = mGravity;
        if (mFullWidth) {
            params.leftMargin = params.rightMargin = 0;

        } else if (mNeedSetLeftRightMargin) {
            params.leftMargin = params.leftMargin * 2;
            params.rightMargin = params.rightMargin * 2;
        }
        mImageView.requestLayout();
        mImageView.animate().alpha(1F).start();
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
