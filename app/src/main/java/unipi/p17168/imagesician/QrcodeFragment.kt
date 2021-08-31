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

import unipi.p17168.imagesician.utils.ToolBox

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import com.google.mlkit.vision.barcode.BarcodeScanning
import kotlinx.android.synthetic.main.fragment_image.*

class QrcodeFragment : androidx.fragment.app.Fragment() , ImageAnalysis.Analyzer{

    //var
    private var _binding: FragmentQrcodeBinding? = null

    //val
    private val binding get() = _binding!!
    private val contextQrCode get() = this@QrcodeFragment.requireContext()

    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA  // Select back camera
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val processingBarcode = AtomicBoolean(false)
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(contextQrCode)



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
        startUserCamera()
    }

    private fun startUserCamera(){
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(
                    binding.cameraPreviewView.surfaceProvider
                )
            }
            try {
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy: ImageProxy? ->
                        if (processingBarcode.compareAndSet(false, true)) {
                            if (imageProxy != null) {
                                Log.wtf("MyApp 66", "I am here boss")

                                /*QrRecognizing().*/analyze(imageProxy)
                                    /*  val type =  QrRecognizing().typeOfQR*/

                               /*     when (QrRecognizing().typeOfQR){
                                        true-> {Log.wtf("1","I am true boss")}
                                        false-> {Log.wtf("2","I am false boss")}
                                        null-> {Log.wtf("3","I am null boss , sorry")}

                                }*/
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


    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC)
        .build()

    var listWithResult = arrayListOf<String>()

//    var typeOfQR: Boolean? = null

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {

            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image).addOnSuccessListener { barcodes ->
                // Task completed successfully
                for (barcode in barcodes) {
                    // barcodeListener( barcode.rawValue)
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints
                    val rawValue = barcode.rawValue
                    Log.wtf("MyApp 69:","I AM HERE BOSS !!")
                    Log.e("bounds:","$bounds")
                    Log.e("corners:","$corners")
                    Log.wtf("rawValue:",rawValue)
                    // See API reference for complete list of supported types
                    when (barcode.valueType) {
                        Barcode.TYPE_WIFI -> {
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType
                            Log.wtf("test:","I AM WIFI BOSS")
//                            typeOfQR = true
                            listWithResult.add(ssid!!)
                            listWithResult.add(password!!)
                            listWithResult.add(type.toString())
                        }
                        Barcode.TYPE_URL -> {
                            Log.wtf("test:","I AM URL BOSS")
                            val title = barcode.url!!.title
                            val url = barcode.url!!.url
//                            typeOfQR = false
                            listWithResult.add(title!!)
                            listWithResult.add(url!!)

                            Log.e("My URL IS HERE BOSS:",url)
                        }
                        Barcode.FORMAT_ALL_FORMATS->{
//                            typeOfQR = null
                            val value = barcode.displayValue
                            listWithResult.add(value!!)
                            Log.wtf("test:",value)

                        }
                    }
                }
            }.addOnFailureListener {
                // Task failed with an exception
                ToolBox().showSnackBar(
                    this@QrcodeFragment.requireView(),
                    ContextCompat.getColor(contextQrCode, R.color.colorErrorBackgroundSnackbar),
                    ContextCompat.getColor(contextQrCode, R.color.colorStrings),
                    "Something went wrong!",
                    "OK",
                    Snackbar.ANIMATION_MODE_SLIDE
                ).show()
                Log.wtf("PreviewUseCase", "Binding failed! :( boss ")
            }.addOnCompleteListener {
                // It's important to close the imageProxy
                imageProxy.close()
            }
        }
    }
}