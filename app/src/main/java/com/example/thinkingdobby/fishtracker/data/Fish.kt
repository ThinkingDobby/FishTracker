package com.example.thinkingdobby.fishtracker.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "Fish")
class Fish(@PrimaryKey var id: Long?,
        @ColumnInfo(name = "fishName") var fishName: String?,
        @ColumnInfo(name = "date") var date: String?,
        @ColumnInfo(name = "location") var location: String?,
        @ColumnInfo(name = "info") var info: String?,
        @ColumnInfo(name = "count") var count: Int?,
        @ColumnInfo(name = "size") var size: Int?,
        @ColumnInfo(name = "fishNo") var fishNo: Int?,
        @ColumnInfo(name = "imgOt") var imgOt: Int?,
        @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB) var image: ByteArray?) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.createByteArray()) {
    }

    constructor() : this(null, "", "정보 없음", "정보 없음", "", 0, 0, null,null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(fishName)
        parcel.writeString(date)
        parcel.writeString(location)
        parcel.writeString(info)
        parcel.writeValue(count)
        parcel.writeValue(size)
        parcel.writeValue(fishNo)
        parcel.writeValue(imgOt)
        parcel.writeByteArray(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Fish> {
        override fun createFromParcel(parcel: Parcel): Fish {
            return Fish(parcel)
        }

        override fun newArray(size: Int): Array<Fish?> {
            return arrayOfNulls(size)
        }
    }

}