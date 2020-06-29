package com.zwyl.guide.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.zwyl.guide.App;
import com.zwyl.guide.R;
import com.zwyl.guide.viewstate.CenterCropRoundCornerTransform;

import java.io.File;
import java.io.IOException;


/**
 *
 */
public class UIUtils {


    private static RequestOptions options = new RequestOptions()
            //默认照片
            .placeholder(R.mipmap.textbook).error(R.mipmap.textbook);
    private static RequestOptions optionsTranform = new RequestOptions().centerCrop()//默认照片带圆角
            .placeholder(R.mipmap.textbook).error(R.mipmap.textbook).transform(new CenterCropRoundCornerTransform(10));
    private static RequestOptions optionsCenterCrop = new RequestOptions().centerCrop()//默认照片不带圆角
            .placeholder(R.mipmap.textbook).error(R.mipmap.dili);
    private static RequestOptions optionsHead = new RequestOptions().centerCrop()//用户头像
            .circleCropTransform().placeholder(R.mipmap.textbook).error(R.mipmap.textbook);
    private static RequestOptions optionsDoctor = new RequestOptions().centerCrop()//医生头像
            .circleCropTransform().placeholder(R.mipmap.textbook).error(R.mipmap.textbook);
    private static RequestOptions addPhoto = new RequestOptions().centerCrop()//默认照片带圆角
            .error(R.mipmap.textbook).transform(new CenterCropRoundCornerTransform(10));
    private static RequestOptions answerSheet = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);

    public static void updateOptions(RequestOptions options, String url, long modified, ImageView imageView) {
        try {
            String tail = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(tail);
            options.signature(new MediaStoreSignature(type, modified, 0));
            Glide.with(App.mContext).load(url).apply(options).into(imageView);
        } catch (Exception e) {
        }
    }

    /**
     * //默认照片不带圆角
     */
    public static void setCenterCrop(String url, ImageView imgeview) {
        Glide.with(App.mContext).load(url).apply(answerSheet).into(imgeview);
    }

    /**
     * 转化为压缩后的uri
     */
    public static Uri getCompressUri(Uri uri) {
        ContentResolver resolver = App.getContext().getContentResolver();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //85
        Bitmap showbitmap = ThumbnailUtils.extractThumbnail(bitmap, 200, 200);
        return Uri.parse(MediaStore.Images.Media.insertImage(resolver, showbitmap, null, null));
    }

    public static String picUri2String(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = App.getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(App.getContext(), "没有发现图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;
            }
            return picturePath;
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(App.getContext(), "没有发现图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;
            }
            return file.getAbsolutePath();
        }
    }


}
