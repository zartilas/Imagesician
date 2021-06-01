package unipi.p17168.imagesician.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import unipi.p17168.imagesician.wiki.WikiListItems
import com.squareup.picasso.Picasso
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.inflate
import android.view.View.*
import android.view.ViewGroup
import unipi.p17168.imagesician.databinding.WikiItemsBinding

class RecyclerViewWikiAdapter(private val itemsList: List<WikiListItems>) :
    RecyclerView.Adapter<RecyclerViewWikiAdapter.SetHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetHolder {
        val inflatedView: View = parent.inflate(R.layout.wiki_items, false)

        return SetHolder(
            inflatedView
        )
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: SetHolder, position: Int) {
        val itemCard = itemsList[position]
        holder.bindSet(itemCard)
    }

    class SetHolder(v: View) : RecyclerView.ViewHolder(v),
        OnClickListener {

        private var itemsList: WikiListItems? = null
        private var _binding : WikiItemsBinding? = null
        private val binding get() = _binding!!

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {

        }

        fun bindSet(itemsList: WikiListItems) {
            this.itemsList = itemsList

            binding.itemName.text = itemsList.item
            binding.itemInfo.text = itemsList.itemInfo
        }
    }
}