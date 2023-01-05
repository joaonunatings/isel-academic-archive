package pt.isel.pdm.chessroyale.history

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.common.PuzzleInfo


/**
 * Implementation of the ViewHolder pattern. Its purpose is to eliminate the need for
 * executing findViewById each time a reference to a view's child is required.
 */
class HistoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val dayView = itemView.findViewById<TextView>(R.id.day)
    private val puzzleIdView = itemView.findViewById<TextView>(R.id.puzzleId)
    private val solvedView = itemView.findViewById<ImageView>(R.id.is_solved_view)

    /**
     * Binds this view holder to the given quote item
     */
    fun bindTo(puzzleInfo: PuzzleInfo, onItemClick: () -> Unit) {
        dayView.text = puzzleInfo.date.toLocaleString() // Date is horrible
        puzzleIdView.text = puzzleInfo.puzzleId
        if (puzzleInfo.solved)
            solvedView.setImageResource(android.R.drawable.presence_online)
        else
            solvedView.setImageResource(android.R.drawable.presence_offline)

        itemView.setOnClickListener {
            itemView.isClickable = false
            startAnimation {
                onItemClick()
                itemView.isClickable = true
            }
        }
    }

    private fun startAnimation(onAnimationEnd: () -> Unit) {

        val animation = ValueAnimator.ofArgb(
            ContextCompat.getColor(itemView.context, R.color.list_item_background),
            ContextCompat.getColor(itemView.context, R.color.list_item_background_selected),
            ContextCompat.getColor(itemView.context, R.color.list_item_background)
        )

        animation.addUpdateListener { animator ->
            val background = itemView.background as GradientDrawable
            background.setColor(animator.animatedValue as Int)
        }

        animation.duration = 400
        animation.doOnEnd { onAnimationEnd() }

        animation.start()
    }
}

/**
 * Adapts items in a data set to RecycleView entries
 */
class HistoryAdapter(
    private val dataSource: List<PuzzleInfo>,
    private val onItemClick: (PuzzleInfo) -> Unit
) : RecyclerView.Adapter<HistoryItemViewHolder>() {

    /**
    * Factory method of view holders (and its associated views)
    */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_view, parent, false)
        return HistoryItemViewHolder(view)
    }

    /**
     * Associates (binds) the view associated to [HistoryItemViewHolder] to the item at [position] of the
     * data set to be adapted.
     */
    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bindTo(dataSource[position]) {
            onItemClick(dataSource[position])
        }
    }

    /**
     * The size of the data set
     */
    override fun getItemCount() = dataSource.size
}