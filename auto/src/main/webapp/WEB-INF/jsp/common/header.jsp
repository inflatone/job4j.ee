<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form action="logout" method="post">
    <div class="header-group btn-group d-flex" role="group">
        <sec:authorize access="hasAuthority('ADMIN')">
            <button id="addUser" type="button"  class="btn btn-outline-dark" onclick="openUserCreateForm()" hidden>
                <span class="fa fa-plus"></span>
                Add user
            </button>
            <a class="btn btn-outline-dark" href="users">
                <span class="fa fa-list"></span>
                Users
            </a>
        </sec:authorize>
        <a class="btn btn-outline-secondary" href="profile">
            <span class="fa fa-user"></span>
            Profile
        </a>

        <button class="btn btn-outline-danger" type="submit">
            <span class="fa fa-sign-out"></span>
            Logout
        </button>
    </div>
</form:form>

<script type="text/javascript">
    const profileId = "${profileId}";
    const editable = '${editable}' === 'true';

    const modPostAjaxUrl = '${modPostUrl}';

    const modImageProfileUrl = '${modImageProfileUrl}';
    const modImagePostUrl = '${modImagePostUrl}';
</script>