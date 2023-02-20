package com.code4rox.medialoaderx

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.code4rox.medialoaderx.databinding.FolderViewBinding
import com.code4rox.medialoaderx.databinding.ItemViewBinding


open class AllFilesRecyclerAdapterX : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: ArrayList<*> = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view, parent, false)
        ) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when (list.firstOrNull()) {
            is ImageItem -> {
                val item = list[position] as ImageItem

                ItemViewBinding.bind(holder.itemView).apply {
                    Glide.with(holder.itemView.context)
                        .load(item.uri)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(CenterInside(), RoundedCorners(5))
                        .into(itemViewImg)
                }
            }
            is AudioItem -> {
                val item = list[position] as AudioItem

                ItemViewBinding.bind(holder.itemView).apply {

                    Glide.with(holder.itemView.context)
                        .load(R.drawable.ic_audio)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(CenterInside(), RoundedCorners(5))
                        .into(itemViewImg)
                }
            }
            is VideoItem -> {
                val item = list[position] as VideoItem

                ItemViewBinding.bind(holder.itemView).apply {

                    videoPlayImg.isVisible = true
                    Glide.with(holder.itemView.context)
                        .load(item.uri)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(CenterInside(), RoundedCorners(5))
                        .into(itemViewImg)
                }
            }
            is FileItem -> {
                val item = list[position] as FileItem


                ItemViewBinding.bind(holder.itemView).apply {
                    titleTxt.isVisible = true
                    titleTxt.text = item.title
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.ic_files)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(CenterInside(), RoundedCorners(5))
                        .into(itemViewImg)
                }
            }
        }


    }

    override fun getItemCount(): Int = list.size

    fun setData(list: ArrayList<*>) {
        this.list = list
        notifyDataSetChanged()
    }
}


open class AllFolderRecyclerAdapterX : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: ArrayList<*> = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.folder_view, parent, false)
        ) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when (list.firstOrNull()) {
            is ImageFolder -> {
                val item = list[position] as ImageFolder

                FolderViewBinding.bind(holder.itemView).apply {
                    folderTxt.text = item.folderName
                }
            }
            is AudioFolder -> {
                val item = list[position] as AudioFolder

                FolderViewBinding.bind(holder.itemView).apply {
                    folderTxt.text = item.folderName
                }
            }
            is VideoFolder -> {
                val item = list[position] as VideoFolder

                FolderViewBinding.bind(holder.itemView).apply {
                    item.folderName.printIt()
                    folderTxt.text = item.folderName
                }
            }
            is FileFolder -> {
                val item = list[position] as FileFolder

                FolderViewBinding.bind(holder.itemView).apply {
                    folderTxt.text = item.folderName
                }
            }
        }

    }

    override fun getItemCount(): Int = list.size

    fun setData(list: ArrayList<*>) {
        this.list = list
        notifyDataSetChanged()
    }


}