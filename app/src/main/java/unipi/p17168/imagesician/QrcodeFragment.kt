package unipi.p17168.imagesician

import android.annotation.SuppressLint
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
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.fragment_qrcode.*
import unipi.p17168.imagesician.databinding.FragmentQrcodeBinding
import unipi.p17168.imagesician.utils.ToolBox


class QrcodeFragment : Fragment() {

    //var
    private var _binding : FragmentQrcodeBinding? = null

    //val
    private val binding get() = _binding!!
    private val contextQrCode get() = this@QrcodeFragment.requireContext()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        YourImageAnalyzer()



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQrcodeBinding.inflate(inflater, container, false)

        print("i am here boss2")

        return binding.root
    }



    fun startQrRecognizing(image:InputImage){
        val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC)
        .build()
        val scanner = BarcodeScanning.getClient(options)
        print("i am here boss")
        val result = scanner.process(image)
            result.addOnSuccessListener { barcodes ->
            // Task completed successfully
            for (barcode in barcodes) {
                val bounds = barcode.boundingBox
                val corners = barcode.cornerPoints
                val rawValue = barcode.rawValue
                println("bounds: $bounds")
                println("corners: $corners")
                println("rawValue: $rawValue")

                // See API reference for complete list of supported types
                when (barcode.valueType) {
                    Barcode.TYPE_WIFI -> {
                        val ssid = barcode.wifi!!.ssid
                        val password = barcode.wifi!!.password
                        val type = barcode.wifi!!.encryptionType
                        println("ssid: $ssid")
                        println("password: $password")
                        println("type: $type")
                    }
                    Barcode.TYPE_URL -> {
                        val title = barcode.url!!.title
                        val url = barcode.url!!.url
                        println("title: $title")
                        println("url: $url")
                    }
                }
            }
        }.addOnFailureListener {
            // Task failed with an exception
            val dialogWrong = ToolBox().showWrongDialog(contextQrCode)
            dialogWrong.show()
        }
    }

    private fun startUserCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(contextQrCode)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(
                    binding.cameraPreviewView.surfaceProvider
                )
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA  // Select back camera
            try {
                cameraProvider.unbindAll() // Unbind any bound use cases before rebinding
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)  // Bind use cases to lifecycleOwner
            } catch (e: Exception) {
                Log.e("PreviewUseCase", "Binding failed! :(", e)
            }
        }, ContextCompat.getMainExecutor(contextQrCode))
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

private class YourImageAnalyzer : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to an ML Kit Vision API
            print("i am here boss 1")
            QrcodeFragment().startQrRecognizing(image)
        }
    }
}