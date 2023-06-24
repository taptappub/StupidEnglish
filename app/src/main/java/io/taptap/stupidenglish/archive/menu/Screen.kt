package io.taptap.stupidenglish.archive.menu


//if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
//    val keyboardController = LocalSoftwareKeyboardController.current
//    DisposableEffect(Unit) {
//        onDispose {
//            when (state.sheetContentType) {
//                WordListContract.SheetContentType.AddGroup ->
//                    onEventSent(WordListContract.Event.OnGroupAddingCancel)
//
//                WordListContract.SheetContentType.Motivation ->
//                    onEventSent(WordListContract.Event.OnMotivationCancel)
//
//                WordListContract.SheetContentType.GroupMenu ->
//                    onEventSent(WordListContract.Event.OnGroupMenuCancel)
//            }
//            keyboardController?.hide()
//        }
//    }
//}

//ModalBottomSheetLayout(
//    sheetState = modalBottomSheetState,
//sheetContent = {
//    MenuBottomSheet(
//        list = state.groupMenuList,
//        onClick = {
//            onEventSent(WordListContract.Event.OnGroupMenuItemClick(it))
//        },
//        titleRes = R.string.word_group_menu_title,
//        modifier = Modifier
//            .fillMaxWidth()
//            .animateContentSize()
//    )
//}