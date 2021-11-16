package com.io.tazarapp.ui.citizen.ad.adCreate.fragment

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TimePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.ScheduleItem
import com.io.tazarapp.ui.citizen.ad.adCreate.adapter.FillScheduleAdapter
import com.io.tazarapp.utils.boom
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import java.util.*


class BottomSheetFragment(var adapter: FillScheduleAdapter.ScheduleViewHolder) :
    BottomSheetDialogFragment() {
    private lateinit var mListener: BottomSheetListener
    private var mView: View? = null
    lateinit var scheduleItem: ScheduleItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        scheduleItem = this.arguments?.getSerializable("item") as ScheduleItem
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        boom(btn_save)

        when (scheduleItem.name) {
            1 -> title.text = getString(R.string.monday)
            2 -> title.text = getString(R.string.tuesday)
            3 -> title.text = getString(R.string.wednesday)
            4 -> title.text = getString(R.string.thursday)
            5 -> title.text = getString(R.string.friday)
            6 -> title.text = getString(R.string.saturday)
            7 -> title.text = getString(R.string.sunday)
        }

        is_off.isChecked = scheduleItem.is_weekend
        edit_from.setText(scheduleItem.start_at_time)
        edit_to.setText(scheduleItem.expires_at_time)
        listeners()
    }

    private fun listeners() {
        edit_to.setOnClickListener {
            edit_to.requestFocus()
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: MyPicker
            mTimePicker = MyPicker(
                edit_to,
                scheduleItem,
                false,
                requireContext(),
                    OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    val mHour = String.format("%02d", selectedHour)
                    val mMinute = String.format("%02d", selectedMinute)
                    edit_to.setText("$mHour:$mMinute")
                    scheduleItem.expires_at_time = "$mHour:$mMinute"
                },
                hour,
                minute,
                true
            )
            mTimePicker.show()
        }

        is_off.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                is_for_all.isChecked = false
        }
        is_for_all.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                is_off.isChecked = false
        }

        edit_from.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: MyPicker
            mTimePicker = MyPicker(
                    edit_from,
                    scheduleItem,
                    true,
                    requireContext(),
                    OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                        val mHour = String.format("%02d", selectedHour)
                        val mMinute = String.format("%02d", selectedMinute)
                        edit_from.setText("$mHour:$mMinute")
                        scheduleItem.start_at_time = "$mHour:$mMinute"
                    },
                    hour,
                    minute,
                    true
            )
            mTimePicker.show()
        }

        btn_save.setOnClickListener {
            scheduleItem.is_weekend = is_off.isChecked
            mListener.onReadyButtonClicked(scheduleItem,is_for_all.isChecked)
            dismiss()
        }

        btn_close.setOnClickListener { dismiss() }
    }

    interface BottomSheetListener {
        fun onReadyButtonClicked(scheduleItem: ScheduleItem,isForAll : Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = adapter
        } catch (e: ClassCastException) {
            throw ClassCastException("Error")
        }
    }

    override fun onDestroyView() {
        mView = null
        super.onDestroyView()
    }
}

class MyPicker(private val edt : EditText,private var shdl : ScheduleItem ,private val isStart : Boolean, context : Context,listener :  OnTimeSetListener, hourOfDay : Int, minute : Int, is24HourView : Boolean)
    : TimePickerDialog(context, 2, listener, hourOfDay, minute, is24HourView){
    override fun show() {
        super.show()

        getButton(BUTTON_NEGATIVE)?.text = edt.context.getString(R.string.cancel)
        getButton(BUTTON_POSITIVE)?.text = edt.context.getString(R.string.ok)
    }

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        super.onTimeChanged(view, hourOfDay, minute)
        val mHour = String.format("%02d", hourOfDay)
        val mMinute = String.format("%02d", minute)
        edt.setText("$mHour:$mMinute")
        if (isStart)
            shdl.start_at_time = "$mHour:$mMinute"
        else
            shdl.expires_at_time = "$mHour:$mMinute"
    }
}