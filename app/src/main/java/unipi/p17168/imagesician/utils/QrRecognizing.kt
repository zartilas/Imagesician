package unipi.p17168.imagesician.utils


import android.annotation.SuppressLint
import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage

class QrRecognizing {

   /* private var options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    var barcodeScanner = BarcodeScanning.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        @SuppressLint("UnsafeOptInUsageError")
        val mediaImage: Image? = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(image).addOnSuccessListener {  barcodes ->
                if (barcodes.isNotEmpty()) {
                    for (barcode in barcodes) {
                        Log.wtf("My code", barcode.rawValue)
                        when (barcode.valueType) {
                            Barcode.TYPE_WIFI -> {
                                val ssid = barcode.wifi!!.ssid
                                val password = barcode.wifi!!.password
                                val type = barcode.wifi!!.encryptionType
                            }
                            Barcode.TYPE_URL -> {
                                val title = barcode.url!!.title
                                val url = barcode.url!!.url
                            }
                        }
                    }
                }
            }.addOnFailureListener {
            }
                .addOnCompleteListener {
                    // It's important to close the imageProxy
                    imageProxy.close()
                }
        }
    }*/
}
