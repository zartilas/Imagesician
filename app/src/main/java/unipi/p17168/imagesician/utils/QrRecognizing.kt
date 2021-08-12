package unipi.p17168.imagesician.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QrRecognizing : ImageAnalysis.Analyzer {


    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {

            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val result = scanner.process(image)

            result.addOnSuccessListener { barcodes ->
                // Task completed successfully
                for (barcode in barcodes) {
                    // barcodeListener( barcode.rawValue)
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints
                    val rawValue = barcode.rawValue

                    Log.e("bounds:","$bounds")
                    Log.e("corners:","$corners")
                    Log.wtf("rawValue:",rawValue)

                    // See API reference for complete list of supported types
                    when (barcode.valueType) {
                        Barcode.TYPE_WIFI -> {
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType
                        }
                        Barcode.TYPE_URL -> {
                            val title = barcode.url!!.title
                            val url = barcode.url!!.url
                            Log.e("test:","e")
                        }
                        Barcode.FORMAT_ALL_FORMATS->{
                            val test = barcode.displayValue
                            Log.wtf("test:",test)

                        }
                    }
                }
            }.addOnFailureListener {
                // Task failed with an exception
            }.addOnCompleteListener {
                // It's important to close the imageProxy
                imageProxy.close()
            }
        }
    }
}