package com.example.filemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.R
import com.example.filemanager.adapter.FileAdapter
import com.example.filemanager.viewmodel.FileViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private val viewModel: FileViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        adapter = FileAdapter { fileItem ->
            // TODO: open file or folder on click
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observe file list
        viewModel.fileList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // Swipe to delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.currentList[viewHolder.adapterPosition]
                lifecycleScope.launch {
                    viewModel.deleteFile(item)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Load initial directory (root)
        viewModel.loadRoot()
    }
}
