package com.example.nozery.diveapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.app.Fragment;
import android.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnProfileInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "username";
    private static final String ARG_PARAM2 = "email";
    private static final String ARG_PARAM3 = "name";
    private static final String ARG_PARAM4 = "gender";
    private static final String ARG_PARAM5 = "birthday";
    private static final String ARG_PARAM6 = "language";
    private static final String ARG_PARAM7 = "location";

    // TODO: Rename and change types of parameters
    protected static UserProfile mProfile;

    protected Dialog mMultiDialog;

    private TextView mUsernameTextView;
    private TextView mEmailTextView;
    protected static TextView mNameTextView;
    protected static TextView mGenderTextView;

    protected static TextView mBirthdayTextView;
    protected static TextView mLanguageTextView;
    protected static View mGenderDialog;
    protected static View mNameDialog;
    protected static TextView mCountryTextView;
    protected static TextView mCertificationTextView;
    protected static TextView mOrganizationTextView;
    protected static TextView mAdditionalCertTextView;
    protected static ImageView mProfilePicImageView;
    protected static ImageView mExpandedImageView;
    private OnProfileInteractionListener mListener;




    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    float mStartScaleFinal;
    final Rect startBounds = new Rect();
    final Rect finalBounds = new Rect();


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param data Parameter 1.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    //TODO: put image
    public static ProfileFragment newInstance(HashMap<String, String> data) {

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("data",data);
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

            HashMap<String, String> data = (HashMap<String, String>)getArguments()
                                                                .getSerializable("data");
            mProfile = new UserProfile(data);

        }

        mMultiDialog = new Dialog(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Profile");
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final View nameView = inflater.inflate(R.layout.name_dialog, container, false);
        final View genderView = inflater.inflate(R.layout.gender_dialog, container, false);

        mGenderDialog = genderView;
        //mNameDialog = nameView;

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




        mProfilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(mProfilePicImageView, R.drawable.profile);
            }
        });

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        mExpandedImageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }
                final RelativeLayout image_container = (RelativeLayout) getActivity()
                        .findViewById(R.id.expanded_image_container);
                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(image_container, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(image_container,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(image_container,
                                        View.SCALE_X, mStartScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(image_container,
                                        View.SCALE_Y, mStartScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProfilePicImageView.setAlpha(1f);

                        //profileScrollView.setVisibility(View.VISIBLE);
                        image_container.setVisibility(View.GONE);

                        //expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mProfilePicImageView.setAlpha(1f);

                        //profileScrollView.setVisibility(View.VISIBLE);
                        image_container.setVisibility(View.GONE);

                        //expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);








        populateProfileFields();

        setNameDialog();
        setGenderDialog(mGenderDialog);
        setBirthdayDialog();
        setLanguageDialog();
        setCountryDialog();
        setCertificationDialog();
        setOrganizationDialog();
        setAdditionalCertDialog();

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return view;
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
        mProfilePicImageView.setImageDrawable(MainActivity.decodeImage(getResources()
                , mProfile.getValue("profilePic")));
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

    private void setGenderDialog(final View genderView) {
        final RadioGroup radio = (RadioGroup) genderView.findViewById(R.id.radio_gender);
        RadioButton button;
        for(int i=0; i<radio.getChildCount(); i++) {
            button = (RadioButton) radio.getChildAt(i);
            if (mGenderTextView.getText().toString().equals(button.getText().toString())) {
                button.setChecked(true);
                break;
            }
        }
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selected = (RadioButton) genderView
                        .findViewById(radio.getCheckedRadioButtonId());
                if(!mGenderTextView.getText().toString()
                        .equals(selected.getText().toString())) {
                    mProfile.setValue("gender",selected.getText().toString());
                    mGenderTextView.setText(mProfile.getValue("gender"));
                }
                mMultiDialog.dismiss();
            }
        });
        mGenderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMultiDialog.setTitle("Gender");
                mMultiDialog.setContentView(genderView);
                mMultiDialog.show();
            }
        });

    }

    private void setNameDialog() {

        mNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new NameChangeFragment();
                newFragment.show(getFragmentManager(), "nameChanges");
            }
        });
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
        mListener = null;
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
        // TODO Auto-generated method stub
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
            final View view = View.inflate(getActivity(),R.layout.name_dialog, null);
            final EditText input = (EditText)view.findViewById(R.id.edit_name);
            final TextView ok = (TextView) view.findViewById(R.id.name_ok);

            input.setText(mNameTextView.getText().toString());
            mNameTextView.setClickable(false);
            dialogBuilder.setView(view);
            ok.setOnClickListener(new TextView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNameTextView.setClickable(true);
                    if (!mNameTextView.getText().toString().equals(input.getText().toString())) {
                        mProfile.setValue("name", input.getText().toString());
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
                    // TODO Auto-generated method stub
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
            for(int i=0; i<mCountryList.length; i++) {
                if(mCountryList[i].equals(currentCountry)) {
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
                        mProfile.setValue("country", mCountryTextView.getText().toString());
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
                    // TODO Auto-generated method stub
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
    }
    
    public static class LanguagePickerFragment extends DialogFragment {

        String[] mLanguageList;
        protected boolean[] mSelections;
        protected static int mCheckedCount =0;
        protected static AlertDialog mDialog;

        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mLanguageList = getResources().getStringArray(R.array.string_array_languages);
            mSelections =  new boolean[ mLanguageList.length ];
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            String[] currentLang = mLanguageTextView.getText().toString().split(", ");

            for(int i=0; i<mLanguageList.length; i++) {
                for (String lang : currentLang) {
                    if(mLanguageList[i].equals(lang)) {
                        mSelections[i] = true;
                        mCheckedCount++;
                    }
                }
            }
            dialogBuilder.setTitle("Language");
            //dialogBuilder.setMultiChoiceItems(mLanguageList, mSelections, this);
            dialogBuilder.setMultiChoiceItems(mLanguageList, mSelections, new DialogInterface.OnMultiChoiceClickListener() {
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
                            mProfile.setValue("language", mLanguageTextView.getText().toString());
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
                    // TODO Auto-generated method stub
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
            int minYear = year-100;
            int maxYear = year-10;

            if(!mBirthdayTextView.getText().toString().equals(getString(R.string.not_specified))){
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }

            final DatePickerDialog datePicker;

            if(MainActivity.FragmentsEnum.PROFILE != MainActivity.mWorkingFrag) {
                datePicker = new
                        DatePickerDialog(getActivity(),(MainActivity)getActivity()
                        , year, month, day);
            }
            else {
                datePicker = new
                        DatePickerDialog(getActivity(), this, year, month, day);
            }

            c.set(maxYear, month,day);
            datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
            c.set(minYear, month, day);
            datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
            datePicker.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            final Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
            Date date = c.getTime();

            mBirthdayTextView.setClickable(true);
            mBirthdayTextView.setText(myFormat.format(date));
            mProfile.setValue("birthday", mBirthdayTextView.getText().toString());
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

            for(int i=0; i< mCertificationList.length; i++) {
                if(mCertificationList[i].equals(currentCert)) {
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
                        mProfile.setValue("certification"
                                , mCertificationTextView.getText().toString());
                    }
                    mCertificationTextView.setClickable(true);
                    mDialog.dismiss();
                }
            })  ;
            mDialog = dialogBuilder.create();
            mDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
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

            for(int i=0; i< mOrganizationList.length; i++) {
                if(mOrganizationList[i].equals(currentOrg)) {
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
                        mProfile.setValue("organization"
                                , mOrganizationTextView.getText().toString());
                    }
                    mOrganizationTextView.setClickable(true);
                    mDialog.dismiss();
                }
            })  ;
            mDialog = dialogBuilder.create();
            mDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
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
    }

    public static class AdditionalCertPickerFragment extends DialogFragment {

        String[] mAddCertList;
        protected boolean[] mSelections;
        protected static int mCheckedCount =0;
        protected static AlertDialog mDialog;

        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mAdditionalCertTextView.setClickable(false);
            mAddCertList = getResources().getStringArray(R.array.string_array_scuba_add_cert);
            mSelections =  new boolean[ mAddCertList.length ];
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            String[] currentAddCert = mAdditionalCertTextView.getText().toString().split("\n");

            for(int i=0; i<mAddCertList.length; i++) {
                for (String cert: currentAddCert) {
                    if(mAddCertList[i].equals(cert)) {
                        mSelections[i] = true;
                        mCheckedCount++;
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
                                    addCert = addCert.concat(mAddCertList[i]);
                                    addCert = addCert.concat("\n");
                                }
                            }
                            // if(1 < mAdditionalCertTextView.getText().toString().length()) {

                            // }
                            mAdditionalCertTextView
                                    .setText(addCert.substring(0, addCert.length()));
                            mProfile.setValue("additionalCert"
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
                    // TODO Auto-generated method stub
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
    }




    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final RelativeLayout image_container = (RelativeLayout) getActivity()
                .findViewById(R.id.expanded_image_container);
        mExpandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.

        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        getActivity().findViewById(R.id.expanded_image)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);

        //profileScrollView.setVisibility(View.GONE);
        image_container.setVisibility(View.VISIBLE);

        //expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        image_container.setPivotX(0f);
        image_container.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(image_container, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(image_container, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(image_container, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(image_container,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
        mStartScaleFinal = startScale;

    }




}
