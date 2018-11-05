package com.webstersys.geofence.geofence

import com.google.android.gms.location.Geofence


class GeofenceModel private constructor(
    val requestId: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val expiration: Long,
    val transition: Int,
    val loiteringDelay: Int
) {

    fun toGeofence(): Geofence {
        return Geofence.Builder()
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(expiration)
            .setRequestId(requestId)
            .setTransitionTypes(transition)
            .setLoiteringDelay(loiteringDelay)
            .build()
    }

    class Builder(private val requestId: String) {
        private var latitude: Double = 0.toDouble()
        private var longitude: Double = 0.toDouble()
        private var radius: Float = 0.toFloat()
        private var expiration: Long = 0
        private var transition: Int = 0
        private var loiteringDelay: Int = 0

        fun setLatitude(latitude: Double): Builder {
            this.latitude = latitude
            return this
        }

        fun setLongitude(longitude: Double): Builder {
            this.longitude = longitude
            return this
        }

        fun setRadius(radius: Float): Builder {
            this.radius = radius
            return this
        }

        fun setExpiration(expiration: Long): Builder {
            this.expiration = expiration
            return this
        }

        fun setTransition(transition: Int): Builder {
            this.transition = transition
            return this
        }

        fun setLoiteringDelay(loiteringDelay: Int): Builder {
            this.loiteringDelay = loiteringDelay
            return this
        }

        fun build(): GeofenceModel {
            return GeofenceModel(
                requestId,
                latitude,
                longitude,
                radius,
                expiration,
                transition,
                loiteringDelay
            )
        }
    }
}