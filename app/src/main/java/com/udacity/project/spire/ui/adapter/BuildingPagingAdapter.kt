package com.udacity.project.spire.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.udacity.project.spire.R
import com.udacity.project.spire.databinding.ItemBuildingBinding
import com.udacity.project.spire.domain.model.Building

/**
 * PagingDataAdapter for displaying buildings in a RecyclerView with Paging 3.
 * Automatically handles pagination, loading states, and efficient updates.
 *
 * TODO #43: Implement BuildingPagingAdapter
 */
class BuildingPagingAdapter(
    private val onItemClick: (Building) -> Unit
) : PagingDataAdapter<Building, BuildingPagingAdapter.BuildingViewHolder>(BUILDING_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingViewHolder {
        val binding = ItemBuildingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuildingViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BuildingViewHolder, position: Int) {
        val building = getItem(position)
        if (building != null) {
            holder.bind(building)
        }
    }

    class BuildingViewHolder(
        private val binding: ItemBuildingBinding,
        private val onItemClick: (Building) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(building: Building) {
            binding.apply {
                textBuildingName.text = building.name
                textBuildingLocation.text = root.context.getString(
                    R.string.building_location_format,
                    building.city,
                    building.country
                )
                textBuildingHeight.text = root.context.getString(
                    R.string.building_height_format,
                    building.heightMeters
                )
                textBuildingFloors.text = root.context.getString(
                    R.string.building_floors_format,
                    building.floors
                )

                imageBuilding.load(building.imageUrl) {
                    crossfade(true)
                   // placeholder(R.drawable.placeholder_building)
                  //  error(R.drawable.error_building)
                }

                root.setOnClickListener {
                    onItemClick(building)
                }
            }
        }
    }

    companion object {
        private val BUILDING_COMPARATOR = object : DiffUtil.ItemCallback<Building>() {
            override fun areItemsTheSame(oldItem: Building, newItem: Building): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Building, newItem: Building): Boolean {
                return oldItem == newItem
            }
        }
    }
}
