package com.example.marvelapirecyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class characterRecyclerViewAdapter(private val characterList: MutableList<Character>) : RecyclerView.Adapter<characterRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.heroImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionView2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = characterList[position]
        Log.d("CHECKING", character.description)
        // Bind data to views
        Glide.with(holder.itemView.context)
            .load(character.thumbnailUrl)
            .placeholder(R.drawable.marvelspiderman)
            .error(R.drawable.marvelspiderman)
            .into(holder.imageView)

        holder.nameTextView.text = character.name
        holder.descriptionTextView.text = character.description
        // Toast pops up as the view being clicked,
        // printing the name of the character
        holder.itemView.setOnClickListener {
            val clickedCharacter = characterList[position]
            val toastMessage = "Clicked on ${clickedCharacter.name} at position $position"
            Toast.makeText(holder.itemView.context, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = characterList.size

    fun setCharacterList(characters: List<Character>) {
        characterList.clear() // Clear the existing list
        characterList.addAll(characters) // Add the new list of characters
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }

}


// data class to hold the structure of pulled data.
data class Character(
    val name: String,
    val description: String,
    val thumbnailUrl: String
)
