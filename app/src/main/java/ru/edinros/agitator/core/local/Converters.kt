package ru.edinros.agitator.core.local


import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.edinros.agitator.core.data.*

class RejectedReportsConverter {
    @TypeConverter
    fun fromDb(value: String): List<RejectedReportsInfo> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toDb(value: List<RejectedReportsInfo>): String {
        return Json.encodeToString(value)
    }
}

class TaskLinksConverter {
    @TypeConverter
    fun fromDb(value: String): List<LinkInfo> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toDb(value: List<LinkInfo>): String {
        return Json.encodeToString(value)
    }

}

class TaskTypeConverter {
    @TypeConverter
    fun fromDb(value: String): TaskType =
        when (value) {
            TaskType.comment.name -> TaskType.comment
            TaskType.post.name -> TaskType.post
            TaskType.repost.name -> TaskType.repost
            TaskType.like.name -> TaskType.like
            else -> TaskType.undefined
        }


    @TypeConverter
    fun toDb(value: TaskType): String {
        return value.name
    }
}

class ListOfStringConverter {
    @TypeConverter
    fun fromDb(value: String): List<String> =
        Json.decodeFromString(value)

    @TypeConverter
    fun toDb(value: List<String>): String {
        return Json.encodeToString(value)
    }

}

class AttachmentTypeConverter {
    @TypeConverter
    fun fromDb(value: String): AttachmentType =
        when (value) {
            AttachmentType.image.name -> AttachmentType.image
            AttachmentType.video.name -> AttachmentType.video
            else -> throw Throwable("Такие типы не поддерживаются")

        }


    @TypeConverter
    fun toDb(value: AttachmentType): String {
        return value.name
    }

}
class TaskAttachmentsConverter {
    @TypeConverter
    fun fromDb(value: String): List<TaskAttachment> {
        return Json.decodeFromString(value)
    }
    @TypeConverter
    fun toDb(value: List<TaskAttachment>): String {
        return Json.encodeToString(value)
    }

}