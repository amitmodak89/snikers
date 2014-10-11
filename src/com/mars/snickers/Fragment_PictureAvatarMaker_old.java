//package com.mars.snickers;
//
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.util.Log;
//import android.view.ContextThemeWrapper;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//import com.mars.snickers.helper.IconizedMenu;
//import com.mars.snickers.helper.ImageHelper;
//import com.mars.snickers.helper.TouchImageView;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class Fragment_PictureAvatarMaker_old extends Fragment {
//
//    public final String TAG = "PictureAvatarMaker";
//    FrameLayout container;
//    Button chooseChar, pickMood, chooseProp, apply;
//    ImageView userPic;
//    TouchImageView imagezoom;
//    LinearLayout iconContainer;
//    ImageView icon_char, icon_mood, icon_prop;
//    String characterSelection = "1", moodSelection = "1", propSelection = "1";
//    Bitmap avatarBitmap = null;
//    boolean isCharacterChoose = false, isMoodChoose = false, isPropApplied = false;
//    Float zoom = 5f;
//    Float zoomOut = 0.05f;
//    boolean isZoomOut = false;
//
//
//    public Fragment_PictureAvatarMaker_old() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
//                             Bundle savedInstanceState) {
//
//
//        View view = inflater.inflate(R.layout.fragment_picture_avatar_maker, parent, false);
//        container = (FrameLayout) view.findViewById(R.id.fpam_fl_imageContainer);
//        apply = (Button) view.findViewById(R.id.fpam_btn_apply);
//        chooseChar = (Button) view.findViewById(R.id.fpam_btn_chooseCharacter);
//        pickMood = (Button) view.findViewById(R.id.fpam_btn_pickMood);
//        chooseProp = (Button) view.findViewById(R.id.fpam_btn_chooseProp);
//        userPic = (ImageView) view.findViewById(R.id.fpam_iv_userPic);
//        Drawable blank = userPic.getDrawable();
//        Bitmap image = ImageHelper.decodeFile(getArguments().getString("selectedImagePath"), ImageHelper.getDeviceWidth(getActivity()), ImageHelper.getDeviceHeight(getActivity()), ImageHelper.ScalingLogic.FIT);
//        imagezoom = (TouchImageView) view.findViewById(R.id.fpam_iv_touchImageView);
//        imagezoom.setImageBitmap(image);
//        imagezoom.setMaxZoom(zoom);
////        imagezoom.setZoom(zoomOut);
//        iconContainer = (LinearLayout) view.findViewById(R.id.fpam_ll_propContainer);
//        icon_char = (ImageView) view.findViewById(R.id.fpam_iv_char);
//        icon_mood = (ImageView) view.findViewById(R.id.fpam_iv_mood);
//        icon_prop = (ImageView) view.findViewById(R.id.fpam_iv_prop);
//
//        pickMood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                pickAvatarMood(view);
//            }
//        });
//        pickMood.setEnabled(false);
//
//        chooseProp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chooseAvatarProp(view);
//            }
//        });
//        chooseProp.setEnabled(false);
//
//        apply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                applyEffect();
//            }
//        });
//        apply.setEnabled(false);
//
//        chooseChar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chooseCharacter(view);
//            }
//        });
//
//        return view;
//    }
//
//
//    private void chooseAvatarProp(View v) {
//
//        if (isCharacterChoose & isMoodChoose) {
//            Context wrapper = new ContextThemeWrapper(getActivity(), R.style.PopupMenu);
//            IconizedMenu popup = new IconizedMenu(wrapper, v);
//
//
//            int position = Integer.valueOf(characterSelection);
//            switch (position) {
//                case 1:
//                    popup.getMenuInflater().inflate(R.menu.caveman_prop, popup.getMenu());
//                    break;
//                case 2:
//                    popup.getMenuInflater().inflate(R.menu.grandma_prop, popup.getMenu());
//                    break;
//                case 3:
//                    popup.getMenuInflater().inflate(R.menu.diva_prop, popup.getMenu());
//                    break;
//            }
//
//
//            popup.setOnMenuItemClickListener(
//                    new IconizedMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            propSelection = String.valueOf(item.getOrder());
//                            isPropApplied = true;
//                            icon_prop.setImageDrawable(item.getIcon());
//                            apply.setEnabled(true);
//                            chooseAvatar();
//                            return true;
//                        }
//                    });
//            popup.show();
//        }
//
//    }
//
//    private void pickAvatarMood(View v) {
//
//        if (isCharacterChoose) {
//            Context wrapper = new ContextThemeWrapper(getActivity(), R.style.PopupMenu);
//            IconizedMenu popup = new IconizedMenu(wrapper, v);
//            popup.getMenuInflater().inflate(R.menu.choose_mood, popup.getMenu());
//            popup.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
//
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    moodSelection = String.valueOf(item.getOrder());
//                    isMoodChoose = true;
//                    icon_mood.setImageDrawable(item.getIcon());
//                    chooseProp.setEnabled(true);
//                    if (isCharacterChoose & isMoodChoose & isPropApplied)
//                        chooseAvatar();
//                    return true;
//                }
//            });
//            popup.show();
//        }
//
//    }
//
//    private void chooseCharacter(View v) {
//        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.PopupMenu);
//        IconizedMenu popup = new IconizedMenu(wrapper, v);
//        popup.getMenuInflater().inflate(R.menu.choose_character, popup.getMenu());
//        popup.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
//
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                characterSelection = String.valueOf(item.getOrder());
//                isCharacterChoose = true;
//                pickMood.setEnabled(true);
//                icon_char.setImageDrawable(item.getIcon());
//                if (isCharacterChoose & isMoodChoose & isPropApplied)
//                    chooseAvatar();
//                return true;
//            }
//        });
//        popup.show();
//
//    }
//
//    private void applyEffect() {
//        if (isCharacterChoose & isMoodChoose & isPropApplied) {
//            iconContainer.setVisibility(View.INVISIBLE);
//            new SavePhotoTask().execute(container);
//        }
//
//    }
//
//
//    public Bitmap viewToBitmap(View view) {
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//        return bitmap;
//    }
//
//
//    class SavePhotoTask extends AsyncTask<View, String, String> {
//
//        @Override
//        protected String doInBackground(View... view) {
//            Bitmap bitmap = viewToBitmap(view[0]);
//            File photo = new File(getActivity().getFilesDir(), getString(R.string.userAvatarFileName));
//            if (photo.exists()) {
//                photo.delete();
//            }
//            FileOutputStream fos = null;
//
//            try {
//                fos = new FileOutputStream(photo);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
//            } catch (java.io.IOException e) {
//                Log.e("PictureDemo", "Exception in photoCallback", e);
//            } finally {
//
//                if (fos != null)
//                    try {
//                        bitmap.recycle();
//                        fos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//            }
//
//            return photo.getAbsolutePath().toString();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            if (s != null) {
//                File photo = new File(getActivity().getFilesDir(), getString(R.string.userAvatarFileName));
//                isZoomOut = false;
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                Fragment_FinalScreen ffs = new Fragment_FinalScreen();
//                Bundle b = new Bundle();
//                b.putString("avatarImagePath", photo.getAbsolutePath());
//                ffs.setArguments(b);
//                fm.popBackStack(ffs.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                fm.beginTransaction().replace(R.id.container, ffs, ffs.TAG).addToBackStack(ffs.TAG).commit();
////                fm.beginTransaction().replace(R.id.container, ffs).addToBackStack(null).commit();
//            } else {
//            }
//        }
//    }
//
//    private void chooseAvatar() {
//        int position = Integer.valueOf(characterSelection + moodSelection);
//        switch (position) {
//            case 11:
//                setImage(R.array.caveman_clumpsy);
//                break;
//            case 12:
//                setImage(R.array.caveman_crazy);
//                break;
//            case 13:
//                setImage(R.array.caveman_irritated);
//                break;
//            case 14:
//                setImage(R.array.caveman_sad);
//                break;
//
//            case 21:
//                setImage(R.array.grandma_clumpsy);
//                break;
//            case 22:
//                setImage(R.array.grandma_crazy);
//                break;
//            case 23:
//                setImage(R.array.grandma_irritated);
//                break;
//            case 24:
//                setImage(R.array.grandma_sad);
//                break;
//
//            case 31:
//                setImage(R.array.diva_clumpsy);
//                break;
//            case 32:
//                setImage(R.array.diva_crazy);
//                break;
//            case 33:
//                setImage(R.array.diva_irritated);
//                break;
//            case 34:
//                setImage(R.array.diva_sad);
//                break;
//        }
//    }
//
//    private void setImage(int mood) {
//        TypedArray imgs = getResources().obtainTypedArray(mood);
//        int prop = Integer.valueOf(propSelection) - 1;
//        if (prop < 0) prop = 0;
//        if (avatarBitmap != null)
//            avatarBitmap.recycle();
//        avatarBitmap = ImageHelper.decodeResource(getResources(), imgs.getResourceId(prop, 0), ImageHelper.getDeviceWidth(getActivity()), ImageHelper.getDeviceHeight(getActivity()), ImageHelper.ScalingLogic.FIT);
//        userPic.setImageBitmap(avatarBitmap);
//        imagezoom.getLayoutParams().height = avatarBitmap.getHeight();
//        imagezoom.getLayoutParams().width = avatarBitmap.getWidth();
//        if(!isZoomOut) {
//            imagezoom.setZoom(zoomOut);
//            isZoomOut = true;
//        }
//        imgs.recycle();
//    }
//}
