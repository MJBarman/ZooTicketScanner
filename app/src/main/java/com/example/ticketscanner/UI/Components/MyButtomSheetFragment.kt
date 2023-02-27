package com.example.ticketscanner.UI.Components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ticketscanner.databinding.FragmentMyBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentMyBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

}
