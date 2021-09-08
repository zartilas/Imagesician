package unipi.p17168.imagesician

import android.Manifest.permission.CAMERA
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_qrcode.*
import unipi.p17168.imagesician.databinding.FragmentQrcodeBinding
import unipi.p17168.imagesician.utils.ToolBox
import kotlinx.android.synthetic.main.fragment_image.*
import unipi.p17168.imagesician.databinding.FragmentHistoryBinding
import unipi.p17168.imagesician.models.UserReco


class HistoryFragment : androidx.fragment.app.Fragment(){

    //var
    private var _binding: FragmentHistoryBinding? = null

    //val
    private val binding get() = _binding!!
    private val contextQrCode get() = this@HistoryFragment.requireContext()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

     private fun setupClickListeners() {

     }

    private fun init() {
        ToolBox().setupPermissions(contextQrCode, requireActivity(), CAMERA, 234)
        setupClickListeners()

    }
    /**
     * A function to get the successful tables list from cloud firestore.
     *
     * @param userLogsList Will receive the tables list from cloud firestore.
     */
    fun successUserLogsFromFireStore(userLogsList: ArrayList<UserReco>) {

        //hideProgressDialog()

        if (userLogsList.size > 0) {

            val userLogAdapter = UserLogListAdapter(this, userLogsList)

            binding.apply {
                recyclerView.run {
                    layoutManager = LinearLayoutManager(this@UserLogsListActivity, LinearLayoutManager.VERTICAL, false)
                    setHasFixedSize(true)

                    adapter = userLogAdapter
                }
                layoutEmptyState.root.visibility = View.GONE
            }
        }
        else
            binding.layoutEmptyState.root.visibility = View.VISIBLE
    }

    fun hideLogs() {
        hideProgressDialog()
        binding.layoutEmptyState.root.visibility = View.VISIBLE
    }


    override fun onResume() {
        super.onResume()

    }


}