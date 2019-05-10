package com.avengers.businesscardapp.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.avengers.businesscardapp.R;
import com.avengers.businesscardapp.util.NetworkHelper;
import com.avengers.businesscardapp.webservice.BusinessCardWebservice;
import com.avengers.businesscardapp.webservice.GenericResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;

/**
 * AddCard fragment.
 */
public class AddCardFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private final String ARG_PARAM2 = "param2";
    private final int CAMERA_REQUEST = 2;
    private final int PHOTOS_REQUEST = 4;

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_GET_SINGLE_FILE = 3;
    private final String TAG = "CardFragment";

    private Button cameraButton, btnPhotos;
    private ImageView cardImage;

    private String cardFilePath;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddCardFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddCardFragment newInstance(String param1) {
        AddCardFragment fragment = new AddCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    handleCameraResponse(data);
                    break;
                case REQUEST_GET_SINGLE_FILE:
                    handlePhotosResponse(data);
                    break;
            }
        }
    }

    private void handlePhotosResponse(Intent data) {
        Uri selectedImageUri = data.getData();
        // Get the path from the Uri
        final String path = getPathFromPhotoURL(selectedImageUri);
        if (path != null) {
            File f = new File(path);
            selectedImageUri = Uri.fromFile(f);
        }
        // Set the image in ImageView
        cardImage.setImageURI(selectedImageUri);
    }

    private String getPathFromPhotoURL(Uri selectedImageUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        if (getActivity() != null) {
            Cursor cursor = getActivity().
                    getContentResolver().
                    query(selectedImageUri, proj, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    res = cursor.getString(column_index);
                }
                cursor.close();
            }
        }
        return res;
    }

    private void handleCameraResponse(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        cardImage.setImageBitmap(photo);
        cameraButton.setVisibility(Button.VISIBLE);


        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(getActivity().getApplicationContext(), photo);

        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));
        cardFilePath = getRealPathFromURI(tempUri);
        System.out.println("ffffff" + getRealPathFromURI(tempUri));
        // System.out.println(mImageCaptureUri);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getActivity().getContentResolver() != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_card, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                new UploadCardTask(getActivity().getApplicationContext()).execute();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraButton = view.findViewById(R.id.btn_camera);
        btnPhotos = view.findViewById(R.id.btn_photos);
        cardImage = view.findViewById(R.id.img_camera);

        cameraButton.setOnClickListener(this);
        btnPhotos.setOnClickListener(this);
    }

    private void openPhotos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                REQUEST_GET_SINGLE_FILE);
    }

    private void showMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                handleCameraClick();
                break;
            case R.id.btn_photos:
                handlePhotosClick();
                openPhotos();
                break;
        }
    }

    private void handlePhotosClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PHOTOS_REQUEST);
            } else {
                openPhotos();
            }
        } else {
            openPhotos();
        }
    }

    private void handleCameraClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED ||
                    getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, CAMERA_REQUEST);
            } else {
                openCamera();
                        /*if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }*/
            }
        } else {
            //system os is lower version
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    showMsg(getString(R.string.txt_denied_camera));
                }
                break;
            case PHOTOS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    showMsg(getString(R.string.txt_denied_photos));
                }
                break;
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class UploadCardTask extends AsyncTask<Void, GenericResponse, GenericResponse> {

        private Context mContext;

        public UploadCardTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected GenericResponse doInBackground(Void... voids) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String emailId = sharedPrefs.getString("Email_Id", "");
            //Create a file object using file path
            File file = new File(cardFilePath);
            // Create a request body with file and image media type
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file",
                    file.getName(),
                    RequestBody.create(MediaType.parse("image/*"),
                            file));
            //RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), emailId);

            if (NetworkHelper.hasNetworkAccess(mContext)) {
                BusinessCardWebservice webservice = BusinessCardWebservice
                        .retrofit.create(BusinessCardWebservice.class);
                Call<GenericResponse> call = webservice.uploadCard(filePart, email);
                try {
                    GenericResponse response = call.execute().body();
                    return response;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "handleActionRequestQuestion: " + e.getMessage());
                    return null;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(GenericResponse response) {
            super.onPostExecute(response);
            if (response != null && response.getMessage() != null) {
                showMsg(response.getMessage());
            }
        }
    }
}
