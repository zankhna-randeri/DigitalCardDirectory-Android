package com.avengers.businesscardapp.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.avengers.businesscardapp.EditCardActivity;
import com.avengers.businesscardapp.R;
import com.avengers.businesscardapp.dto.UploadCardResponse;
import com.avengers.businesscardapp.util.Constants;
import com.avengers.businesscardapp.util.NetworkHelper;
import com.avengers.businesscardapp.util.Utility;
import com.avengers.businesscardapp.webservice.BusinessCardWebservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;

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

    private ImageView cardImage;
    private LinearLayout progress;
    private TextView txtProgressMsg;

    private String cardFilePath;
    private String appUserEmail;
    private Uri cardImageUri;
    private String mCurrentPhotoPath;

    private TransferUtility transferUtility;

    public AddCardFragment() {
        // Required empty public constructor
    }

    public static AddCardFragment newInstance(String emailId) {
        AddCardFragment fragment = new AddCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL_ID, emailId);
        fragment.setArguments(args);
        return fragment;
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
        Button cameraButton = view.findViewById(R.id.btn_camera);
        Button btnPhotos = view.findViewById(R.id.btn_photos);
        cardImage = view.findViewById(R.id.img_camera);
        progress = view.findViewById(R.id.lyt_progress);
        txtProgressMsg = progress.findViewById(R.id.txt_progress_msg);

        cameraButton.setOnClickListener(this);
        btnPhotos.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    handleCameraResponse();
                    break;
                case REQUEST_BROWSE_IMAGE:
                    handlePhotosResponse(data);
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appUserEmail = getArguments().getString(ARG_EMAIL_ID);
        }
        setHasOptionsMenu(true);
        createTransferUtility();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_card, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_done) {
            String fileName = getFileNameFromUri(cardImageUri);
            Log.d(TAG, "onOptionsItemSelected : objectKey ---- " + fileName);
            File file;
            try {
                file = createFileFromUri(cardImageUri, fileName);
                String objectKey = appUserEmail + "/" + fileName;
                upload(file, objectKey);

                progress.setVisibility(View.VISIBLE);
                txtProgressMsg.setText(getString(R.string.txt_upload_progress));

            } catch (IOException e) {
                Log.e(TAG, "onOptionsItemSelected: ", e);
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
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

    private String getFileNameFromUri(Uri uri) {
        if (getActivity() != null) {
            Cursor returnCursor = getActivity()
                    .getContentResolver()
                    .query(uri, null, null, null, null);
            int nameIndex;
            if (returnCursor != null) {
                nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                String name = returnCursor.getString(nameIndex);
                returnCursor.close();
                return name;
            }
        }
        return "";
    }

    private File createFileFromUri(Uri uri, String objectKey) throws IOException {
        File file = null;
        if (getActivity() != null) {
            InputStream is = getActivity().getContentResolver().openInputStream(uri);
            file = new File(getActivity().getApplicationContext().getCacheDir(), objectKey);
            if (file.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[2046];
                int read;
                while (is != null && (read = is.read(buf)) != -1) {
                    fos.write(buf, 0, read);
                }
                fos.flush();
                fos.close();
            }
        }
        return file;
    }

    private void createTransferUtility() {
        if (getActivity() != null) {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getActivity().getApplicationContext(),
                    Constants.COGNITO_POOL_ID,
                    Regions.US_WEST_2
            );
            AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
            transferUtility = new TransferUtility(s3Client, getActivity().getApplicationContext());
        }
    }

    private void upload(final File file, final String objectKey) {
        TransferObserver transferObserver = transferUtility.upload(
                Constants.BUCKET_NAME,
                objectKey,
                file
        );
        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + state);
                if (TransferState.COMPLETED.equals(state)) {
//                    progress.setVisibility(View.GONE);
//                    Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
                    new UploadCardTask(getActivity(), file.getName()).execute();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, "onError: ", ex);
                progress.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void handleCameraResponse() {
        File file = new File(mCurrentPhotoPath);
        Bitmap photo = null;
        try {
            photo = getActivity() != null ?
                    MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file)) :
                    null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photo != null) {
            cardImage.setImageBitmap(photo);
        }
//        Bitmap photo = (Bitmap) data.getExtras().get("data");
//        cardImage.setImageBitmap(photo);
//        Uri tempUri = getImageUri(getActivity().getApplicationContext(), photo);
        cardImageUri = getImageUri(getActivity().getApplicationContext(), photo);
        cardImage.setImageURI(cardImageUri);
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

    private void handlePhotosClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity() != null) {
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PHOTOS_REQUEST);
                } else {
                    openPhotos();
                }
            }
        } else {
            openPhotos();
        }
    }

    private void handleCameraClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity() != null) {
                if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED ||
                        getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, CAMERA_REQUEST);
                } else {
                    openCamera();
                }
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
                    Utility.getInstance().showMsg(getActivity().getApplicationContext(),
                            getString(R.string.txt_denied_camera));
                }
                break;
            case PHOTOS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Utility.getInstance().showMsg(getActivity().getApplicationContext(),
                            getString(R.string.txt_denied_photos));
                }
                break;
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getActivity() != null) {
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.avengers.businesscardapp.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        } else {
            timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
        }
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getApplicationContext()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "createImageFile : -- mCurrentPhotoPath : " + mCurrentPhotoPath);
        return image;
    }

    private void handlePhotosResponse(Intent data) {
        cardImageUri = data.getData();
        cardImage.setImageURI(cardImageUri);
//        cardFilePath = getPathFromPhotoURL(cardImageUri);
        Log.d(TAG, "handlePhotosResponse: cardImageUri ------ " + cardImageUri);
        cardFilePath = getRealPathFromURI_API19(getActivity(), cardImageUri);
//        cardImage.setImageBitmap(BitmapFactory.decodeFile(cardFilePath));
    }

//    private String getPathFromPhotoURL(Uri selectedImageUri) {
//        String result = null;
//        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//        if (getActivity() != null) {
//            Cursor cursor = getActivity().
//                    getContentResolver().
//                    query(selectedImageUri, filePathColumn,
//                            null, null, null);
////                    query(selectedImageUri, proj, null, null, null);
//            if (cursor != null) {
//                cursor.moveToFirst();
//                int column_index = cursor.getColumnIndex(filePathColumn[0]);
//                result = cursor.getString(column_index);
//                cursor.close();
//            } else {
//                result = selectedImageUri.getPath();
//            }
//        }
//        Log.d(TAG, "getPathFromPhotoURL: " + result);
//        return result;
//    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            } else {
                filePath = uri.getPath();
            }
            cursor.close();
        }
        return filePath;
    }

    private void openPhotos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                REQUEST_BROWSE_IMAGE);
    }

    private class UploadCardTask extends AsyncTask<Void, UploadCardResponse, UploadCardResponse> {

        private Context mContext;
        private String fileName;

        UploadCardTask(Context mContext, String fileName) {
            this.mContext = mContext;
            this.fileName = fileName;
        }

        @Override
        protected UploadCardResponse doInBackground(Void... voids) {

//            //Create a file object using file path
//            File file = new File(cardFilePath);
//            // Create a request body with file and image media type
//            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file",
//                    file.getName(),
//                    RequestBody.create(MediaType.parse("image/*"),
//                            file));
//            //RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
//            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), appUserEmail);

            if (NetworkHelper.hasNetworkAccess(mContext)) {
                BusinessCardWebservice webservice = BusinessCardWebservice
                        .retrofit.create(BusinessCardWebservice.class);
//                Call<UploadCardResponse> call = webservice.uploadCard(filePart, email);
                Call<UploadCardResponse> call = webservice.uploadCard(fileName, appUserEmail);
                try {
                    return call.execute().body();
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
            progress.setVisibility(View.GONE);
            if (response != null) {
                Intent editIntent = new Intent(getActivity(), EditCardActivity.class);
                editIntent.putExtra(Constants.EXTRA_CARD_DETAIL, response);
                editIntent.putExtra(Constants.EXTRA_IMG_URI, cardImageUri);
                startActivity(editIntent);
                Log.d(TAG, "Card Parse detail: " + response.toString());
                Utility.getInstance().showMsg(getActivity().getApplicationContext(),
                        getString(R.string.txt_upload_success));
            }
        }
    }
}
