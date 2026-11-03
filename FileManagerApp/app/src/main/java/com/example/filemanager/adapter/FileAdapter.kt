package com.example.filemanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.R
import com.example.filemanager.model.FileItem

class FileAdapter(private val onClick: (FileItem) -> Unit) : ListAdapter<FileItem, FileAdapter.FileViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FileItem>() {
            override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean = oldItem.uri == newItem.uri
            override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean = oldItem == newItem
        }
    }

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.file_icon)
        private val name: TextView = itemView.findViewById(R.id.file_name)
        private val size: TextView = itemView.findViewById(R.id.file_size)

        fun bind(item: FileItem) {
            name.text = item.name
            size.text = if (item.isDirectory) "Folder" else "${item.size / 1024} KB"
            // Simple icon selection
            val res = if (item.isDirectory) R.drawable.ic_folder else R.drawable.ic_file
            icon.setImageResource(res)
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
