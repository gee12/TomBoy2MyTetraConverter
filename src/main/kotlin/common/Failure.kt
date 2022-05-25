package common


sealed class Failure(val exception: Throwable?) {
    class Convert(exception: Throwable?) : Failure(exception)
    sealed class Tomboy(exception: Throwable?) : Failure(exception) {
        class ParseNoteXml(exception: Throwable?) : Tomboy(exception)
    }
    sealed class Mytetra(exception: Throwable?) : Failure(exception) {
        class SaveStorageXml(exception: Throwable?) : Mytetra(exception)
        class SaveRecordHtml(exception: Throwable?) : Mytetra(exception)
    }
}