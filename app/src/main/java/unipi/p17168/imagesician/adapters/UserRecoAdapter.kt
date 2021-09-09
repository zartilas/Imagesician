package unipi.p17168.imagesician.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import unipi.p17168.imagesician.HistoryFragment
import unipi.p17168.imagesician.R
import unipi.p17168.imagesician.databinding.UserRecoHistoryBinding
import unipi.p17168.imagesician.models.UserReco


/**
 * A adapter class for tables list items.
 */
open class UserRecoAdapter(
    private val context: HistoryFragment,
    private var list: ArrayList<UserReco>
) : RecyclerView.Adapter<UserRecoAdapter.UserHistoryViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHistoryViewHolder {
        return UserHistoryViewHolder(
            UserRecoHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     */
    override fun onBindViewHolder(holder: UserHistoryViewHolder, position: Int) {
     /*   val model = list[position]

        holder.binding.apply {
            txtViewEquation.text =
                String.format(
                    context.getString(R.string.txt_equation_format),
                    model.numFirst,
                    model.numSecond
                )
            txtViewType.text =
                String.format(
                    context.getString(R.string.txt_type_format),
                    model.type
                )
            txtViewDateAdded.text = Constants.DATE_FORMAT.format(model.dateAdded)

            *//* ICON *//*
            if (model.type == Constants.TYPE_MISTAKE) {
                imgViewIcon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_cross))
                imgViewIcon.setColorFilter(context.getColor(R.color.colorRedImperial))
            }
            else if (model.type == Constants.TYPE_SOLVED)
                imgViewIcon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_check_mark))
            else if (model.type == Constants.TYPE_TIME_UP)
                imgViewIcon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_time_up))
            else if (model.type == Constants.TYPE_SKIP)
                imgViewIcon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_skip_track))
        }
//        holder.itemView.setOnClickListener {
//            context.playButtonPressSound(context)
//            val intent = Intent(context, TableResultActivity::class.java)
//            intent.putExtra(Constants.EXTRA_NUMBER_FIRST, model.number)
//            context.startActivity(intent)
//        }

        // Slide from right animation
        val animation: Animation =
            AnimationUtils.loadAnimation(context, R.anim.anim_from_right)
        holder.itemView.startAnimation(animation)*/
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class UserHistoryViewHolder(val binding: UserRecoHistoryBinding) : RecyclerView.ViewHolder(binding.root)
}