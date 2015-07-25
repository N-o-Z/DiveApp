package com.example.nozery.diveapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.DialogFragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnProfileInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    //Local user profile member
    protected static UserProfile mProfile;

    //UI members
    private TextView mUsernameTextView;
    private TextView mEmailTextView;
    protected static TextView mNameTextView;
    protected static TextView mGenderTextView;
    protected static TextView mBirthdayTextView;
    protected static TextView mLanguageTextView;
    protected static TextView mCountryTextView;
    protected static TextView mCertificationTextView;
    protected static TextView mOrganizationTextView;
    protected static TextView mAdditionalCertTextView;
    protected static ImageView mProfilePicImageView;
    protected static ImageView mExpandedImageView;
    protected static LinearLayout mExpandedImageContainer;
    protected static ImageView mChangePictureView;
    protected static ImageView mRevertPictureView;
    protected static ImageView mTakePictureView;
    private static OnProfileInteractionListener mListener;

    //Camera members
    protected static Uri imageUri;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_GALLERY = 3;


    public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }

    public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
            super(context, R.layout.crop_layout, options);

            mOptions = options;

            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.crop_layout, null);

            CropOption item = mOptions.get(position);

            if (item != null) {
                ((ImageView) convertView.findViewById(R.id.iv_icon)).setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.tv_name)).setText(item.title);

                return convertView;
            }

            return null;
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param data Parameter 1.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(HashMap<String, String> data) {

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", data);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            HashMap<String, String> data = (HashMap<String, String>) getArguments()
                    .getSerializable("data");
            mProfile = new UserProfile(data);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Profile");
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mUsernameTextView = (TextView) view.findViewById(R.id.account_username_text);
        mEmailTextView = (TextView) view.findViewById(R.id.account_email_text);
        mNameTextView = (TextView) view.findViewById(R.id.profile_name_text);
        mGenderTextView = (TextView) view.findViewById(R.id.profile_gender_text);
        mBirthdayTextView = (TextView) view.findViewById(R.id.profile_birthday_text);
        mLanguageTextView = (TextView) view.findViewById(R.id.profile_language_text);
        mCountryTextView = (TextView) view.findViewById(R.id.profile_country_text);
        mCertificationTextView = (TextView) view.findViewById(R.id.profile_certification_text);
        mOrganizationTextView = (TextView) view.findViewById(R.id.profile_organization_text);
        mAdditionalCertTextView = (TextView) view.findViewById(R.id.profile_additional_cert_text);
        mProfilePicImageView = (ImageView) view.findViewById(R.id.profile_picture);
        mExpandedImageView = (ImageView) view.findViewById(R.id.expanded_image);
        mExpandedImageContainer = (LinearLayout) view.findViewById(R.id.expanded_image_container);
        mChangePictureView = (ImageView) view.findViewById(R.id.profile_add_image);
        mRevertPictureView = (ImageView) view.findViewById(R.id.profile_revert_image);
        mTakePictureView = (ImageView) view.findViewById(R.id.profile_open_camera);

        populateProfileFields();

        setNameDialog();
        setGenderDialog();
        setBirthdayDialog();
        setLanguageDialog();
        setCountryDialog();
        setCertificationDialog();
        setOrganizationDialog();
        setAdditionalCertDialog();
        setImageAnimationAndDialog();
        setImageButtons();

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FROM_CAMERA: {
                if (resultCode == Activity.RESULT_OK) {
                    doCrop();
                    Uri selectedImage = imageUri;
                    getActivity().getContentResolver().notifyChange(selectedImage, null);
                    try {

                        Toast.makeText(getActivity(), selectedImage.toString(),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
                else {
                    mTakePictureView.setClickable(true);
                }
                break;
            }

            case CROP_FROM_CAMERA: {

                if (null != data) {
                    if (data.hasExtra("data")) {
                        Bitmap photo = data.getParcelableExtra("data");

                        mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_PROFILE_PIC
                                , MainActivity.encodeImage(mExpandedImageView.getDrawable()));
                        mTakePictureView.setClickable(true);
                        mProfilePicImageView.setImageBitmap(photo);
                        mExpandedImageView.setImageBitmap(photo);
                        mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_PROFILE_PIC
                        ,MainActivity.encodeImage(mProfilePicImageView.getDrawable()));
                    }

                    File f = new File(imageUri.getPath());

                    if (f.exists()) f.delete();
                }
                else {
                    mTakePictureView.setClickable(true);
                }
                break;
            }
            case PICK_FROM_GALLERY: {
                if(Activity.RESULT_OK == resultCode){
                    Uri selectedImage = imageUri;
                    getActivity().getContentResolver().notifyChange(selectedImage, null);
                    try {

                        Toast.makeText(getActivity(), selectedImage.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e) {
                        Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Gallery", e.toString());
                        mChangePictureView.setClickable(true);

                    }
                    imageUri = data.getData();
                    doCrop();
                }
                else {
                    mChangePictureView.setClickable(true);
                }
                break;
            }
            default: {
                mChangePictureView.setClickable(true);
                break;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mListener = (OnProfileInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListener != null) {
            mListener.onProfileInteraction(mProfile);
            mListener = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mListener != null) {
            mListener.onProfileInteraction(mProfile);
            mListener = null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnProfileInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null) {
            mListener.onProfileInteraction(mProfile);
            mListener = null;
        }
    }


    private void populateProfileFields() {

        mUsernameTextView.setText(mProfile.getValue("username"));
        mEmailTextView.setText(mProfile.getValue("email"));
        mNameTextView.setText(mProfile.getValue("name"));
        mGenderTextView.setText(mProfile.getValue("gender"));
        mBirthdayTextView.setText(mProfile.getValue("birthday"));
        mLanguageTextView.setText(mProfile.getValue("language"));
        mCountryTextView.setText(mProfile.getValue("country"));
        mCertificationTextView.setText(mProfile.getValue("certification"));
        mOrganizationTextView.setText(mProfile.getValue("organization"));
        mAdditionalCertTextView.setText(mProfile.getValue("additionalCert"));
        Drawable image = MainActivity.decodeImage(getResources(),mProfile.getValue("profilePic"));
        Drawable exImage = MainActivity.decodeImage(getResources(),mProfile.getValue("profilePic"));
        mExpandedImageView.setImageDrawable(exImage);
        mProfilePicImageView.setImageDrawable(image);
    }

    private void setImageButtons() {

        mTakePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTakePictureView.setClickable(false);
                takePhoto();
            }
        });

        mChangePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangePictureView.setClickable(false);
                selectPhoto();
            }
        });

        mRevertPictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable default_pic = MainActivity.getDrawable(getResources()
                        , R.drawable.profile);
                if(mProfilePicImageView.getDrawable().equals(default_pic)) {
                    return;
                }
                mRevertPictureView.setClickable(false);
                mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_PROFILE_PIC
                        , MainActivity.encodeImage(default_pic));
                mProfilePicImageView.setImageDrawable(MainActivity.decodeImage(getResources()
                        , mProfile.getValue("profilePic")));
                mExpandedImageView.setImageDrawable(mProfilePicImageView.getDrawable());
                mRevertPictureView.setClickable(true);
            }
        });

    }

    public void selectPhoto() {
        File photo = new File(Environment.getExternalStorageDirectory(), "Profile_pic.jpg");
        Intent intent = new Intent();
        if(19 <= MainActivity.SDK_VERSION) {
            intent = new Intent(Intent.ACTION_PICK
                    , android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            /*intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "content://media/internal/images/media"));*/
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        /*intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);*/
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                imageUri);
        imageUri = Uri.fromFile(photo);
        startActivityForResult(Intent.createChooser(intent,"chooser"), PICK_FROM_GALLERY);

    }

    public void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(), "Profile_pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    private Bitmap loadImage(String imgPath) {
        BitmapFactory.Options options;
        try {
            options = new BitmapFactory.Options();
            options.inSampleSize = 4;// 1/4 of origin image size from width and height
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(getActivity(), "Can not find image crop app", Toast.LENGTH_SHORT)
                    .show();

            return;
        }
        else {
            intent.setData(imageUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 3);
            intent.putExtra("aspectY", 4);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName
                        , res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getActivity().getPackageManager()
                            .getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getActivity().getPackageManager()
                            .getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent.setComponent(
                            new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(
                        getActivity().getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (imageUri != null) {
                            getActivity().getContentResolver().delete(imageUri, null, null);
                            imageUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

    private void setImageAnimationAndDialog() {
        final Animation animShow = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up);
        final Animation animHide = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down);

        mProfilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfilePicImageView.setClickable(false);
                mExpandedImageView.setClickable(true);
                mProfilePicImageView.startAnimation(animHide);
                mProfilePicImageView.setVisibility(View.INVISIBLE);
                mExpandedImageContainer.setVisibility(View.VISIBLE);
                mExpandedImageContainer.startAnimation(animShow);
            }
        });
        mExpandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfilePicImageView.setClickable(true);
                mExpandedImageView.setClickable(false);
                mProfilePicImageView.startAnimation(animShow);
                mProfilePicImageView.setVisibility(View.VISIBLE);
                mExpandedImageContainer.startAnimation(animHide);
                mExpandedImageContainer.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setAdditionalCertDialog() {
        mAdditionalCertTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AdditionalCertPickerFragment();
                newFragment.show(getFragmentManager(), "AdditionalCertPickerFragment");
            }
        });
    }

    private void setOrganizationDialog() {
        mOrganizationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new OrganizationPickerFragment();
                newFragment.show(getFragmentManager(), "organizationPicker");
            }
        });
    }

    private void setCertificationDialog() {
        mCertificationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new CertificationPickerFragment();
                newFragment.show(getFragmentManager(), "certificationPicker");
            }
        });
    }

    private void setCountryDialog() {
        mCountryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new CountryPickerFragment();
                newFragment.show(getFragmentManager(), "countryPicker");
            }
        });
    }

    private void setLanguageDialog() {
        mLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new LanguagePickerFragment();
                newFragment.show(getFragmentManager(), "LanguagePickerFragment");
            }
        });
    }

    private void setBirthdayDialog() {
        mBirthdayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
    }

    private void setGenderDialog() {
        mGenderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new GenderPickerFragment();
                newFragment.show(getFragmentManager(), "genderChange");
            }
        });
    }

    private void setNameDialog() {

        mNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new NameChangeFragment();
                newFragment.show(getFragmentManager(), "nameChange");
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnProfileInteractionListener {
        void onProfileInteraction(UserProfile profile);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_activity_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                // do s.th.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class NameChangeFragment extends DialogFragment {

        protected static AlertDialog mDialog;

        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            final View view = View.inflate(getActivity(), R.layout.name_dialog, null);
            final EditText input = (EditText) view.findViewById(R.id.edit_name);
            final TextView ok = (TextView) view.findViewById(R.id.name_ok);

            input.setText(mNameTextView.getText().toString());
            mNameTextView.setClickable(false);
            dialogBuilder.setView(view);
            ok.setOnClickListener(new TextView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNameTextView.setClickable(true);
                    if (!mNameTextView.getText().toString().equals(input.getText().toString())) {
                        mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_NAME
                                , input.getText().toString());
                        mNameTextView.setText(mProfile.getValue("name"));
                    }
                    mNameTextView.setClickable(true);
                    mDialog.dismiss();
                }
            });
            dialogBuilder.setTitle("Full name");
            mDialog = dialogBuilder.create();
            mDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mNameTextView.setClickable(true);
                        mDialog.dismiss();
                    }
                    return true;
                }
            });
            Toast.makeText(getActivity(), "Enter name"
                    , Toast.LENGTH_SHORT).show();
            return mDialog;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            mNameTextView.setClickable(true);
        }

    }

    public static class GenderPickerFragment extends DialogFragment {

        protected static AlertDialog mDialog;

        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            final View view = View.inflate(getActivity(), R.layout.gender_dialog, null);
            mGenderTextView.setClickable(false);
            final RadioGroup radio = (RadioGroup) view.findViewById(R.id.radio_gender);
            RadioButton button;
            for (int i = 0; i < radio.getChildCount(); i++) {
                button = (RadioButton) radio.getChildAt(i);
                if (mGenderTextView.getText().toString().equals(button.getText().toString())) {
                    button.setChecked(true);
                    break;
                }
            }
            radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton selected = (RadioButton) view
                            .findViewById(radio.getCheckedRadioButtonId());
                    if (!mGenderTextView.getText().toString()
                            .equals(selected.getText().toString())) {
                        mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_GENDER
                                , selected.getText().toString());
                        mGenderTextView.setText(mProfile.getValue("gender"));
                    }
                    mGenderTextView.setClickable(true);
                    mDialog.dismiss();
                }
            });
            dialogBuilder.setView(view);
            dialogBuilder.setTitle("Gender");
            mDialog = dialogBuilder.create();
            mDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mGenderTextView.setClickable(true);
                        mDialog.dismiss();
                    }
                    return true;
                }
            });
            Toast.makeText(getActivity(), "Select gender"
                    , Toast.LENGTH_SHORT).show();
            return mDialog;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            mGenderTextView.setClickable(true);
        }
    }

    public static class CountryPickerFragment extends DialogFragment {

        String[] mCountryList;
        protected static AlertDialog mDialog;

        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mCountryList = getResources().getStringArray(R.array.string_array_countries);
            int selection = 0;
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            String currentCountry = mCountryTextView.getText().toString();
            mCountryTextView.setClickable(false);
            for (int i = 0; i < mCountryList.length; i++) {
                if (mCountryList[i].equals(currentCountry)) {
                    selection = i;
                    break;
                }
            }
            dialogBuilder.setTitle("Country");
            dialogBuilder.setSingleChoiceItems(mCountryList, selection
                    , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!mCountryList[which].equals(mCountryTextView.getText().toString())) {
                        mCountryTextView.setText(mCountryList[which]);
                        mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_COUNTRY
                                , mCountryTextView.getText().toString());
                    }
                    mCountryTextView.setClickable(true);
                    mDialog.dismiss();
                }
            });
            mDialog = dialogBuilder.create();
            mDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mCountryTextView.setClickable(true);
                        mDialog.dismiss();
                    }
                    return true;
                }
            });
            Toast.makeText(getActivity(), "Select country"
                    , Toast.LENGTH_SHORT).show();
            return mDialog;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            mCountryTextView.setClickable(true);
        }
    }

    public static class LanguagePickerFragment extends DialogFragment {

        String[] mLanguageList;
        protected boolean[] mSelections;
        protected int mCheckedCount = 0;
        protected static AlertDialog mDialog;

        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mLanguageList = getResources().getStringArray(R.array.string_array_languages);
            mSelections = new boolean[mLanguageList.length];
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            String[] currentLang = mLanguageTextView.getText().toString().split(", ");

            for (int i = 0; i < mLanguageList.length; i++) {
                for (String lang : currentLang) {
                    if (mLanguageList[i].equals(lang)) {
                        mSelections[i] = true;
                        mCheckedCount++;
                    }
                }
            }
            dialogBuilder.setTitle("Language");
            dialogBuilder.setMultiChoiceItems(mLanguageList, mSelections
                    , new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    mLanguageTextView.setClickable(false);
                    if (isChecked) {
                        mCheckedCount++;
                    } else if (!isChecked) {
                        mCheckedCount--;
                    }
                    if (mCheckedCount < 1) {
                        mCheckedCount++;
                        Toast.makeText(getActivity(), "Select at least 1 language"
                                , Toast.LENGTH_SHORT).show();
                        mDialog.getListView().setItemChecked(which, true);
                        mSelections[which] = true;
                    }
                    if (mCheckedCount >= 4) { // it will allow 3 checkboxes only
                        mCheckedCount--;
                        Toast.makeText(getActivity(), "Select up to 3 languages"
                                , Toast.LENGTH_SHORT).show();
                        mDialog.getListView().setItemChecked(which, false);
                        mSelections[which] = false;

                    }
                    //else {
                    //    c.setSelected(isChecked);
                    //}
                }
            });
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            String languages = "";
                            for (int i = 0; i < mSelections.length; i++) {
                                if (mSelections[i]) {
                                    languages = languages.concat(mLanguageList[i]);
                                    languages = languages.concat(", ");
                                }
                            }
                            mLanguageTextView
                                    .setText(languages.substring(0, languages.length() - 2));
                            mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_LANGUAGE
                                    , mLanguageTextView.getText().toString());
                            mLanguageTextView.setClickable(true);
                            dialog.dismiss();
                            break;
                    }
                }
            });
            mDialog = dialogBuilder.create();
            mDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mLanguageTextView.setClickable(true);
                        mDialog.dismiss();
                    }
                    return true;
                }
            });
            Toast.makeText(getActivity(), "Select up to 3 languages"
                    , Toast.LENGTH_SHORT).show();
            return mDialog;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            mLanguageTextView.setClickable(true);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mBirthdayTextView.setClickable(false);

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int minYear = year - 100;
            int maxYear = year - 10;

            if (!mBirthdayTextView.getText().toString().equals(getString(R.string.not_specified))) {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }

            final DatePickerDialog datePicker;

            if (MainActivity.FragmentsEnum.PROFILE != MainActivity.mWorkingFrag) {
                datePicker = new
                        DatePickerDialog(getActivity(), (MainActivity) getActivity()
                        , year, month, day);
            } else {
                datePicker = new
                        DatePickerDialog(getActivity(), this, year, month, day);
            }

            c.set(maxYear, month, day);
            datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
            c.set(minYear, month, day);
            datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
            datePicker.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mBirthdayTextView.setClickable(true);
                        datePicker.dismiss();
                    }
                    return true;
                }
            });
            // Create a new instance of DatePickerDialog and return it
            return datePicker;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            mBirthdayTextView.setClickable(true);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            final Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
            Date date = c.getTime();

            mBirthdayTextView.setClickable(true);
            mBirthdayTextView.setText(myFormat.format(date));
            mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_BIRTHDAY
                    , mBirthdayTextView.getText().toString());
        }
    }

    public static class CertificationPickerFragment extends DialogFragment {

        String[] mCertificationList;
        protected static AlertDialog mDialog;

        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mCertificationTextView.setClickable(false);
            mCertificationList = getResources().getStringArray(R.array.string_array_scuba_cert);
            int selection = 0;
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            String currentCert = mCertificationTextView.getText().toString();

            for (int i = 0; i < mCertificationList.length; i++) {
                if (mCertificationList[i].equals(currentCert)) {
                    selection = i;
                }
            }
            dialogBuilder.setTitle("Diving Certification");
            dialogBuilder.setSingleChoiceItems(mCertificationList, selection
                    , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!mCertificationList[which]
                            .equals(mCertificationTextView.getText().toString())) {
                        mCertificationTextView.setText(mCertificationList[which]);
                        mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_CERTIFICATION
                                , mCertificationTextView.getText().toString());
                    }
                    mCertificationTextView.setClickable(true);
                    mDialog.dismiss();
                }
            });
            mDialog = dialogBuilder.create();
            mDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mCertificationTextView.setClickable(true);
                        mDialog.dismiss();
                    }
                    return true;
                }
            });
            Toast.makeText(getActivity(), "Select diver certification"
                    , Toast.LENGTH_SHORT).show();
            return mDialog;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            mCertificationTextView.setClickable(true);
        }
    }

    public static class OrganizationPickerFragment extends DialogFragment {

        String[] mOrganizationList;
        protected static AlertDialog mDialog;

        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mOrganizationTextView.setClickable(false);
            mOrganizationList = getResources().getStringArray(R.array.string_array_scuba_orgs);
            int selection = 0;
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            String currentOrg = mOrganizationTextView.getText().toString();

            for (int i = 0; i < mOrganizationList.length; i++) {
                if (mOrganizationList[i].equals(currentOrg)) {
                    selection = i;
                }
            }
            dialogBuilder.setTitle("Diving Organization");
            dialogBuilder.setSingleChoiceItems(mOrganizationList, selection
                    , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!mOrganizationList[which]
                            .equals(mOrganizationTextView.getText().toString())) {
                        mOrganizationTextView.setText(mOrganizationList[which]);
                        mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_ORGANIZATION
                                , mOrganizationTextView.getText().toString());
                    }
                    mOrganizationTextView.setClickable(true);
                    mDialog.dismiss();
                }
            });
            mDialog = dialogBuilder.create();
            mDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mOrganizationTextView.setClickable(true);
                        mDialog.dismiss();
                    }
                    return true;
                }
            });
            Toast.makeText(getActivity(), "Select diving organization"
                    , Toast.LENGTH_SHORT).show();
            return mDialog;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            mOrganizationTextView.setClickable(true);
        }
    }

    public static class AdditionalCertPickerFragment extends DialogFragment {

        String[] mAddCertList;
        protected boolean[] mSelections;
        protected int mCheckedCount = 0;
        protected static AlertDialog mDialog;

        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mAdditionalCertTextView.setClickable(false);
            mAddCertList = getResources().getStringArray(R.array.string_array_scuba_add_cert);
            mSelections = new boolean[mAddCertList.length];
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            String[] currentAddCert = mAdditionalCertTextView.getText().toString().split("\n");

            for (int i = 0; i < mAddCertList.length; i++) {
                for (String cert : currentAddCert) {
                    if (mAddCertList[i].equals(cert)) {
                        mSelections[i] = true;
                    }
                }
            }
            dialogBuilder.setTitle("Additional Certifications");
            dialogBuilder.setMultiChoiceItems(mAddCertList, mSelections
                    , new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                }
            });
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            String addCert = "";
                            for (int i = 0; i < mSelections.length; i++) {
                                if (mSelections[i]) {
                                    mCheckedCount++;
                                    addCert = addCert.concat(mAddCertList[i]);
                                    addCert = addCert.concat("\n");
                                }
                            }
                            if(1 > mCheckedCount) {
                                addCert = getString(R.string.not_specified);
                            }
                            mAdditionalCertTextView
                                    .setText(addCert.substring(0, addCert.length()));
                            mProfile.setValue(ParseDbHelper.ProfileEntry.COLUMN_NAME_ADD_CERT
                                    , mAdditionalCertTextView.getText().toString());
                            mAdditionalCertTextView.setClickable(true);
                            dialog.dismiss();
                            break;
                    }
                }
            });
            mDialog = dialogBuilder.create();
            mDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mAdditionalCertTextView.setClickable(true);
                        mDialog.dismiss();
                    }
                    return true;
                }
            });
            Toast.makeText(getActivity(), "Select additional certifications"
                    , Toast.LENGTH_SHORT).show();
            return mDialog;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            mAdditionalCertTextView.setClickable(true);
        }

    }
}
