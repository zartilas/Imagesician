package unipi.p17168.imagesician

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.net.Uri.parse
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import unipi.p17168.imagesician.databinding.FragmentImageBinding
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.jvm.Throws


@Suppress("DEPRECATION") //for the method getImage
class ImageFragment : Fragment() {

    //~~~~~~~VARIABLES~~~~~~~

    //var
    private var _binding : FragmentImageBinding ? = null
    private var imageIsText = false

    //val
    private val binding get() = _binding!!



//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        val view = binding.root

        userTriggerButtons()

        return view
    }

//    fun init(){
//
//    }

    private fun userTriggerButtons() {
        binding.switchContextText.setOnClickListener{
            imageIsText = binding.switchContextText.isChecked

        }
        binding.floatingButton.setOnClickListener{
            pickImage() //custom fun
        }

    }

    private fun pickImage(){
        val openGalleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(openGalleryIntent, 666)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 666) {
            if(resultCode == Activity.RESULT_OK)
            {
                val bitmapImage = getImage(data)

//                if (bitmap != null) {
//                    binding.imageView.setImageBitmap(bitmap)
//                    processImageTagging(bitmap)
//                }
                bitmapImage.apply {
                    if(!imageIsText){
                        if (bitmapImage != null) {
                                val imageForIV =
                                    context?.let { checkIfImageNeededRotation(it,bitmapImage,bitmapImage) } //TODO FIX THIS LINE

                            binding.chipFailed.isVisible = false
                            binding.imageView.setImageBitmap(imageForIV)
                            processImageTagging(bitmapImage)
                        }
                    }
                    if (imageIsText){
                        if (bitmapImage != null) {
                            binding.imageView.setImageBitmap(bitmapImage)
                            startTextRecognizing(bitmapImage)
                        }
                    }
                }

                }
            }


        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("SetTextI18n")
    private fun startTextRecognizing(bitmap: Bitmap) {
        val recognizer = TextRecognition.getClient()
        val image = InputImage.fromBitmap(bitmap,0)
        val result = recognizer.process(image)
        binding.chipGroup.removeAllViews()
        result.addOnSuccessListener {
                binding.chipFailed.isVisible = true
                binding.chipFailed.text = "i am text"// }
            println("I AM TEXT BOOS")
            }.addOnFailureListener {
                // Task failed with an exception
                // ...
                binding.chipFailed.isVisible = true
                binding.chipFailed.chipText = "i am text Fail"
                println("I AM TEXT  FAIL BOSS")
            }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun processImageTagging(bitmap: Bitmap){

        // Multiple object detection in static images
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()

        val objectDetector = ObjectDetection.getClient(options)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        val listWithLabels = arrayListOf<String>()

        try {
            val image = InputImage.fromBitmap(bitmap,0)
            fun objDetector(firstFailed :Boolean) {
                //OBJECT DETECTOR
                objectDetector.process(image)
                    .addOnSuccessListener {
                        // Task completed successfully
                        for (detectedObject in it) {
                            for (label in detectedObject.labels) {
                                val textDetected = label.text
                                if (!listWithLabels.contains(textDetected)) {
                                    listWithLabels.add(textDetected)

                                }
                            }
                        }
                        binding.chipGroup.removeAllViews()
                        listWithLabels.sortedByDescending { it.length }.map {
                            Chip(
                                context,
                                null,
                                R.style.Widget_MaterialComponents_Chip_Choice
                            ).apply {
                                text = it }
                        }.forEach {
                                binding.chipGroup.addView(it) }
                    }.addOnFailureListener {
                        // Task failed with an exception
                        if(firstFailed){
                            binding.chipFailed.isVisible = true
                            binding.chipFailed.text = "I may be an AI app but not God, I can't know everything, I'm sorry." }
                    }
            }
           // LABELER
            labeler.process(image)
                .addOnSuccessListener {
                    // Task completed successfully
                     for (label in it) {
                         val textLabeler = label.text
                         if (!listWithLabels.contains(label.text)) {
                             listWithLabels.add(textLabeler)
                     }
                         println("The labeler : $textLabeler")
                     }

                    //OBJECT DETECTOR
                    objDetector(false)

                }.addOnFailureListener {
                    // Task failed with an exception
                    objDetector(true)
                }

        } catch (e: IOException) {
            binding.chipFailed.isVisible = true
            binding.chipFailed.text = "Something went wrong,try again."
            e.printStackTrace()
        }
    }

    //get image from data
    private fun getImage(data: Intent?): Bitmap?{
        val selectedImage = data?.data
        return MediaStore.Images.Media.getBitmap(context?.contentResolver,selectedImage)
    }

    //Get Image Uri from bitmap
    private fun  getImageUri(context: Context, bitmap: Bitmap): Uri? {
        val bytes =  ByteArrayOutputStream()
        bitmap.compress( Bitmap.CompressFormat.JPEG,100,bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "imageUri", null)
        return parse(path)
    }

    //Check if the photo needs rotation
   @Throws(IOException::class)
    private fun checkIfImageNeededRotation(context: Context, newImage: Bitmap, selectedImage: Bitmap): Bitmap? {
        val selectedImageForRotation =  getImageUri(context,selectedImage)
        val input = selectedImageForRotation?.let { context.contentResolver.openInputStream(it) }
        val ei = ExifInterface(input!!)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(newImage, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(newImage, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(newImage, 270)

            else -> newImage
        }
    }

    private fun rotateImage(newImage: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedNewImage = Bitmap.createBitmap(newImage, 0, 0, newImage.width, newImage.height, matrix, true)
        newImage.recycle()
        return rotatedNewImage
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





