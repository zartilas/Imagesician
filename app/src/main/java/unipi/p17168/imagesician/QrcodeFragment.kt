package unipi.p17168.imagesician

import android.Manifest.permission.CAMERA
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_qrcode.*
import unipi.p17168.imagesician.databinding.FragmentQrcodeBinding
import unipi.p17168.imagesician.utils.QrRecognizing
import unipi.p17168.imagesician.utils.ToolBox

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

typealias BarcodeListener = (barcode: String) -> Unit

class QrcodeFragment : androidx.fragment.app.Fragment() {

    //var
    private var _binding : FragmentQrcodeBinding? = null

    //val
    private val binding get() = _binding!!
    private val contextQrCode get() = this@QrcodeFragment.requireContext()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQrcodeBinding.inflate(inflater, container, false)

        Log.wtf("MyApp","I am here");
        startUserCamera()
        ToolBox().setupPermissions(contextQrCode,requireActivity(),CAMERA,234)

        return binding.root
    }

    private fun startUserCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(contextQrCode)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build() .also {
                it.setSurfaceProvider(
                    binding.cameraPreviewView.surfaceProvider
                )
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA  // Select back camera
            val cameraExecutor = Executors.newSingleThreadExecutor()
            val  processingBarcode =  AtomicBoolean(false)
            try {
                val imageAnalysis = ImageAnalysis.Builder().build().also{
                    it.setAnalyzer(cameraExecutor) { imageProxy: ImageProxy? ->
                        if (processingBarcode.compareAndSet(false, true)) {
                            if (imageProxy != null){
                                QrRecognizing().analyze(imageProxy)
                            }
                        }

                    }
                }
                cameraProvider.unbindAll() // Unbind any bound use cases before rebinding
                cameraProvider.bindToLifecycle(this, cameraSelector, preview,imageAnalysis)  // Bind use cases to lifecycleOwner

            } catch (e: Exception) {
                ToolBox().showSnackBar(this@QrcodeFragment.requireView(),
                    ContextCompat.getColor(contextQrCode,R.color.colorErrorBackgroundSnackbar),
                    ContextCompat.getColor(contextQrCode,R.color.colorStrings),
                    "Something went wrong!",
                    "OK",
                    Snackbar.ANIMATION_MODE_SLIDE).show()
                Log.e("PreviewUseCase", "Binding failed! :(", e)
            }
        }, ContextCompat.getMainExecutor(contextQrCode))
    }
}

