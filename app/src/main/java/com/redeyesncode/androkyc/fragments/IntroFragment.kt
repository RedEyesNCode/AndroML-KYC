package com.redeyesncode.androkyc.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.redeyesncode.androkyc.R
import com.redeyesncode.androkyc.databinding.FragmentIntroBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IntroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IntroFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    lateinit var binding:FragmentIntroBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIntroBinding.inflate(layoutInflater)


        if(param1.equals("document")){
            binding.tvSubtitle.text = "Before we start make sure you have all the required documents nearby"
            binding.tvTitle.text = "Get ID document ready"
            binding.ivTitle.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_document_ready))
        }else if(param1.equals("face")){
            binding.tvSubtitle.text = "Your face to be well it.Make sure you don't have any background lights."
            binding.tvTitle.text = "Take a Selfie"
            binding.ivTitle.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_face_detect))
        }else if(param1.equals("upload")){
            binding.tvSubtitle.text = "Upload or Scan your proper documents."
            binding.tvTitle.text = "Scan your ID Document"
            binding.ivTitle.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_image_upload))
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IntroFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IntroFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}