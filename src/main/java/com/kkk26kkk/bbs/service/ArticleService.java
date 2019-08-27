package com.kkk26kkk.bbs.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kkk26kkk.bbs.dao.ArticleDao;
import com.kkk26kkk.bbs.model.Article;
import com.kkk26kkk.bbs.model.ArticleDto;
import com.kkk26kkk.bbs.model.ArticleRankVo;
import com.kkk26kkk.bbs.model.ArticleReadCountVo;
import com.kkk26kkk.bbs.model.ArticleVo;
import com.kkk26kkk.bbs.model.User;
import com.kkk26kkk.common.exception.BizException;

@Service
public class ArticleService {
	@Autowired
	private ArticleDao articleDao;
	
	public Article getArticle(String articleId) {
		return articleDao.getArticle(articleId);
	}

	public void insertArticle(ArticleDto articleDto, User user) throws SQLException {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setUserId(user.getUserId());
		articleVo.setUserName(user.getUserName());
		articleVo.setTitle(articleDto.getTitle());
		articleVo.setContents(articleDto.getContents());
		articleVo.setParentId(articleDto.getParentId());
		
		String articleId = articleDao.getArticleSeqNextVal();
		articleVo.setArticleId(articleId);
		
		int resultCnt = articleDao.insertArticle(articleVo);
		if(1 != resultCnt) {
			throw new SQLException("게시글 등록을 실패 했습니다.");
		}

		if(articleDto.isNotice()) {
			this.insertNoticeArticle(articleId);
		}
	}
	
	private void insertNoticeArticle(String articleId) throws SQLException {
		int resultCnt = articleDao.insertNoticeArticle(articleId);
		
		if(1 != resultCnt) {
			throw new SQLException("공지글 등록을 실패 했습니다.");
		}
	}
	
	public void updateArticle(String articleId, ArticleDto articleDto) throws SQLException {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setArticleId(articleId);
		articleVo.setTitle(articleDto.getTitle());
		articleVo.setContents(articleDto.getContents());
		
		int resultCnt = articleDao.updateArticle(articleVo);
		if(1 != resultCnt) {
			throw new SQLException("게시글 수정을 실패 했습니다.");
		}
	}

	public void deleteArticle(String articleId) throws SQLException {
		int resultCnt = articleDao.deleteArticle(articleId);
		if(1 != resultCnt) {
			throw new SQLException("게시글 삭제를 실패했습니다.");
		}
	}
	
	public void updateArticleExample(String articleId, ArticleDto articleDto) throws BizException {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setArticleId(articleId);
		articleVo.setTitle(articleDto.getTitle());
		articleVo.setContents(articleDto.getContents());
		
		int resultCnt = articleDao.updateArticle(articleVo);
		if(1 != resultCnt) {
			throw new BizException("게시글 수정을 실패 했습니다.");
		}
	}

	public void insertReadCount(String articleId, String userId) throws SQLException {
		ArticleReadCountVo articleReadCountVo = new ArticleReadCountVo();
		articleReadCountVo.setArticleId(articleId);
		articleReadCountVo.setUserId(userId);
		
		int resultCnt = articleDao.insertReadCount(articleReadCountVo);
		if(1 != resultCnt) {
			throw new SQLException("조회수 증가 처리를 실패 했습니다.");
		}
	}

	public void saveRanking() {
		List<Article> readCountList = articleDao.getReadCountList();
		List<Article> commentCountList = articleDao.getCommentCountList();
		int listSize = readCountList.size();
		List<Article> popularityList = new ArrayList<>();
		for(int i=0; i<listSize; i++) {
			int readCountPoint = readCountList.get(i).getReadCount() * 2;
			int commentCountPoint = commentCountList.get(i).getCommentCount() * 3;
			int popularity = readCountPoint + commentCountPoint;
			
			Article article = new Article();
			article.setArticleId(readCountList.get(i).getArticleId());
			article.setPopularity(popularity);
			
			popularityList.add(article);
		}
		
		List<ArticleRankVo> articleRankVoList = new ArrayList<>();
		
		Article.ranking(readCountList, "readCount");
		Article.ranking(commentCountList, "commentCount");
		Article.ranking(popularityList, "popularity");
		
		for(int i=0; i<listSize; i++) {
			ArticleRankVo articleRankVo = new ArticleRankVo();
			articleRankVo.setArticleId(readCountList.get(i).getArticleId());
			articleRankVo.setReadCountRank(readCountList.get(i).getRank());
			articleRankVo.setCommentCountRank(commentCountList.get(i).getRank());
			articleRankVo.setPopularityRank(popularityList.get(i).getRank());
			articleRankVoList.add(articleRankVo);
		}
		
		articleDao.deleteArticleRank();
		articleDao.insertArticleRank(articleRankVoList);
	}
	
}