package ru.edinros.agitator.core.data

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.edinros.agitator.core.local.entities.TaskEntity

@Serializable
data class TaskListResponse(
    val ok: Boolean,
    val result: List<AgitatorTask>
)

@Serializable
@Parcelize
data class RejectedReportsInfo(
    val link_facebook: String?= null,
    val link_instagram: String?=null,
    val link_vk: String?=null,
    val link_odnoklassniki: String?=null,
    val reason: String,
    val screenshots: List<String>?= emptyList(),
    val time: Int,
    val uid: String
) : Parcelable

@Serializable
data class AgitatorTask(
    val id: Int,
    val accept_before: Int,                  //Время, до которого нужно принять задание, в Unix-time, точность до секунды.
    val accepted_at: Int?,                   //Время принятия в Unix-time, точность до секунды. Если =null, значит задание не принято.
    val assigned_at: Int,                    //Время постановки задачи в Unix-time, точность до секунды.
    val attachments: List<TaskAttachment>?,
    val complete_before: Int,                //Время, до которого нужно завершить задание, в Unix-time, точность до секунды.
    val completed_at: Int?,                  //Время завершения в Unix-time, точность до секунды. Если =null, значит задание не завершено.
    val is_expired: Boolean,                 //задание просрочено
    val maximum_number_of_reports: Int,      //максимальное количество отчетов
    val report_items: List<String>,          //"file" "link" "screenshot" "text"
    val resources: List<String>?,            //"facebook" "instagram" "odnoklassniki" "telegram" "vk" "whatsapp","telegram","viber"
    val description: String?,
    val text: String,
    val type: TaskType,
    val links: List<LinkInfo>?,
    @SerialName("progress")
    val reportsStatus: TaskReportStatus?
) {
    fun toEntity() = TaskEntity(
        id = id,
        accept_before = accept_before,
        accepted_at = accepted_at,
        assigned_at = assigned_at,
        complete_before = complete_before,
        completed_at = completed_at,
        is_expired = is_expired,
        maximum_number_of_reports = maximum_number_of_reports,
        report_items = report_items,
        resources = resources ?: emptyList(),
        text = text,
        type = type,
        links = links ?: emptyList(),
        description = description ?: "",
        attachments = attachments ?: emptyList(),
        accepted = reportsStatus?.accepted ?: emptyList(),
        rejected = reportsStatus?.rejected ?: emptyList(),
        sent = reportsStatus?.new ?: emptyList(),
        rejected_detailed = reportsStatus?.rejected_detailed ?: emptyList()
    )
}

/**
 * Текущий статус отчетов для поручения
 */
@Serializable
data class TaskReportStatus(
    val accepted: List<String>?,                        //подтверждённые
    val rejected: List<String>?,                        //отклоненные
    val new: List<String>?,                            //отправленные на проверку
    val rejected_detailed: List<RejectedReportsInfo>? = emptyList()
)

/**
 * Вложения к поручению
 * @see AttachmentType
 */
@Serializable
@Parcelize
data class TaskAttachment(
    val description: String?,
    val title: String,
    val url: String,
    val type: AttachmentType
) : Parcelable

@Parcelize
@Serializable
data class LinkInfo(val description: String, val url: String) : Parcelable

@Serializable
data class AcceptOrCompleteTaskResult(
    val ok: Boolean,
    val is_task_already_accepted: Boolean? = null,
    val is_too_late_to_accept_task: Boolean? = null,
    val is_task_completed: Boolean? = null,
    val is_task_expired: Boolean? = null,
    val is_task_not_found: Boolean? = null,
    val is_not_interviewer: Boolean? = null,
    val accepted_at: Int? = null,
    val completed_at: Int? = null
)

/**
 * Типы вложений(картинка, видео) для поручения
 */
enum class AttachmentType {
    video,
    image
}

/**
 *  Тип задания
 */
enum class TaskType {
    comment,//комментировать
    like,//поставить лайки
    post,//разместить пост
    repost,//репост
    undefined //неопределено
}

enum class TaskStatus(@ColorRes val color: Int, val text: String, val filterText: String) {
    Unknown(-1, "Все", "Все"),
    New(-1, "Новое", "Новые"),
    InWork(-1, "В работе", "В работе"),
    Completed(-1, "Завершено", "Завершённые"),
    Expired(-1, "Просрочено", "Просроченные")
}
