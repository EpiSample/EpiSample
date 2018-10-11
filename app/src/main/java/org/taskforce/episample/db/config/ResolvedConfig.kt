package org.taskforce.episample.db.config

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import org.taskforce.episample.config.sampling.SamplingMethodEntity
import org.taskforce.episample.core.models.MapboxStyleUrl
import org.taskforce.episample.db.config.landmark.CustomLandmarkType
import org.taskforce.episample.db.converter.DateConverter
import org.taskforce.episample.config.sampling.ResolvedSamplingMethodEntity
import org.taskforce.episample.db.config.customfield.CustomField
import java.util.*

@TypeConverters(DateConverter::class)
class ResolvedConfig(var name: String,
                     @ColumnInfo(name = "date_created")
                     val dateCreated: Date = Date(),
                     @ColumnInfo(name = "mapbox_style")
                     val mapboxStyleString: String,
                     val id: String) {

    val mapboxStyle
        get() = MapboxStyleUrl(mapboxStyleString)

    @Embedded()
    lateinit var adminSettings: AdminSettings

    @Embedded()
    lateinit var enumerationSubject: EnumerationSubject

    @Embedded()
    lateinit var userSettings: UserSettings

    @Embedded()
    lateinit var displaySettings: DisplaySettings

    @Relation(parentColumn = "id", entityColumn = "config_id")
    lateinit var customFields: List<CustomField>

    @Relation(parentColumn = "id", entityColumn = "config_id")
    lateinit var customLandmarkTypes: List<CustomLandmarkType>

    @Relation(entity = EnumerationArea::class, parentColumn = "id", entityColumn = "config_id")
    lateinit var enumerationAreas: List<ResolvedEnumerationArea>

    @Relation(entity = SamplingMethodEntity::class, parentColumn = "id", entityColumn = "config_id")
    lateinit var methodologies: List<ResolvedSamplingMethodEntity>

    val methodology: ResolvedSamplingMethodEntity
        get() = methodologies.first()
}

@Dao
abstract class ResolvedConfigDao {
    @Query("SELECT * from config_table c INNER JOIN display_settings_table ds ON ds.display_settings_config_id = c.id INNER JOIN user_settings_table us ON us.user_settings_config_id = c.id INNER JOIN admin_settings_table a ON a.admin_settings_config_id = c.id INNER JOIN enumeration_subject_table e ON e.enumeration_subject_config_id = c.id WHERE c.id LIKE :configId")
    abstract fun getConfig(configId: String): LiveData<ResolvedConfig>

    @Query("SELECT * from config_table c INNER JOIN display_settings_table ds ON ds.display_settings_config_id = c.id INNER JOIN user_settings_table us ON us.user_settings_config_id = c.id INNER JOIN admin_settings_table a ON a.admin_settings_config_id = c.id INNER JOIN enumeration_subject_table e ON e.enumeration_subject_config_id = c.id WHERE c.id LIKE :configId")
    abstract fun getConfigSync(configId: String): ResolvedConfig
}