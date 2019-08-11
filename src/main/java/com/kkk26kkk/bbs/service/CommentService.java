package com.kkk26kkk.bbs.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kkk26kkk.bbs.dao.CommentDao;
import com.kkk26kkk.bbs.model.Comment;
import com.kkk26kkk.bbs.model.CommentDto;
import com.kkk26kkk.bbs.model.CommentVo;
import com.kkk26kkk.bbs.model.User;
import com.kkk26kkk.common.model.PageListParam;

@Service
public class CommentService {
	@Autowired
	private CommentDao commentDao;
	
	public int insertComment(CommentDto commentDto, User user) throws SQLException {
		CommentVo commentVo = new CommentVo();
		commentVo.setUserId(user.getUserId());
		commentVo.setUserName(user.getUserName());
		commentVo.setArticleId(commentDto.getArticleId());
		commentVo.setContents(commentDto.getContents());
		
		int resultCnt = commentDao.insertComment(commentVo);
		if(1 != resultCnt) {
			throw new SQLException("��� ����� ���� �߽��ϴ�.");
		}
		
		int currentCommentId = getCurrentCommentId();
		
		return currentCommentId;
	}
	
	private int getCurrentCommentId() {
		return commentDao.getCurrentCommentId();
	}
	
	public List<Comment> getCommentList(int articleId) {
		// XXX �Ķ���͸� �������� �������ϴ°�?
//		CommentDto commentDto = new CommentDto();
//		commentDto.setArticleId(articleId);
//		commentDto.setStartNum(pageListParam.getStartNum());
//		commentDto.setEndNum(pageListParam.getEndNum());
//		
//		return commentDao.getCommentList(commentDto);
		return commentDao.getCommentList(articleId);
	}

	public Comment getComment(int commentId) {
		return commentDao.getComment(commentId);
	}
}
