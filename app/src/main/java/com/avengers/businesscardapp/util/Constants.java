package com.avengers.businesscardapp.util;

public class Constants {

    public static final String COGNITO_POOL_ID = "";
    public static final String BUCKET_NAME = "";

    public static final String PREFS_EMAIL_ID = "Email_Id";
    public static final String PREFS_LOGIN = "is_login";

    public static final String EXTRA_CARD_DETAIL = "card_detail";
    public static final String EXTRA_IMG_URI = "img_uri";
    public static final int RESPONSE_FAIL = 417;
    public static final int RESPONSE_OK = 200;

//    private String getFileName(Uri uri) {
//        String result = null;
//        if (uri.getScheme().equals("content")) {
//            Cursor cursor = getActivity().getContentResolver().query(uri,
//                    null, null, null, null);
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//        if (result == null) {
//            result = uri.getPath();
//            int cut = result.lastIndexOf('/');
//            if (cut != -1) {
//                result = result.substring(cut + 1);
//            }
//        }
//        return result;
//    }
}
