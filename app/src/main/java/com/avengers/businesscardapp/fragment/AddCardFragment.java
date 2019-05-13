package com.avengers.businesscardapp.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import com.avengers.businesscardapp.EditCardActivity;
import com.avengers.businesscardapp.R;
import com.avengers.businesscardapp.dto.UploadCardResponse;
import com.avengers.businesscardapp.util.Constants;
import com.avengers.businesscardapp.util.NetworkHelper;
import com.avengers.businesscardapp.webservice.BusinessCardWebservice;

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

    private static final String ARG_EMAIL_ID = "args_emailId";

    private final int CAMERA_REQUEST = 2;
    private final int PHOTOS_REQUEST = 4;

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_BROWSE_IMAGE = 3;
    private final String TAG = "AddCardFragment";

    private Button cameraButton, btnPhotos;
    private ImageView cardImage;

    private String cardFilePath;
    private String emailId;
    private Uri cardImageUri;

    public AddCardFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddCardFragment newInstance(String emailId) {
        AddCardFragment fragment = new AddCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL_ID, emailId);
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
                case REQUEST_BROWSE_IMAGE:
                    handlePhotosResponse(data);
                    break;
            }
        }
    }

    private void handleCameraResponse(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        cardImage.setImageBitmap(photo);
        cameraButton.setVisibility(Button.VISIBLE);

        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
//        Uri tempUri = getImageUri(getActivity().getApplicationContext(), photo);
        cardImageUri = getImageUri(getActivity().getApplicationContext(), photo);

        // CALL THIS METHOD TO GET THE ACTUAL PATH
        cardFilePath = getRealPathFromURI(cardImageUri);
        Log.d(TAG, "handleCameraResponse: " + getRealPathFromURI(cardImageUri));
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.
                insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getActivity() != null) {
            if (getActivity().getContentResolver() != null) {
                Cursor cursor = getActivity()
                        .getContentResolver()
                        .query(uri, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cursor.getString(idx);
                    cursor.close();
                }
            }
        }
        return path;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            emailId = getArguments().getString(ARG_EMAIL_ID);
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
                new UploadCardTask(getActivity()).execute();
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

    private void handlePhotosResponse(Intent data) {
        cardImageUri = data.getData();
        cardImage.setImageURI(cardImageUri);
        cardFilePath = getPathFromPhotoURL(cardImageUri);
//        cardFilePath = getRealPathFromURI_API19(getActivity(), cardImageUri);
        cardImage.setImageBitmap(BitmapFactory.decodeFile(cardFilePath));
    }

    private String getPathFromPhotoURL(Uri selectedImageUri) {
        String result = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        if (getActivity() != null) {
            Cursor cursor = getActivity().
                    getContentResolver().
                    query(selectedImageUri, filePathColumn,
                            null, null, null);
//                    query(selectedImageUri, proj, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int column_index = cursor.getColumnIndex(filePathColumn[0]);
                result = cursor.getString(column_index);
                cursor.close();
            } else {
                result = selectedImageUri.getPath();
            }
        }
        Log.d(TAG, "getPathFromPhotoURL: " + result);
        return result;
    }

//    public static String getRealPathFromURI_API19(Context context, Uri uri) {
//        String filePath = "";
//        String wholeID = DocumentsContract.getDocumentId(uri);
//
//        // Split at colon, use second item in the array
//        String id = wholeID.split(":")[1];
//
//        String[] column = {MediaStore.Images.Media.DATA};
//
//        // where id is equal to
//        String sel = MediaStore.Images.Media._ID + "=?";
//
//        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                column, sel, new String[]{id}, null);
//
//        int columnIndex = cursor.getColumnIndex(column[0]);
//
//        if (cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        } else {
//            filePath = uri.getPath();
//        }
//        cursor.close();
//        return filePath;
//    }

    private void openPhotos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                REQUEST_BROWSE_IMAGE);
    }

    private void showMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private class UploadCardTask extends AsyncTask<Void, UploadCardResponse, UploadCardResponse> {

        private Context mContext;

        public UploadCardTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected UploadCardResponse doInBackground(Void... voids) {

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
                Call<UploadCardResponse> call = webservice.uploadCard(filePart, email);
                try {
                    UploadCardResponse response = call.execute().body();
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
        protected void onPostExecute(UploadCardResponse response) {
            super.onPostExecute(response);
            if (response != null) {
                Intent editIntent = new Intent(getActivity(), EditCardActivity.class);
                editIntent.putExtra(Constants.EXTRA_CARD_DETAIL, response);
                editIntent.putExtra(Constants.EXTRA_IMG_URI, cardImageUri);
                startActivity(editIntent);
                showMsg(response.toString());
            }
        }
    }
}
