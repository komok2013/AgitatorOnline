package ru.edinros.agitator.core.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

//@Entity
//@Parcelize
//data class TaskEntity(
//    @PrimaryKey
//    val id: Int,
//    val accept_before: Int,
//    val accepted_at: Int?,
//    val assigned_at: Int,
//    val complete_before: Int,
//    val completed_at: Int?,
//    val is_expired: Boolean,
//    val maximum_number_of_reports: Int,
//    val report_items: List<String> = emptyList(),
//    val resources: List<String>? = emptyList(),
//    val description: String,
//    val text: String,
//    val type: TaskType,
//    val links: List<LinkInfo> = emptyList(),
//    val reports_count: Int = 0,
//    val accepted: List<String> = emptyList(),
//    val rejected: List<String> = emptyList(),
//    val sent: List<String> = emptyList(),
//    val attachments:List<TaskAttachment> = emptyList(),
//    val rejected_detailed:List<RejectedReportsInfo> = emptyList()
//) : Parcelable {
//    fun checkStatus() = when {
//        accepted_at == null && currentTimeInSec() < accept_before -> TaskStatus.New
//        accepted_at == null && currentTimeInSec() > accept_before -> TaskStatus.Expired
//        completed_at != null -> TaskStatus.Completed
//        accepted_at != null && currentTimeInSec() < complete_before -> TaskStatus.InWork
//        accepted_at != null && currentTimeInSec() > complete_before -> TaskStatus.Expired
//        is_expired -> TaskStatus.Expired
//        else -> TaskStatus.Unknown
//    }
//}
//@Entity
//data class ReportEntity(
//    @PrimaryKey val uuid: String,
//    val task_id: Int,
//    val sent: Boolean = false,
//    val images:List<MediaFile> = emptyList(),
//    val link_vk:String = "",
//    val link_facebook:String = "",
//    val link_odnoklassniki:String = "",
//    val link_instagram:String = ""
//)