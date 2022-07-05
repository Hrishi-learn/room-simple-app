package com.hrishi.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrishi.roomdemo.databinding.ActivityMainBinding
import com.hrishi.roomdemo.databinding.CustomDialogBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val employeeDao=(application as EmployeeApp).db.employeeDao()
        binding?.btnAdd?.setOnClickListener {
            addRecord(employeeDao)
        }
        lifecycleScope.launch{
            employeeDao.fetchAllEmployees().collect{
                val list= ArrayList(it)
                addDataToRecyclerView(list,employeeDao)
            }
        }
    }
    fun addRecord(employeeDao: EmployeeDao){
        var name=binding?.etName?.text.toString()
        var email=binding?.etEmailId?.text.toString()

        if(binding?.etName?.text!!.isNotEmpty() && binding?.etName?.text!!.isNotEmpty()){
            lifecycleScope.launch{
                employeeDao.insert(EmployeeEntity(name=name,email=email))
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_SHORT).show()
                binding?.etName?.text?.clear()
                binding?.etEmailId?.text?.clear()
            }
        }
        else{
            Toast.makeText(this,"Please provide the above details",Toast.LENGTH_SHORT).show()
        }
    }
    fun addDataToRecyclerView(employeeList:ArrayList<EmployeeEntity>,employeeDao:EmployeeDao){
        if(employeeList.isNotEmpty()){
            val adapter=ItemAdapter(employeeList,
                {
                    update_id->
                    run {
                        updateRecordDialog(update_id, employeeDao)
                    }
                },
                {
                    delete_id->
                    run {
                        deleteRecordDialog(delete_id, employeeDao)
                    }
                }
            )
            binding?.rvItemsList?.adapter=adapter
            binding?.rvItemsList?.layoutManager=LinearLayoutManager(this)
            binding?.tvNoRecordsAvailable?.visibility= View.INVISIBLE
            binding?.rvItemsList?.visibility=View.VISIBLE
        }
        else{
            binding?.tvNoRecordsAvailable?.visibility= View.VISIBLE
            binding?.rvItemsList?.visibility=View.INVISIBLE
        }
    }
    private fun updateRecordDialog(id:Int,employeeDao:EmployeeDao){
        var updateDialog=Dialog(this,R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        var updateBinding=CustomDialogBinding.inflate(layoutInflater)
        updateDialog.setContentView(updateBinding.root)
        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect {
                if(it!=null){
                    updateBinding.etUpdateName.setText(it.name)
                    updateBinding.etUpdateEmailId.setText(it.email)
                }
            }
        }
        updateBinding.tvUpdate.setOnClickListener {
            if(updateBinding.etUpdateName.text.isNotEmpty() && updateBinding.etUpdateEmailId.text.isNotEmpty()){
                val name=updateBinding.etUpdateName.text.toString()
                val email=updateBinding.etUpdateEmailId.text.toString()
                lifecycleScope.launch{
                    employeeDao.update(EmployeeEntity(id,name,email))
                    Toast.makeText(this@MainActivity, "The records were updated", Toast.LENGTH_SHORT).show()
                    updateDialog.dismiss()
                }
            }
            else{
                Toast.makeText(this, "Please fill the contents before updating", Toast.LENGTH_SHORT).show()
            }
        }
        updateBinding.tvCancel.setOnClickListener {
               updateDialog.dismiss()
        }
        updateDialog.show()
    }
    private fun deleteRecordDialog(id:Int,employeeDao: EmployeeDao){
        var alertDialogBuilder=AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Do you want to delete?")

        alertDialogBuilder.setPositiveButton("Yes"){alertInterface,_ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(this@MainActivity, "The record has been successfully deleted", Toast.LENGTH_SHORT).show()
                alertInterface.dismiss()
            }
        }
        alertDialogBuilder.setNegativeButton("No"){alertInterface,_ ->
            alertInterface.dismiss()
        }
        var alertDialog:AlertDialog=alertDialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}