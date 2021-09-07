package unipi.p17168.imagesician

import android.Manifest.permission.CAMERA
import android.os.Bundle
import android.util.Log
import androidx.camera.core.Preview;
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_qrcode.*
import unipi.p17168.imagesician.databinding.FragmentQrcodeBinding

import unipi.p17168.imagesician.utils.ToolBox

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.android.synthetic.main.fragment_image.*
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor

import androidx.camera.core.ImageAnalysis

import androidx.camera.core.CameraSelector
import unipi.p17168.imagesician.utils.QrRecognizing


class QrcodeFragment : androidx.fragment.app.Fragment(){

    //var
    private var _binding: FragmentQrcodeBinding? = null

    //val
    private val binding get() = _binding!!
    private val contextQrCode get() = this@QrcodeFragment.requireContext()

    //private val scannerFragmentContext: ScannerFragment? = null
    var processingBarcode = AtomicBoolean(false)
    private val qrRecognizing: QrRecognizing = QrRecognizing()
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var analysisExecutor: Executor? = null
    private var camera: Camera? = null
    var toggleFlash = false



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
           // startUserCamera()
            useCamera()
        }
     }

    private fun init() {
        ToolBox().setupPermissions(contextQrCode, requireActivity(), CAMERA, 234)
        userTriggerButtons()
        useCamera()
        //startUserCamera()
    }


    private fun useCamera(){
        analysisExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture!!.addListener(Runnable {
            try {
                val cameraProvider = cameraProviderFuture!!.get()
                bindPreview(cameraProvider)
            } catch (ignored: ExecutionException) {
            } catch (ignored: InterruptedException) {
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun bindPreview(cameraProvider: ProcessCameraProvider) {

        val preview = Preview.Builder()
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        preview.setSurfaceProvider( binding.cameraPreviewView.surfaceProvider)

        analysisExecutor?.let {
            imageAnalysis.setAnalyzer(it, { imageProxy: ImageProxy? ->
                if (processingBarcode.compareAndSet(false, true)) {
                    if (imageProxy != null) {
                        Log.wtf("MyApp 66", "I am here boss")
                        qrRecognizing.analyze(imageProxy)
                    }
                }
            })
        }
        cameraProvider.unbindAll()
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
    }

    override fun onResume() {
        super.onResume()
        processingBarcode.set(false)
    }


}