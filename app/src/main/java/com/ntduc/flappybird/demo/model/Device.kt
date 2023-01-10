package com.ntduc.flappybird.demo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Device(
    var width: Int = 0,
    var height: Int = 0
) : Parcelable