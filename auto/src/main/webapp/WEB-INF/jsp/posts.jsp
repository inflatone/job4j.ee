<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="common/resources.jsp"/>
<body>

<script type="text/javascript" src="static/js/common/basic.js" defer></script>
<script type="text/javascript" src="static/js/common/form.js" defer></script>
<script type="text/javascript" src="static/js/common/list.js" defer></script>
<script type="text/javascript" src="static/js/common/image.js" defer></script>

<script type="text/javascript" src="static/js/common/post.js" defer></script>
<script type="text/javascript" src="static/js/listPost.js" defer></script>

<div class="container-fluid container-page">
    <jsp:include page="common/header.jsp"/>

    <div class="listing-row__title">Posts</div>
    <br>

    <div class="row content">
        <div class="col-sm-8" style="width: 100%">
            <div class="row flex-row" style="width: 100%">
                <table class="table table-hover" id="postTable">
                    <thead class="thead-middle">
                    <tr>
                        <th class="active">Preview</th>
                        <th>Review</th>
                    </tr>
                    </thead>
                </table>

            </div>
        </div>
        <div class="col-sm-4 sidenav">
            <div class="card" id="filter-data">
                <div class="data-body" style="padding: 0 !important;">
                    <form id="filterForm" style="margin-bottom: 0">
                        <table id="table-profile" class="table" style="margin-bottom: 0;">
                            <tr>
                                <th colspan="2"><div class="h4 listing-row__title">
                                    <strong>Filters</strong></div>
                                </th>
                            </tr>
                            <tr>
                                <td colspan="2" style="border-top-width: 3px; border-top-color: dimgray;">
                                    <b style="font-size: 1.1rem"><u>Car features:</u></b>
                                </td>
                            </tr>
                            <tr>
                                <td style="border-top-width: 3px; border-top-color: dimgray;" class="top-border-strong"><label for="filterVendor" class="col-form-label" ><b>Vendor:</b></label></td>
                                <td style="border-top-width: 3px; border-top-color: dimgray;" class="form-group" title="Vendor" >
                                    <div>
                                        <select class="form-control vendor-id" name="vendor" id="filterVendor">
                                            <option>Vendor</option>
                                        </select>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td><label for="filterBody" class="col-form-label"><b>Body:</b></label></td>
                                <td class="form-group" title="Body">
                                    <div>
                                        <select class="form-control body-id" name="body" id="filterBody">
                                            <option>Body</option>
                                        </select>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td><label for="filterEngine" class="col-form-label"><b>Engine:</b></label></td>
                                <td class="form-group" title="Engine">
                                    <div>
                                        <select class="form-control engine-id" name="engine" id="filterEngine">
                                            <option>Engine</option>
                                        </select>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td><label for="filterTransmission" class="col-form-label"><b>Transmission:</b></label>
                                </td>
                                <td class="form-group" title="Transmission">
                                    <div>
                                        <select class="form-control transmission-id" name="transmission" id="filterTransmission">
                                            <option>Transmission</option>
                                        </select>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" style="border-bottom: none; padding-bottom: 0">
                                    <b>Assembled between:</b>
                                </td>
                            </tr>
                            <tr style="margin-bottom: 0; ">
                                <td style="padding-top: 0; margin-bottom: 0; border-top: none">
                                    <label for="yearMin" class="col-form-label" style="margin-bottom: 0; padding-bottom: 0"><i>From:</i></label>
                                    <input type="text" class="form-control" name="year.min" id="yearMin">

                                </td>
                                <td style="padding-top: 0; margin-bottom: 0; border-top: none">
                                    <label for="yearMax" class="col-form-label" style="margin-bottom: 0; padding-bottom: 0"><i>To:</i></label>
                                    <input type="text" class="form-control" name="year.max" id="yearMax">

                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" style="border-top-width: 3px; border-top-color: dimgray;">
                                    <b style="font-size: 1.1rem"><u>Post features:</u></b>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" style="border-top-width: 3px; border-top-color: dimgray; border-bottom: none; padding-bottom: 0">
                                    <b>Posted between:</b>
                                </td>
                            </tr>
                            <tr style="margin-bottom: 0; ">
                                <td style="padding-top: 0; margin-bottom: 0; border-top: none">
                                    <label for="filterPostedMin" class="col-form-label" style="margin-bottom: 0; padding-bottom: 0"><i>From:</i></label>
                                    <input class="form-control" name="posted.min" id="filterPostedMin">

                                </td>
                                <td style="padding-top: 0; margin-bottom: 0; border-top: none">
                                    <label for="filterPostedMax" class="col-form-label" style="margin-bottom: 0; padding-bottom: 0"><i>To:</i></label>
                                    <input class="form-control" name="posted.max" id="filterPostedMax">
                                </td>
                            </tr>

                            <tr>
                                <td colspan="2" style="border-bottom: none; padding-bottom: 0">
                                    <b>Price between:</b>
                                </td>
                            </tr>
                            <tr style="margin-bottom: 0; ">
                                <td style="padding-top: 0; margin-bottom: 0; border-top: none">
                                    <label for="filterPriceMin" class="col-form-label" style="margin-bottom: 0; padding-bottom: 0"><i>From:</i></label>
                                    <input type="number" min="0" step="1000" class="form-control" name="price.min" id="filterPriceMin">

                                </td>
                                <td style="padding-top: 0; margin-bottom: 0; border-top: none">
                                    <label for="filterPriceMax" class="col-form-label" style="margin-bottom: 0; padding-bottom: 0"><i>To:</i></label>
                                    <input type="number" min="0" step="1000" class="form-control" name="price.max" id="filterPriceMax">
                                </td>
                            </tr>
                            <tr>
                                <td style="padding-top: 5%; margin-bottom: 0;"><b>With image:</b></td>
                                <td class="form-group" title="With image" style="vertical-align: center; margin-bottom: 0;">
                                    <label class="custom-control custom-checkbox" title="Image">
                                        <input class="custom-control-input" type="checkbox" name="image">
                                        <span class="custom-control-indicator"></span>
                                        <span class="custom-control-label"></span>
                                    </label>
                                </td>
                            </tr>
                        </table>
                    </form>
                    <div class="btn-group special btn-group-sm btn-block header-group">
                        <button id="filterButton" onclick="doFilter($(this))" class="btn btn-outline-dark">
                            <span class="fa fa-refresh"></span>
                            Filter
                        </button>
                        <button id="clearButton" onclick="resetFilter($(this))" class="btn btn-outline-danger">
                            <span class="fa fa-undo"></span> Reset
                        </button>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>

<div class="modal fade" tabindex="-1" id="modalPostCard">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title listing-row__title" style="font-size: 1.5em"></h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">

            </div>
        </div>
    </div>
</div>

<jsp:include page="common/formPost.jsp"/>

</body>
</html>