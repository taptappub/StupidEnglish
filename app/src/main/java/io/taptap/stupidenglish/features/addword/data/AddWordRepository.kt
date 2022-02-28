package io.taptap.stupidenglish.features.addword.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.features.alarm.ui.AlarmScheduler
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWordRepository @Inject constructor(
    private val wordDao: WordDao,
    private val alarmScheduler: AlarmScheduler
) {

    suspend fun saveWord(word: String, description: String, groupsIds: List<Long>): Reaction<Long> = Reaction.on {
        wordDao.insertWord(WordDto(word = word, description = description, points = 0, groupsIds = groupsIds.joinToString(",")))
            .also { alarmScheduler.enableNotifications() } // todo не забудь удалять нотификации, когда добавишь удаление слов. И поищи ALARM_STOP
    }
}