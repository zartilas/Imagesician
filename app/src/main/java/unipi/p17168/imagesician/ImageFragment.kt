package unipi.p17168.imagesician

//import android.media.ExifInterface

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
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
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException


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
//                            val imageForIV = checkIfImageNeededRotation(bitmapImage)//TODO FIX THIS LINE

                            binding.chipFailed.isVisible = false
                            binding.imageView.setImageBitmap(bitmapImage)
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

        searchEng()

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

            val result = result.result
            for (block in result.textBlocks) {
                val blockText = block.text
                val blockCornerPoints = block.cornerPoints
                val blockFrame = block.boundingBox
                for (line in block.lines) {
                    val lineText = line.text
                    val lineCornerPoints = line.cornerPoints
                    val lineFrame = line.boundingBox
                    for (element in line.elements) {
                        val elementText = element.text
                        val elementCornerPoints = element.cornerPoints
                        val elementFrame = element.boundingBox
                    }
                }
            }







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

    private fun searchEng(){
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        val term = "kotlin"
        val mysearch = intent.putExtra(SearchManager.QUERY, term)
        println("My Search: $mysearch")
    }






    //get image from data
    private fun getImage(data: Intent?): Bitmap?{
        val selectedImage = data?.data
        return MediaStore.Images.Media.getBitmap(context?.contentResolver,selectedImage)
    }

//    fun rotateBitmap(original: Bitmap, degrees: Float): Bitmap? {
//        val width = original.width
//        val height = original.height
//        val matrix = Matrix()
//        matrix.preRotate(degrees)
//        val rotatedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true)
//        val canvas = Canvas(rotatedBitmap)
//        canvas.drawBitmap(original, 5.0f, 0.0f, null)
//        return rotatedBitmap
//    }

//    //Get Image Uri from bitmap
//    private fun  getImageUri(context: Context, bitmap: Bitmap): Uri? {
//       // val bytes =  ByteArrayOutputStream()
//        //bitmap.compress( Bitmap.CompressFormat.JPEG,100,bytes)
//        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "imageUri", null)
//        return parse(path)
//    }
//


//    //Check if the photo needs rotation
//    @Throws(IOException::class)
//    private fun checkIfImageNeededRotation(newImage: Bitmap): Bitmap? {
//
//    val bos = ByteArrayOutputStream()
//    newImage.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
//    val bitmapdata: ByteArray = bos.toByteArray()
//    val bs = ByteArrayInputStream(bitmapdata)
//
//        val ei = ExifInterface(bs)
//        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
//            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(newImage, 90F)
//            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(newImage, 180F)
//            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(newImage, 270F)
//
//            else -> newImage
//        }
//    }
//
//    private fun rotateImage(newImage: Bitmap, degree: Float): Bitmap? {
//        val matrix = Matrix()
//        matrix.postRotate(degree)
//        val rotatedNewImage = Bitmap.createBitmap(newImage, 0, 0, newImage.width, newImage.height, matrix, true)
//        newImage.recycle()
//        println("I AM HERE TOO BOSS")
//        return rotatedNewImage
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

