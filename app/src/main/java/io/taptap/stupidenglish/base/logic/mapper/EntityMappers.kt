package io.taptap.stupidenglish.base.logic.mapper

import io.taptap.stupidenglish.archive.features.sentences.ui.SentencesListItemUI
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.base.model.Sentence
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.words.ui.model.WordListItemUI
import io.taptap.uikit.group.GroupItemUI
import io.taptap.uikit.group.GroupListItemsModel
import io.taptap.uikit.group.NoGroup

fun List<Group>.toGroupsList(withNoGroup: Boolean): List<GroupListItemsModel> {
    val groupList = mutableListOf<GroupListItemsModel>()
    if (withNoGroup) {
        groupList.add(NoGroup)
    }

    groupList.addAll(this.map {
        it.toGroupItemUI()
    })

    return groupList
}

fun Group.toGroupItemUI(): GroupItemUI = GroupItemUI(id, name)

fun List<Word>.toWordsList(): List<WordListItemUI> {
    return this.map {
        WordListItemUI(
            id = it.id,
            word = it.word,
            description = it.description
        )
    }
}

fun List<Sentence>.toSentenceList(): List<SentencesListItemUI> {
    return this.map {
        SentencesListItemUI(
            id = it.id,
            sentence = it.sentence
        )
    }
}

//TODO перенеси сюда все мапперы из VM, а потом GroupViewModelhelper удали