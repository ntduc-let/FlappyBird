package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
data class Cloud(
    var id: Int,
    var cloudRes: Int,
    var cloudX: ArrayList<Int> = arrayListOf(),
    var cloudY: ArrayList<Int> = arrayListOf(),
    var cloudVelocity: Int = 6                  //Vận tốc cloud
) : Parcelable