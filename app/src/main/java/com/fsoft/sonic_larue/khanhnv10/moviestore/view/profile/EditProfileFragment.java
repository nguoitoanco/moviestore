package com.fsoft.sonic_larue.khanhnv10.moviestore.view.profile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ProfileDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Profile;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.CircleImageView;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.DateUtil;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.ImageUtil;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.MovieDatePicker;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.StringUtil;

import static com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ProfileDao.*;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.model.Profile.*;

public class EditProfileFragment extends Fragment {
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 1000;

    private Profile profile;
    private int mergeType;

    private CircleImageView proFileIcon;
    private EditText nameInput;
    private EditText emailInput;
    private TextView birthDay;


    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mergeType = getArguments().getInt("mergeType", 0);

        proFileIcon  = (CircleImageView) view.findViewById(R.id.profile_icon);

        nameInput  = (EditText) view.findViewById(R.id.name_input);
        emailInput  = (EditText) view.findViewById(R.id.email_input);
        birthDay  = (TextView) view.findViewById(R.id.birth_day);

        RadioGroup gender = (RadioGroup) view.findViewById(R.id.gender);

        profile = (Profile) getArguments().getSerializable("profile");
        if (profile != null) {
            Bitmap bitmap = ImageUtil.getBitmapFromPath(profile.getUserAvatar());
            if (bitmap != null) {
                ImageUtil.setAutoSizeOfImageView(proFileIcon, getContext(),
                        ImageUtil.RATE_TWO);
                proFileIcon.setImageBitmap(bitmap);
            }

            nameInput.setText(profile.getUserName());
            emailInput.setText(profile.getUserEmail());
            birthDay.setText(profile.getUserBirthday());
            if (profile.getUserGender() == Gender.MALE.getIndex()) {
                gender.check(R.id.male);
                profile.setUserGender(Gender.MALE.getIndex());
            } else {
                gender.check(R.id.female);
                profile.setUserGender(Gender.FEMALE.getIndex());
            }

        } else {
            profile = new Profile();
        }

        radioGroupOnclickListener(gender);
        setupBirthDay();
        btnDoneOnclickListener(view);
        btnCancelOnclickListener(view);
        changeProfileIconListener();

        return view;
    }

    private void radioGroupOnclickListener(final RadioGroup gender) {
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                profile.setUserGender(index);
            }
        });
    }



    private void changeProfileIconListener() {
        proFileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void btnDoneOnclickListener(View view) {
        Button btnDone = (Button) view.findViewById(R.id.btn_edit_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profile.setUserName(nameInput.getText().toString());
                profile.setUserEmail(emailInput.getText().toString());
                if (!StringUtil.isEmpty(nameInput.getText().toString())
                        && !StringUtil.isEmpty(emailInput.getText().toString())) {
                    ProfileDao profileDao = new ProfileDao(getContext(), profile.getUserId());
                    if (profileDao.merge(profile, mergeType) != -1) {
                        // Send broad cast to update favourite ui
//                        createIntentBroadcast();
                        if (mergeType == MergeType.UPDATE.getType()) {
                            Intent intent = new Intent("UpdateProfile");
                            intent.putExtra("profile", profile);
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent("AddProfile");
                            intent.putExtra("profile", profile);
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                        }

                        SharedPreferencesManager.savePreference(getContext(), "profileId", profile.getUserId());
                        getFragmentManager().popBackStack();
                    } else {
                        showDialogError();
                    }
                } else {
                    showDialogError();
                }
            }
        });
    }

    private void btnCancelOnclickListener(View view) {
        Button btnCancel = (Button) view.findViewById(R.id.btn_edit_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    public void setupBirthDay() {
        final MovieDatePicker movieDatePicker = new MovieDatePicker();
        birthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieDatePicker.show(getFragmentManager(), getString(R.string.start_datepicker_title));
                movieDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                        String strDate = DateUtil.getStrDate(year, monthOfYear, dayOfMonth);
                        profile.setUserBirthday(strDate);
                        birthDay.setText(strDate);
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                String destination = ImageUtil.saveBitmap(thumbnail);

                profile.setUserAvatar(destination);
                proFileIcon.setImageBitmap(thumbnail);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String imagePath = ImageUtil.getImagePathFromUri(selectedImageUri, getContext());
                Bitmap bitmap = ImageUtil.getBitmapFromPath(imagePath);
                profile.setUserAvatar(imagePath);
                proFileIcon.setImageBitmap(bitmap);
            }
    }}


    private void selectImage() {
        final CharSequence[] items = { getString(R.string.camera_option), getString(R.string.gallery_option) };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(items[0].toString())) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST);
                } else if (items[item].equals(items[1])) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void showDialogError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Error");
        builder.setNegativeButton(R.string.do_cancel, null);
        builder.setMessage("Input invalid data!");
        builder.create().show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
