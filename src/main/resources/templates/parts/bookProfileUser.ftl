<form enctype="multipart/form-data">
    <div><label>Название книги: ${book.name}</label></div>
    <div><label>Автор: ${book.author}</label></div>
    <div><label>Описание: <#if book.descriptions??>${book.descriptions}</#if></label></div>
    <#if book.filename??>
        <div>
            <a href="/book/download/${book.filename}">Скачать</a>
        </div>
    <#else>
        <div>No file</div>
    </#if>
</form>