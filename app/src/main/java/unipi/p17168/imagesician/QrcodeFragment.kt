package unipi.p17168.imagesician

import android.Manifest.permission.CAMERA
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import kotlinx.android.synthetic.main.fragment_qrcode.*
import unipi.p17168.imagesician.databinding.FragmentQrcodeBinding
import unipi.p17168.imagesician.utils.ToolBox
import kotlinx.android.synthetic.main.fragment_image.*


class QrcodeFragment : androidx.fragment.app.Fragment(){

    //var
    private var _binding: FragmentQrcodeBinding? = null

    //val
    private val binding get() = _binding!!
    private val contextQrCode get() = this@QrcodeFragment.requireContext()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentQrcodeBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

     private fun setupClickListeners() {
        binding.scanButton.setOnClickListener{

        }
     }

    private fun init() {
        ToolBox().setupPermissions(contextQrCode, requireActivity(), CAMERA, 234)
        setupClickListeners()

    }


    override fun onResume() {
        super.onResume()

    }


}