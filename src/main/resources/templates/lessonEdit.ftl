<#import "parts/common.ftl" as c>
    <#import "parts/newSubGroup.ftl" as nsg>

<@c.page>
    <label>${lesson.name}</label>

    <table>
        <tbody>
        <@nsg.newSubGroup "${lesson.id}"/>
        <#list subGroups as subGroup>
            <tr>
                <td><a href="/subGroup/${subGroup.id}">${lesson.name}</a> </td>
<#--                <td><a href="/subGroup/${subGroup.id}"/>Edit</td>-->
                <td><a href="/lesson/${lesson.id}/delete/${subGroup.id}"/>Delete</td>

            </tr>
        </#list>
        </tbody>
    </table>

    <input type="hidden" value="${lesson.id}" name="userId">
    <button type="submit" class="btn btn-primary">Save</button>

</@c.page>