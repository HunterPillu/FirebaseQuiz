package com.prinkal.quiz.utils

object Const {


    const val SCREEN_USER_INVITE = 203
    const val SCREEN_COURSE = 202
    const val SCREEN_USER = 201
    const val SCREEN_BOOKMARK = 204
    const val EXTRA_TYPE: String = "extra_type"


    const val ITEM_3 = 3
    const val SETTING_USER: Int = 2
    const val COURSE_HEADER: Int = 1
    const val COURSE_ITEM: Int = 0
    const val VIDEO_ID: String = "videoId"
    const val CATEGORY_ID: String = "categoryId"
    const val COURSE_SELECT = "course_selected"
    const val VIDEO_MODEL = "video_model"

    const val FB_ERROR = "Firebase_Error"


    //EVENT TYPE
    const val TYPE_PAGINATION = 8
    const val TYPE_CLICKED_3 = 7
    const val TYPE_CLICKED_2 = 6
    const val TYPE_CLICKED = 5
    const val TYPE_BOOKMARK = 4
    const val TYPE_DELETE: Int = 3
    const val TYPE_EDIT: Int = 2
    const val TYPE_ADD: Int = 1


    //FIRESTORE TABLE
    const val TABLE_REFERENCE = "reference"
    const val TABLE_QUESTION = "question"
    const val TABLE_USERS = "users"
    const val TABLE_QUIZ = "quiz"
    const val TABLE_COURSE = "course"
    const val TABLE_ROOM = "room"

    //FIRESTORE SUB-COLLECTION
    const val SUB_BOOKMARK_VIDEOS = "bookmark_videos"

    // PAGINATION
    const val LIMIT: Long = 30

    const val LIMIT_VIDEOS: Long = 10


    //Game status
    const val STATUS_IDLE = 0
    const val STATUS_REJECT = 1
    const val STATUS_WAITING = 2
    const val STATUS_IN_GAME = 3
    const val STATUS_INVITATION_RECEIVED = 4
    const val STATUS_ACCEPTED = 5

    const val CAN_REQUEST_IF_OFFLINE = true
}