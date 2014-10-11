package com.mars.snickers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mars.snickers.helper.IconizedMenu;
import com.mars.snickers.helper.ImageHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_PictureAvatarMaker extends Fragment implements
		OnTouchListener {

	public final String TAG = "PictureAvatarMaker";
	FrameLayout container;
	Button chooseChar, pickMood, chooseProp, apply;
	ImageView userPic;
	ImageView imagezoom;
	LinearLayout iconContainer;
	ImageView icon_char, icon_mood, icon_prop;
	String characterSelection = "1", moodSelection = "1", propSelection = "1";
	public static Bitmap avatarBitmap = null;
	boolean isCharacterChoose = false, isMoodChoose = false,
			isPropApplied = false;
	Float zoom = 5f;
	Float zoomOut = 0.025f;
	boolean isZoomOut = false;

	public static final int FLIP_VERTICAL = 1;
	public static final int FLIP_HORIZONTAL = 2;

	public Fragment_PictureAvatarMaker() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_picture_avatar_maker,
				parent, false);
		container = (FrameLayout) view
				.findViewById(R.id.fpam_fl_imageContainer);
		apply = (Button) view.findViewById(R.id.fpam_btn_apply);
		chooseChar = (Button) view.findViewById(R.id.fpam_btn_chooseCharacter);
		pickMood = (Button) view.findViewById(R.id.fpam_btn_pickMood);
		chooseProp = (Button) view.findViewById(R.id.fpam_btn_chooseProp);
		userPic = (ImageView) view.findViewById(R.id.fpam_iv_userPic);
		Drawable blank = userPic.getDrawable();
		Bitmap image = ImageHelper.decodeFile(
				getArguments().getString("selectedImagePath"),
				ImageHelper.getDeviceWidth(getActivity()),
				ImageHelper.getDeviceHeight(getActivity()),
				ImageHelper.ScalingLogic.FIT);
		imagezoom = (ImageView) view.findViewById(R.id.fpam_iv_touchImageView);
		// imagezoom.setMaxZoom(4f);

		// userPic.bringToFront();
		if (Fragment_GetPicture.galleryLoad) {
			imagezoom.setRotation(0);
			imagezoom.setImageBitmap(image);
		} else {
			imagezoom.setRotation(270);
			imagezoom.setImageBitmap(flipImage(image, 1));
		}

		imagezoom.setOnTouchListener(this);
		// imagezoom.setMaxZoom(zoom);
		// imagezoom.setZoom(zoomOut);
		iconContainer = (LinearLayout) view
				.findViewById(R.id.fpam_ll_propContainer);
		icon_char = (ImageView) view.findViewById(R.id.fpam_iv_char);
		icon_mood = (ImageView) view.findViewById(R.id.fpam_iv_mood);
		icon_prop = (ImageView) view.findViewById(R.id.fpam_iv_prop);

		pickMood.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pickAvatarMood(view);
			}
		});
		pickMood.setEnabled(false);

		chooseProp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseAvatarProp(view);
			}
		});
		chooseProp.setEnabled(false);

		apply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				applyEffect();
			}
		});
		apply.setEnabled(false);

		chooseChar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseCharacter(view);
			}
		});

		return view;
	}

	private void chooseAvatarProp(View v) {

		if (isCharacterChoose & isMoodChoose) {
			Context wrapper = new ContextThemeWrapper(getActivity(),
					R.style.PopupMenu);
			IconizedMenu popup = new IconizedMenu(wrapper, v);

			int position = Integer.valueOf(characterSelection);
			switch (position) {
			case 1:
				popup.getMenuInflater().inflate(R.menu.diva_prop,
						popup.getMenu());
				break;
			case 2:
				popup.getMenuInflater().inflate(R.menu.grandma_prop,
						popup.getMenu());
				break;
			case 3:

				popup.getMenuInflater().inflate(R.menu.caveman_prop,
						popup.getMenu());
				break;
			}

			popup.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					propSelection = String.valueOf(item.getOrder());
					isPropApplied = true;
					icon_prop.setImageDrawable(item.getIcon());
					apply.setEnabled(true);
					chooseAvatar();
					return true;
				}
			});
			popup.show();
		}

	}

	private void pickAvatarMood(View v) {

		if (isCharacterChoose) {
			Context wrapper = new ContextThemeWrapper(getActivity(),
					R.style.PopupMenu);
			IconizedMenu popup = new IconizedMenu(wrapper, v);
			popup.getMenuInflater()
					.inflate(R.menu.choose_mood, popup.getMenu());
			popup.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					moodSelection = String.valueOf(item.getOrder());
					isMoodChoose = true;
					icon_mood.setImageDrawable(item.getIcon());
					chooseProp.setEnabled(true);
					if (isCharacterChoose & isMoodChoose & isPropApplied)
						chooseAvatar();
					return true;
				}
			});
			popup.show();
		}

	}

	private void chooseCharacter(View v) {
		try{
		Context wrapper = new ContextThemeWrapper(getActivity(),
				R.style.PopupMenu);
		IconizedMenu popup = new IconizedMenu(wrapper, v);
		popup.getMenuInflater().inflate(R.menu.choose_character,
				popup.getMenu());
		popup.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				characterSelection = String.valueOf(item.getOrder());
				isCharacterChoose = true;
				pickMood.setEnabled(true);
				icon_char.setImageDrawable(item.getIcon());
				if (isCharacterChoose & isMoodChoose & isPropApplied)
					chooseAvatar();
				return true;
			}
		});
		popup.show();
		}
catch (Exception e) {
	// TODO: handle exception
	e.printStackTrace();
	Toast.makeText(getActivity(), String.valueOf(e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
}
	}

	private void applyEffect() {
		if (isCharacterChoose & isMoodChoose & isPropApplied) {
			iconContainer.setVisibility(View.INVISIBLE);
			new SavePhotoTask().execute(container);
		}

	}

	public Bitmap viewToBitmap(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		return bitmap;
	}

	class SavePhotoTask extends AsyncTask<View, String, String> {

		@Override
		protected String doInBackground(View... view) {
			Bitmap bitmap = viewToBitmap(view[0]);
			File photo = new File(getActivity().getFilesDir(),
					getString(R.string.userAvatarFileName));
			if (photo.exists()) {
				photo.delete();
			}
			FileOutputStream fos = null;

			try {
				fos = new FileOutputStream(photo);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			} catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			} finally {

				if (fos != null)
					try {
						bitmap.recycle();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}

			return photo.getAbsolutePath().toString();
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if (s != null) {
				File photo = new File(getActivity().getFilesDir(),
						getString(R.string.userAvatarFileName));
				isZoomOut = false;
				FragmentManager fm = getActivity().getSupportFragmentManager();
				Fragment_FinalScreen ffs = new Fragment_FinalScreen();
				Bundle b = new Bundle();
				b.putString("avatarImagePath", photo.getAbsolutePath());
				ffs.setArguments(b);
				fm.popBackStack(ffs.TAG,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
				fm.beginTransaction().replace(R.id.container, ffs, ffs.TAG)
						.addToBackStack(ffs.TAG).commit();
				// fm.beginTransaction().replace(R.id.container,
				// ffs).addToBackStack(null).commit();
			} else {
			}
		}
	}

	private void chooseAvatar() {
		int position = Integer.valueOf(characterSelection + moodSelection);
		switch (position) {
		case 11:
			setImage(R.array.diva_sad);
			// setImage(R.array.caveman_clumpsy);
			break;
		case 12:
			setImage(R.array.diva_crazy);
			// setImage(R.array.caveman_crazy);
			break;
		case 13:
			setImage(R.array.diva_clumpsy);
			// setImage(R.array.caveman_irritated);
			break;
		case 14:
			setImage(R.array.diva_irritated);
			// setImage(R.array.caveman_sad);
			break;

		case 21:
			setImage(R.array.grandma_sad);
			// setImage(R.array.grandma_clumpsy);
			break;
		case 22:
			setImage(R.array.grandma_crazy);
			// setImage(R.array.grandma_crazy);
			break;
		case 23:
			setImage(R.array.grandma_clumpsy);
			// setImage(R.array.grandma_irritated);
			break;
		case 24:
			setImage(R.array.grandma_irritated);
			// setImage(R.array.grandma_sad);
			break;

		case 31:
			setImage(R.array.caveman_sad);
			// setImage(R.array.diva_clumpsy);
			break;
		case 32:
			setImage(R.array.caveman_crazy);
			// setImage(R.array.diva_crazy);
			break;
		case 33:
			setImage(R.array.caveman_clumpsy);
			// setImage(R.array.diva_irritated);
			break;
		case 34:
			setImage(R.array.caveman_sad);
			// setImage(R.array.diva_sad);
			break;
		}
	}

	private void setImage(int mood) {
		TypedArray imgs = getResources().obtainTypedArray(mood);
		int prop = Integer.valueOf(propSelection) - 1;
		if (prop < 0)
			prop = 0;
		if (avatarBitmap != null)
			avatarBitmap.recycle();
		avatarBitmap = ImageHelper.decodeResource(getResources(),
				imgs.getResourceId(prop, 0),
				ImageHelper.getDeviceWidth(getActivity()),
				ImageHelper.getDeviceHeight(getActivity()),
				ImageHelper.ScalingLogic.FIT);
		userPic.setImageBitmap(avatarBitmap);
		imagezoom.getLayoutParams().height = avatarBitmap.getHeight();
		imagezoom.getLayoutParams().width = avatarBitmap.getWidth();
		if (!isZoomOut) {
			// imagezoom.setZoom(zoomOut);
			isZoomOut = true;
		}
		imgs.recycle();
	}

	public Bitmap flipImage(Bitmap src, int type) {
		// create new matrix for transformation
		Matrix matrix = new Matrix();
		// if vertical
		if (type == FLIP_VERTICAL) {
			// y = y * -1
			matrix.preScale(1.0f, -1.0f);
		}
		// if horizonal
		else if (type == FLIP_HORIZONTAL) {
			// x = x * -1
			matrix.preScale(-1.0f, 1.0f);
			// unknown type
		} else {
			return null;
		}

		// return transformed image
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
				matrix, true);
	}

	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	String savedItemClicked;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		ImageView view = (ImageView) v;
		dumpEvent(event);

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			Log.d(TAG, "mode=NONE");
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		return true;
	}

	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d(TAG, sb.toString());
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
