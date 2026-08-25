package com.raulastete.aura.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.res.stringResource
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import com.raulastete.aura.MainActivity
import com.raulastete.aura.R
import com.raulastete.aura.navigation.ACTION_CREATE_RECORD

class RecordWidgetReceiver : GlanceAppWidgetReceiver(){
    override val glanceAppWidget: GlanceAppWidget
        get() = RecordWidget()
}
class RecordWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val recordNewEmotion = context.getString(R.string.record_new_emotion)
        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .clickable{
                            val intent = Intent(context, MainActivity::class.java).also {
                                it.data = "https://aura.com/records/true".toUri()
                                it.action = ACTION_CREATE_RECORD
                            }
                            val pendingIntent = TaskStackBuilder
                                .create(context)
                                .addNextIntentWithParentStack(intent)
                                .getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)

                            pendingIntent?.send()
                        }
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.widget),
                        contentDescription = recordNewEmotion
                    )
                }
            }
        }
    }


}