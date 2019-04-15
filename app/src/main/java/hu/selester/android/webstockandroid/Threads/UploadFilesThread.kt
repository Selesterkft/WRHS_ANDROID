package hu.selester.android.webstockandroid.Threads

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import hu.selester.android.webstockandroid.Database.SelesterDatabase
import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment
import hu.selester.android.webstockandroid.Helper.Multipart
import hu.selester.android.webstockandroid.Objects.SessionClass
import org.json.JSONObject
import java.io.File
import java.net.URL


class UploadFilesThread(val context: Context?, val tranCode:String , val tranID:String, val f: MovesSubTableFragment):Thread(){

    val db: SelesterDatabase = SelesterDatabase.getDatabase(context!!)!!
    var allComplated = true

    override fun run() {
        val list = db.photosDao().getAllNotUploadedData()
        if(list.size > 0) {
            allComplated = true
            list.forEach {
                db.photosDao().setUploadStatus(it.id!!, 1)
                if( it.addrId != null && !it.addrId.equals("null") && !it.addrId.equals("null") ) {
                    uploadFile2(it.filePath, it.addrId, it.ptype, it.id!!)
                }else{
                    allComplated = false
                }
                Log.i("TAG", "OUT THREAD")
            }
            if (allComplated){
                db.sessionTempDao().deleteAllData()
                val dir = File( context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath )
                if (dir.isDirectory()) {
                    val children = dir.list()
                    for (i in children.indices) {
                        File(dir, children[i]).delete()
                    }
                }
                CloseLineThread(context,tranCode, tranID, f).start()
                Log.i("TAG", "ALL COMPLATED!")
            }else{
                Toast.makeText(context, "Lezárás nem sikerült, hiba a fájlok feltöltésekor!", Toast.LENGTH_LONG).show()
            }

        } else {
            CloseLineThread(context,tranCode, tranID, f).start()
        }
    }

    private fun uploadFile2(filePathString: String, addrID: Int, docType: Int, appId : Long){
        val selectedFileUri = Uri.parse(filePathString)
        if(selectedFileUri != null){
            //Thread (Runnable{
                val file = File(filePathString)
                val filename = file.path.substring(file.path.lastIndexOf("/") + 1)
                val appID = appId
                Log.i("TAG", filename)
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val content_type = getMimeType(file.path)
                val client = Multipart(URL(SessionClass.getParam("WSUrl") + "/PostImage"))
                client.addFilePart("pic", file, filename, content_type!!)
                client.addHeaderField("addrid", addrID.toString())
                //client.addHeaderField("doctypeid", docType.toString())
                //client.addHeaderField("addrid", "118319630")
                client.addHeaderField("doctypeid", "-1003")
                Log.i("TAG", addrID.toString() + " - " + docType.toString())
                client.upload(object : Multipart.OnFileUploadedListener {
                    override fun onFileUploadingSuccess(response: String) {
                        Log.i("TAG", response)
                        val jsonText = JSONObject(response).getString("UploadFileResult")
                        val jsonRoot = JSONObject(jsonText)
                        Log.i("TAG", "ERROR CODE " + jsonRoot.getString("ERROR_CODE") )
                        if( jsonRoot.getString("ERROR_CODE").equals("-1") ){
                            db.photosDao().setUploadStatus(appID,2)
                            Log.i("TAG","Upload OK")
                        }else{
                            errorUpload(appID)
                            Log.i("TAG","Upload Tried")
                        }
                    }

                    override fun onFileUploadingFailed(responseCode: Int) {
                        allComplated = false
                        errorUpload(appID)
                        Log.i("TAG","FAILD: "+appID)

                    }
                })
            //}).start()
        }else{

        }
    }

    fun errorUpload(appID: Long){
        val photoData = db.photosDao().getDataWithID(appID)
        Log.i("TAG","tried: "+photoData.tried)
        if( photoData.tried < 6){
            db.photosDao().addTried(appID)
        }else{
            db.photosDao().setUploadStatus(appID,3)
        }
    }

    fun getRealPathFromURI(uri: Uri): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = context!!.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, arrayOf(id), null)
        val columnIndex = cursor!!.getColumnIndex(column[0])
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
        return filePath
    }

    private fun getMimeType(path: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}