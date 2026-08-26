package com.raulastete.aura.core.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.raulastete.aura.core.database.converter.FloatListTypeConverter
import com.raulastete.aura.core.database.dao.RecordDao
import com.raulastete.aura.core.database.entity.RecordEntity
import com.raulastete.aura.core.database.entity.RecordTopicCrossRef
import com.raulastete.aura.core.database.entity.TopicEntity

@Database(
    entities = [RecordEntity::class, TopicEntity::class, RecordTopicCrossRef::class],
    version = 1,
)
@ColumnTypeConverters(
    FloatListTypeConverter::class
)
abstract class AuraDatabase: RoomDatabase() {
    abstract val recordDao: RecordDao
}