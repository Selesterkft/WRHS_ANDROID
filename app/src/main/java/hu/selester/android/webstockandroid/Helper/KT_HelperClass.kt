package hu.selester.android.webstockandroid.Helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.provider.Settings
import android.widget.Toast
import java.io.IOException

class KT_HelperClass(){

    companion object {

        fun getSharedPreferences(act: Context, key: String): String{
            val prefs = act.getSharedPreferences("selTransport_logged", 0)

            return prefs.getString(key,"")!!
        }

        fun setSharedPreferences(act: Context, key: String, value: String){
            val prefs = act.getSharedPreferences("selTransport_logged", 0)
            val editor = prefs.edit()
            editor.putString(key,value)
            editor.apply()
        }

        fun toast(context: Context?, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }

        fun toast(context: Context?, text: String, showInt: Int) {
            Toast.makeText(context, text, showInt).show()
        }

        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

        fun getAndroidID(context: Context): String {
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }

        fun loadLocalImage(path: String, scaledSize: Int): Bitmap? {
            //Log.i("GU","loadLocalImage: "+path);
            return getPicCorrectOrientation(path, scaledSize)
        }

        fun getPicCorrectOrientation(filePath: String, scaledSize: Int): Bitmap? {
            val options = BitmapFactory.Options()
            options.inScaled = true
            options.inSampleSize = scaledSize
            val bitmap = BitmapFactory.decodeFile(filePath, options)
            var ei: ExifInterface? = null
            try {
                ei = ExifInterface(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val orientation = ei!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            var rotatedBitmap: Bitmap?
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90f)

                ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmap, 180f)

                ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(bitmap, 270f)

                ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
                else -> rotatedBitmap = bitmap
            }

            return rotatedBitmap
        }

        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }
    }


}