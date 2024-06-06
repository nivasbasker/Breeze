package com.zio.breeze.ui.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zio.breeze.R
import com.zio.breeze.data.ForecastItem

class ForecastAdapter(private val itemList: List<ForecastItem>, val isCelsius: Boolean) :
    RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val temp: TextView = itemView.findViewById(R.id.hourly_temp)
        val time: TextView = itemView.findViewById(R.id.hourly_hour)
        val climate: ImageView = itemView.findViewById(R.id.hourly_climate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        if (isCelsius) holder.temp.text = item.getInCelsius()
        else holder.temp.text = item.getInKelvin()

        holder.time.text = item.time

        when (item.climate) {
            "Clouds" -> holder.climate.setImageResource(R.drawable.img_clouds)
            "Rain" -> holder.climate.setImageResource(R.drawable.img_rains)
        }
    }

    override fun getItemCount() = itemList.size
}

