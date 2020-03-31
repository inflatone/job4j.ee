<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form action="logout" method="post">
    <div class="header-group btn-group d-flex" role="group">
        <button id="addPost" type="button" onclick="openPostCreateForm()" class="btn btn-outline-secondary">
            <span class="fa fa-plus"></span>
            Add post
        </button>
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
        <a class="btn btn-outline-secondary" href="posts">
            <span class="fa fa-list-alt"></span>
            Posts
        </a>
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