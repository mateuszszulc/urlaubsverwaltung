<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title><spring:message code="title"/></title>
    <%@include file="../include/header.jsp" %>
</head>
<body>

<spring:url var="formUrlPrefix" value="/web"/>

<%@include file="../include/menu_header.jsp" %>

<div id="content">

    <div class="container_12">

        <div class="grid_12">

            <div class="grid_6">

                <div class="overview-header">
                    <legend>
                        <p><spring:message code="sicknote" /></p>
                        <sec:authorize access="hasRole('role.office')">
                            <c:if test="${sickNote.active}">
                                <a href="#modal-cancel" role="button" class="btn btn-right" data-toggle="modal">
                                    <i class="icon-trash"></i>&nbsp;<spring:message code="delete" />
                                </a>
                                <a class="btn btn-right" href="${formUrlPrefix}/sicknote/${sickNote.id}/convert"><i class="icon-random"></i>&nbsp;<spring:message code="sicknotes.convert.vacation.short" /></a>
                                <a class="btn btn-right" href="${formUrlPrefix}/sicknote/${sickNote.id}/edit"><i class="icon-pencil"></i>&nbsp;<spring:message code="edit" /></a>
                            </c:if>
                        </sec:authorize>
                    </legend>
                </div>

                <form:form method="POST" action="${formUrlPrefix}/sicknote/${sickNote.id}/cancel">
                <div id="modal-cancel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h3 id="myModalLabel"><spring:message code="sicknote" />&nbsp;<spring:message code="delete" />?</h3>
                    </div>
                        <div class="modal-body">
                            <spring:message code="sicknote.cancel" />
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-danger" type="submit"><i class="icon-trash icon-white"></i>&nbsp;<spring:message code="delete" /></button>
                            <button class="btn" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i>&nbsp;<spring:message code="cancel" /></button>
                        </div>
                </div>
                </form:form>
                
              <table class="app-detail">
                  <tbody>
                      <tr class="odd">
                          <td><spring:message code="name" /></td>
                          <td><c:out value="${sickNote.person.firstName}" />&nbsp;<c:out value="${sickNote.person.lastName}" /></td>
                      </tr>
                      <tr class="even">
                          <td><spring:message code="sicknotes.time" /></td>
                          <td><joda:format style="M-" value="${sickNote.startDate}"/>&nbsp;-&nbsp;<joda:format style="M-" value="${sickNote.endDate}"/></td>
                      </tr>
                      <tr class="odd">
                          <td><spring:message code="work.days" /></td>
                          <td><fmt:formatNumber maxFractionDigits="1" value="${sickNote.workDays}" /></td>
                      </tr>
                      <tr class="even">
                          <td><spring:message code="sicknotes.aub" /></td>
                          <td>
                              <c:choose>
                                  <c:when test="${sickNote.aubPresent}">
                                    <i class="icon-ok"></i> 
                                  </c:when>
                                  <c:otherwise>
                                    <i class="icon-remove"></i>
                                  </c:otherwise>
                              </c:choose>
                          </td>
                      </tr>
                      <tr class="odd">
                          <td><spring:message code="sicknotes.aub.time" /></td>
                          <td><joda:format style="M-" value="${sickNote.aubStartDate}"/>&nbsp;-&nbsp;<joda:format style="M-" value="${sickNote.aubEndDate}"/></td>
                      </tr>
                      <tr class="even">
                          <td><spring:message code="app.date.overview" /></td>
                          <td><joda:format style="M-" value="${sickNote.lastEdited}"/></td>
                      </tr>
                  </tbody>
              </table>  
            </div>

            <div class="grid_6">

                <div class="overview-header">
                    <legend>
                        <p><spring:message code="progress" /></p>
                        <sec:authorize access="hasRole('role.office')">
                            <button class="btn" style="float:right;" onclick="$('div#comment-form').show();">
                                <i class="icon-comment"></i>&nbsp;Neuer Kommentar
                            </button>
                        </sec:authorize>
                    </legend>
                </div>
                
                <table class="app-detail">
                    <tbody>
                        <c:forEach items="${sickNote.comments}" var="comment" varStatus="loopStatus">
                            <tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
                                <td style="width:10%"><joda:format style="M-" value="${comment.date}"/></td>
                                <td style="width:10%">
                                    <c:out value="${comment.person.firstName}" />&nbsp;<c:out value="${comment.person.lastName}" />
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty comment.text}">
                                            <spring:message code="${comment.status.messageKey}" /> 
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${comment.text}" />
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <sec:authorize access="hasRole('role.office')">
                
                    <c:choose>
                        <c:when test="${error}">
                           <c:set var="STYLE" value="display: block" /> 
                        </c:when>
                        <c:otherwise>
                            <c:set var="STYLE" value="display: none" /> 
                        </c:otherwise>
                    </c:choose>
                    
                    <div id="comment-form" style="${STYLE}">
                        <form:form method="POST" action="${formUrlPrefix}/sicknote/${sickNote.id}" modelAttribute="comment">
                            <form:errors path="text" cssClass="error" /><br />
                            <span id="text-comment"></span><spring:message code="max.chars" />
                            <form:textarea style="width: 100%;" rows="4" path="text" cssErrorClass="form-textarea error" onkeyup="count(this.value, 'text-comment');" onkeydown="maxChars(this,200); count(this.value, 'text-comment');" />
                            <br />
                            <button class="btn" type="submit">
                                <i class="icon-ok"></i>&nbsp;<spring:message code="save" />
                            </button>
                            <button class="btn" type="button" onclick="$('div#comment-form').hide();">
                                <i class="icon-remove"></i>&nbsp;<spring:message code="cancel" />
                            </button>
                        </form:form> 
                    </div>
                
                </sec:authorize>
                
            </div>

        </div>

    </div>

</div>

</body>
</html>