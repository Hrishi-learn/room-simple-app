package com.hrishi.roomdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hrishi.roomdemo.databinding.ItemsBinding

class ItemAdapter(private var items:ArrayList<EmployeeEntity>,private var updateListener:(id:Int)->Unit,
                    private var deleteListener:(id:Int)->Unit):RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

    class ViewHolder(binding: ItemsBinding):RecyclerView.ViewHolder(binding.root){
        var llMain=binding.llMain
        var tvName=binding.tvName
        var tvEmail=binding.tvEmail
        var ivDelete=binding.ivDelete
        var ivEdit=binding.ivEdit
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context=holder.itemView.context
        val item=items[position]
        holder.tvName.text=item.name
        holder.tvEmail.text=item.email
        if(position%2==0){
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context,R.color.lightGray))
        }
        holder.ivEdit.setOnClickListener {
            updateListener.invoke(item.id)
        }
        holder.ivDelete.setOnClickListener {
            deleteListener.invoke(item.id)
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}