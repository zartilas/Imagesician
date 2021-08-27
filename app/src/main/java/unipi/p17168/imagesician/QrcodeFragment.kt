package unipi.p17168.imagesician

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.fragment_qrcode.*
import unipi.p17168.imagesician.databinding.FragmentQrcodeBinding
import unipi.p17168.imagesician.utils.QrRecognizing
import unipi.p17168.imagesician.utils.ToolBox

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import com.google.mlkit.vision.barcode.BarcodeScanning

class QrcodeFragment : androidx.fragment.app.Fragment() {

    //var

    private var _binding: FragmentQrcodeBinding? = null

    //val
    private val binding get() = _binding!!
    private val contextQrCode get() = this@QrcodeFragment.requireContext()

    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA  // Select back camera
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val processingBarcode = AtomicBoolean(false)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentQrcodeBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }


     private fun userTriggerButtons() {
        binding.scanButton.setOnClickListener{
            startUserCamera()
        }
     }

    private fun init() {
        ToolBox().setupPermissions(contextQrCode, requireActivity(), CAMERA, 234)
        userTriggerButtons()
    }


    private fun startUserCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(contextQrCode)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(
                    binding.cameraPreviewView.surfaceProvider
                )
            }

            try {
                val imageAnalysis = ImageAnalysis.Builder().build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy: ImageProxy? ->
                        if (processingBarcode.compareAndSet(false, true)) {
                            if (imageProxy != null) {
                                Log.wtf("MyApp 66", "I am here boss")

                                QrRecognizing().analyze(imageProxy)

                            }
                        }
                    }
                }



                cameraProvider.unbindAll() // Unbind any bound use cases before rebinding
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )  // Bind use cases to lifecycleOwner

            } catch (e: Exception) {
                ToolBox().showSnackBar(
                    this@QrcodeFragment.requireView(),
                    ContextCompat.getColor(contextQrCode, R.color.colorErrorBackgroundSnackbar),
                    ContextCompat.getColor(contextQrCode, R.color.colorStrings),
                    "Something went wrong!",
                    "OK",
                    Snackbar.ANIMATION_MODE_SLIDE
                ).show()
                Log.e("PreviewUseCase", "Binding failed! :(", e)
            }
        }, ContextCompat.getMainExecutor(contextQrCode))
    }
}


