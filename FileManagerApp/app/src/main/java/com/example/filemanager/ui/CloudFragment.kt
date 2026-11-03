package com.example.filemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class CloudFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Simple placeholder UI – you can replace with Google Drive/OneDrive UI later
        return inflater.inflate(R.layout.fragment_cloud, container, false)
    }
}
