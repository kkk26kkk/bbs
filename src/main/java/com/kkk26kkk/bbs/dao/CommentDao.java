package com.kkk26kkk.bbs.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kkk26kkk.bbs.model.Comment;
import com.kkk26kkk.bbs.model.CommentDto;
import com.kkk26kkk.bbs.model.CommentVo;

@Repository
public class CommentDao extends BaseDao {
	@Autowired
	private SqlSessionTemplate sqlSession;

	public int insertComment(CommentVo commentVo) {
		return sqlSession.insert("insertComment", commentVo);
	}

	// TODO PageList��
	public List<Comment> getCommentList(CommentDto commentDto) { // XXX �Ķ���͸� �������� �������ϴ°�?
//		return sqlSession.selectList("getCommentList", articleId);
		return null;
	}

	public Comment getComment(int commentId) {
		return sqlSession.selectOne("getComment", commentId);
	}

	public int getCurrentCommentId() {
		return sqlSession.selectOne("getCurrentCommentId");
	}
}
