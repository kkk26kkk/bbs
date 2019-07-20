<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="resources/css/board.css" />
</head>
<body>
<div id="wrap">
	<div id="tb-wrap">
		<div>
			<a href="user/login">로그인</a>
			
			<c:if test="${sessionScope.userId != null }">
					<span>아이디 : ${userId }</span>
					<a href="board/board_write">글쓰기</a>
					<a href="user/logout">로그아웃</a>
			</c:if>
		</div>
		<table class="table table-striped">
			<tr>
				<th>번호</th>
				<th>제목</th>
				<th>작성자</th>
				<th>작성일</th>
			</tr>
			<c:forEach var="board" items="${boardList }">
				<tr>
					<td>${board.idx }</td>
					<td><a href="board/${board.idx }">${board.title }</a></td>
					<td>${board.userName }</td>
					<td>${board.regDtm }</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>
</body>
</html>