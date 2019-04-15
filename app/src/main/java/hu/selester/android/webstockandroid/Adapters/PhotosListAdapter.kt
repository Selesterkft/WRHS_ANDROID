package hu.selester.android.webstockandroid.Adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.selester.android.webstockandroid.Database.Tables.PhotosTable
import hu.selester.android.webstockandroid.Helper.HelperClass
import hu.selester.android.webstockandroid.Helper.KT_HelperClass
import hu.selester.android.webstockandroid.R
import kotlinx.android.synthetic.main.row_photos_list.view.*

class PhotosListAdapter(private var context: Context, val dataList: MutableList<PhotosTable>, val listener: OnItemClickListener): RecyclerView.Adapter<PhotosListAdapter.ViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick( item: String)
        fun onDelQuestion( item: Int)
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var name = view.photos_list_text
        var image = view.photos_list_image
        var datetime = view.photos_list_date
        var delBtn = view.photos_delBin
        var showLayer = view.photos_textLayout
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_photos_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = dataList[position].ptypeText
        holder.datetime.text = dataList[position].datetime
        holder.image.setImageBitmap(KT_HelperClass.loadLocalImage(dataList[position].filePath,20) )
        var image: Drawable? = context.getDrawable(R.drawable.red_bin)
        when(dataList[position].uploaded){
            1 -> image = context.getDrawable(R.drawable.uploading)
            2 -> image = context.getDrawable(R.drawable.uploaded)
            3 -> image = context.getDrawable(R.drawable.errorupload)
        }
        holder.delBtn.setImageDrawable(image)
        holder.delBtn.setOnClickListener { listener.onDelQuestion (position) }


        holder.image.setOnClickListener {
            listener.onItemClick(dataList[position].filePath)
        }
        holder.showLayer.setOnClickListener {
            listener.onItemClick(dataList[position].filePath)
        }
    }

    fun removeAt(position: Int) {
        dataList.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(item: PhotosTable){
        dataList.add(0,item)
        notifyDataSetChanged()
    }

    fun refreshList(newList: MutableList<PhotosTable>){
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }
}